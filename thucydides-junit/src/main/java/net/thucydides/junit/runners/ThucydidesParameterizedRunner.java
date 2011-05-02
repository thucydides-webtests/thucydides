package net.thucydides.junit.runners;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import org.apache.commons.lang.StringUtils;
import org.junit.runners.Suite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;

public class ThucydidesParameterizedRunner extends Suite {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

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
			} catch (ClassCastException cause) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(),
                        getParametersMethod(getTestClass()).getName()),
                        cause);
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
		protected void validateConstructor(final List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(final RunNotifier notifier) {
			return childrenInvoker(notifier);
		}

	}

	private final List<Runner> runners= new ArrayList<Runner>();

    /**
     * Only used for testing.
     */
    public ThucydidesParameterizedRunner(final Class<?> klass,
                                         final WebDriverFactory webDriverFactory) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        
        if (runTestsInParallelFor(klass)) {
            scheduleParallelTestRunsFor(klass);
        }

        buildTestRunnersForEachDataSetUsing(webDriverFactory);
    }

    private void scheduleParallelTestRunsFor(final Class<?> klass) {
        setScheduler(new ParameterizedRunnerScheduler(klass, getThreadCountFor(klass)));
    }

    protected boolean runTestsInParallelFor(final Class<?> klass) {
        return (klass.getAnnotation(Concurrent.class) != null);
    }

    protected int getThreadCountFor(final Class<?> klass) {
        Concurrent concurrent = klass.getAnnotation(Concurrent.class);
        String threadValue = concurrent.threads();
        int threads =  (AVAILABLE_PROCESSORS * 2);
        if (StringUtils.isNotEmpty(threadValue)) {
            if (StringUtils.isNumeric(threadValue)) {
                threads = Integer.valueOf(threadValue);
            } else if (threadValue.endsWith("x")) {
                threads = getRelativeThreadCount(threadValue);
            }

        }
        return threads;
    }

    private int getRelativeThreadCount(final String threadValue) {
        try {
            String threadCount = threadValue.substring(0,threadValue.length() - 1);
            return Integer.valueOf(threadCount) * AVAILABLE_PROCESSORS;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal thread value: " + threadValue);
        }
    }

    private void buildTestRunnersForEachDataSetUsing(final WebDriverFactory webDriverFactory) throws Throwable {
        List<Object[]> parametersList = getParametersList(getTestClass());
        for (int i= 0; i < parametersList.size(); i++) {
            Class testClass = getTestClass().getJavaClass();
            ThucydidesRunner runner = new TestClassRunnerForParameters(testClass, parametersList, i);
            runner.useQualifier(from(parametersList.get(i)));
            overrideWebdriverFactoryIfProvided(runner, webDriverFactory);
            runners.add(runner);
        }
    }

    /**
     * If the test class has a static method called
     * @param testData
     * @return
     */
    private String from(final Object[] testData) {
        StringBuffer testDataQualifier = new StringBuffer();
        boolean firstEntry = true;
        for(Object testDataValue : testData) {
            if (!firstEntry) {
                testDataQualifier.append("_");
            }
            testDataQualifier.append(testDataValue);
            firstEntry = false;
        }
        return testDataQualifier.toString();
    }



    /**
	 * Only called reflectively. Do not use programmatically.
	 */
	public ThucydidesParameterizedRunner(final Class<?> klass) throws Throwable {
        this(klass, null);
	}

    private void overrideWebdriverFactoryIfProvided(final ThucydidesRunner runner,final  WebDriverFactory webDriverFactory) {
        if (webDriverFactory != null) {
            runner.setWebDriverFactory(webDriverFactory);
        }
    }


	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

    public List<AcceptanceTestRun> getAcceptanceTestRuns() {
        List<AcceptanceTestRun> testRuns = new ArrayList<AcceptanceTestRun>();
        for (Runner runner : runners) {
            testRuns.addAll(((ThucydidesRunner) runner).getAcceptanceTestRuns());
        }
        return testRuns;
    }

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(final TestClass klass) throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(null);
	}

	private FrameworkMethod getParametersMethod(final TestClass testClass) throws Exception {
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
