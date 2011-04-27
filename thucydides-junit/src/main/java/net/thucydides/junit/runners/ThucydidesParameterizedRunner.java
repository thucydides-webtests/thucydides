package net.thucydides.junit.runners;

import org.junit.runners.Suite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class ThucydidesParameterizedRunner extends Suite {
	/**
	 * Annotation for a method which provides parameters to be injected into the
	 * test class constructor by <code>Parameterized</code>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface TestData {
	}

	private class TestClassRunnerForParameters extends ThucydidesRunner {
		private final int fParameterSetNumber;

		private final List<Object[]> fParameterList;

		TestClassRunnerForParameters(Class<?> type,
									 List<Object[]> parameterList,
									 int i) throws InitializationError {
			super(type);
			fParameterList= parameterList;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(computeParams());
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod(getTestClass()).getName()));
			}
		}

		@Override
		protected String getName() {
            String firstParameter = fParameterList.get(fParameterSetNumber)[0].toString();
			return String.format("[%s]", firstParameter);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	private final ArrayList<Runner> runners= new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public ThucydidesParameterizedRunner(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		List<Object[]> parametersList = getParametersList(getTestClass());
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
					parametersList, i));
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass)
			throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(
				null);
	}

	private FrameworkMethod getParametersMethod(TestClass testClass)
			throws Exception {
		List<FrameworkMethod> methods= testClass
				.getAnnotatedMethods(TestData.class);
		for (FrameworkMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ testClass.getName());
	}

}
