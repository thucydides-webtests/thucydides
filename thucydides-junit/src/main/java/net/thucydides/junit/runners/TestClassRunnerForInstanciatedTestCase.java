package net.thucydides.junit.runners;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

class TestClassRunnerForInstanciatedTestCase extends ThucydidesRunner {
    private final int parameterSetNumber;
    private final Object instanciatedTest;

    TestClassRunnerForInstanciatedTestCase(final Object instanciatedTest,
                                           final int parameterSetNumber) throws InitializationError {
        super(instanciatedTest.getClass());
        this.instanciatedTest = instanciatedTest;
        this.parameterSetNumber = parameterSetNumber;
    }

    @Override
    protected JUnitStepListener initListenersUsing(final Pages pagesObject) {
        System.out.println("TestClassRunnerForInstanciatedTestCase initListeners for " + parameterSetNumber);
        setStepListener(new ParameterizedJUnitStepListener(Configuration.loadOutputDirectoryFromSystemProperties(),
                pagesObject,
                parameterSetNumber));
        return getStepListener();
    }

    @Override
    public Object createTest() throws Exception {
        return instanciatedTest;
    }

    @Override
    protected String getName() {
        return QualifierFinder.forTestCase(instanciatedTest).getQualifier();
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        return String.format("%s[%s]", method.getName(), parameterSetNumber);
    }

    @Override
    protected Statement classBlock(final RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

}