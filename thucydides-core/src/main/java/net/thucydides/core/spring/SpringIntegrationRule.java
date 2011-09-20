package net.thucydides.core.spring;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;


/**
 * Use the Spring test annotations in Thucydides tests.
 * @author johnsmart
 *
 */
public class SpringIntegrationRule implements MethodRule {

    private TestContextManager testContextManager;

    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        TestContextManager contextManager = getTestContextManager(method.getMethod().getDeclaringClass());
        contextManager.prepareTestInstance(target);

        return null;
    }


    protected TestContextManager getTestContextManager(Class<?> clazz) {
        if (testContextManager != null) {
            testContextManager = createTestContextManager(clazz);
        }
        return testContextManager;
    }
	/**
	 * Creates a new {@link TestContextManager}. Can be overridden by subclasses.
	 * @param clazz the Class object corresponding to the test class to be managed
	 */
	protected TestContextManager createTestContextManager(Class<?> clazz) {
		return new TestContextManager(clazz);
	}
}
