package net.thucydides.core.steps;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.webdriver.WebdriverAssertionError;

import org.junit.Ignore;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen to step results and publish notification messages.
 * The step interceptor is designed to work on a given test case or user story.
 * It logs test step results so that they can be reported on at the end of the test case.
 *
 * @author johnsmart
 */
public class StepInterceptor implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;
    private final List<StepListener> listeners;
    private final Class<? extends ScenarioSteps> testStepClass;
    private TestStepResult resultTally;
    private List<Throwable> stepExceptions;
    private Throwable error = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(StepInterceptor.class);

    public StepInterceptor(final Class<? extends ScenarioSteps> testStepClass,
                           final List<StepListener> listeners) {
        this.testStepClass = testStepClass;
        this.listeners = listeners;
        this.resultTally = new TestStepResult();
        this.stepExceptions = new ArrayList<Throwable>();
    }

    public Object intercept(final Object obj, final Method method,
                            final Object[] args, final MethodProxy proxy) throws Throwable {

        if (invokingLast(method)) {
            notifyFinished(method);
            return null;
        }

        Object result;
        if (baseClassMethod(method)) {
            result = runNormalMethod(obj, method, args, proxy);
        } else if (isATestGroup(method)) {
            notifyGroupStarted(method, args);
            result = runTestGroupStep(obj, method, args, proxy);
            notifyGroupFinished(method, args);
        } else {
            result = testStepResult(obj, method, args, proxy);
        }
        return result;

    }

    private final List<String> OBJECT_METHODS
       = Arrays.asList("toString",
            "equals",
            "hashcode",
            "clone",
            "notify",
            "notifyAll",
            "wait",
            "finalize");

    private boolean baseClassMethod(final Method method) {
        return (OBJECT_METHODS.contains(method.getName()));
    }

    private Object testStepResult(final Object obj, final Method method,
                                  final Object[] args, final MethodProxy proxy) throws Throwable {

        if (!isATestStep(method) && !shouldSkip(method)) {
            return runNormalMethod(obj, method, args, proxy);
        }

        notifyStepStarted(method, args);

        if (shouldSkip(method)) {
            notifyTestSkippedFor(method, args);
            return null;
        }

        return runTestStep(obj, method, args, proxy);

    }

    private boolean shouldSkip(final Method method) {
        return aPreviousStepHasFailed() ||  isPending(method) || isIgnored(method);
    }

    private boolean aPreviousStepHasFailed() {
        boolean aPreviousStepHasFailed = false;
        for (StepListener listener : listeners) {
            if (listener.aStepHasFailed() && !listener.isDataDriven()) {
                aPreviousStepHasFailed = true;
            }
        }
        return aPreviousStepHasFailed;
    }

    private Object runNormalMethod(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {
        LOGGER.info("Running test step " + getTestNameFrom(method, args, false));
        Object result = null;
        try {
            result = invokeMethod(obj, method, args, proxy);
        } catch (AssertionError assertionError) {
            error = assertionError;
            stepExceptions.add(assertionError);
            notifyFailureOf(method, args, assertionError);
        }
        catch (WebDriverException webdriverException) {
            error = webdriverException;
            stepExceptions.add(webdriverException);
            notifyFailureOf(method, args, webdriverException);
        }
        return result;
    }

    private Object runTestGroupStep(final Object obj, final Method method,
                                    final Object[] args, final MethodProxy proxy) throws Throwable {

        LOGGER.info("Running test step group " + getTestNameFrom(method, args, false));
        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
        } catch (AssertionError assertionError) {
            if (!stepExceptions.contains(assertionError)) {
                error = assertionError;
                stepExceptions.add(assertionError);
                notifyFailureOf(method, args, assertionError);
            }
        }
        return result;
    }

    private boolean isATestGroup(final Method method) {
        return (getTestGroupAnnotationFor(method) != null);
    }

    private StepGroup getTestGroupAnnotationFor(final Method method) {
        return method.getAnnotation(StepGroup.class);
    }

    private boolean isATestStep(final Method method) {
        Step stepAnnotation = method.getAnnotation(Step.class);
        return (stepAnnotation != null);
    }

    private boolean isIgnored(final Method method) {
        Ignore ignoreAnnotation = method.getAnnotation(Ignore.class);
        return (ignoreAnnotation != null);
    }

    private Object runTestStep(final Object obj, final Method method,
                               final Object[] args, final MethodProxy proxy) throws Throwable {
        LOGGER.info("Running test step " + getTestNameFrom(method, args, false));
        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
        } catch (AssertionError assertionError) {
            error = assertionError;
            stepExceptions.add(assertionError);
            LOGGER.debug("Addertion error caught - notifying of failure " + assertionError);
            notifyFailureOf(method, args, assertionError);
        } catch (WebDriverException webdriverException) {
            error = webdriverException;
            AssertionError webdriverAssertionError = new WebdriverAssertionError(error.getMessage(), error);
            stepExceptions.add(webdriverAssertionError);
            notifyFailureOf(method, args, webdriverAssertionError);
        }

        notifyTestFinishedFor(method, args);

        resultTally.logExecutedTest();
        LOGGER.info("Test step done: " + getTestNameFrom(method, args, false));
        return result;
    }

    private Object invokeMethod(final Object obj, final Method method,
                                final Object[] args, final MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }

    private boolean isPending(final Method method) {
        Pending pendingAnnotation = method.getAnnotation(Pending.class);
        return (pendingAnnotation != null);
    }

    private void notifyTestFinishedFor(final Method method, final Object[] args) {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));
        for (StepListener listener : listeners) {
            listener.stepFinished(description);
        }
    }

    private String getTestNameFrom(final Method method, final Object[] args) {
        return getTestNameFrom(method, args, true);
    }

    private String getTestNameFrom(final Method method, final Object[] args, final boolean addMarkup) {
        if ((args == null) || (args.length == 0)) {
            return method.getName();
        } else {
            return testNameWithArguments(method, args, addMarkup);
        }
    }

    private String testNameWithArguments(final Method method,
                                         final Object[] args, final boolean addMarkup) {
        StringBuilder testName = new StringBuilder(method.getName());
        testName.append(": ");
        if (addMarkup) {
            if (args.length == 1) {
                testName.append("<span class='single-parameter'>");
            } else {
                testName.append("<span class='parameters'>");
            }
        }
        boolean isFirst = true;
        for (Object arg : args) {
            if (!isFirst) {
                testName.append(", ");
            }
            testName.append(arg);
            isFirst = false;
        }
        if (addMarkup) {
            testName.append("</span>");
        }
        return testName.toString();
    }

    private void notifyTestSkippedFor(final Method method, final Object[] args)
            throws Exception {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));
        for (StepListener listener : listeners) {
            listener.stepIgnored(description);
        }

        resultTally.logIgnoredTest();
    }

    private void notifyFailureOf(final Method method, final Object[] args,
                                 final Throwable cause) throws Exception {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));

        StepFailure failure = new StepFailure(description, cause);

        for (StepListener listener : listeners) {
            listener.stepFailed(failure);
        }
        resultTally.logFailure(failure);
    }

    private void notifyFinished(final Method method) throws Exception {

        for (StepListener listener : listeners) {
            listener.testFinished(resultTally);
        }
    }

    private void notifyGroupStarted(final Method method, final Object[] args)
            throws Exception {

        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));
        for (StepListener listener : listeners) {
            listener.stepGroupStarted(description);
        }
    }

    private void notifyGroupFinished(final Method method, final Object[] args)
            throws Exception {
        for (StepListener listener : listeners) {
            listener.stepGroupFinished();
        }
    }

    private void notifyStepStarted(final Method method, final Object[] args) {

        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));
        StepEventBus.getEventBus().stepStarted(description);

        for (StepListener listener : listeners) {
            listener.stepStarted(description);
        }
    }

    private boolean invokingLast(final Method method) {
        return (method.getName().equals("done") || (method.getName()
                .equals("finalize")));
    }

}
