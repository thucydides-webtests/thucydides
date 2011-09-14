package net.thucydides.core.steps;

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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Listen to step results and publish notification messages.
 * The step interceptor is designed to work on a given test case or user story.
 * It logs test step results so that they can be reported on at the end of the test case.
 *
 * TODO: Remove the stepExceptions variable
 * @author johnsmart
 */
public class StepInterceptor implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;
    private final Class<?> testStepClass;
    private List<Throwable> stepExceptions;
    private Throwable error = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(StepInterceptor.class);

    public StepInterceptor(final Class<?> testStepClass) {
        this.testStepClass = testStepClass;
        this.stepExceptions = new ArrayList<Throwable>();
    }

    public Object intercept(final Object obj, final Method method,
                            final Object[] args, final MethodProxy proxy) throws Throwable {

        Object result;
        if (baseClassMethod(method)) {
            result = runNormalMethod(obj, method, args, proxy);
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

        if (!isATestStep(method)) {
            return runNormalMethod(obj, method, args, proxy);
        }

        notifyStepStarted(method, args);

        if (shouldSkip(method)) {
            Object skippedReturnObject = runSkippedMethod(obj, method, args, proxy);
            notifyTestSkippedFor(method, args);
            return appropriateReturnObject(skippedReturnObject, obj, method);
        }

        return runTestStep(obj, method, args, proxy);

    }

    private Object runSkippedMethod(Object obj, Method method, Object[] args, MethodProxy proxy) {
        LOGGER.info("Running test step " + getTestNameFrom(method, args, false));
        Object result = null;
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();
        try {
            result = invokeMethod(obj, method, args, proxy);
        } catch (Throwable anyException) {
            LOGGER.trace("Ignoring exception thrown during a skipped test", anyException);
        }
        StepEventBus.getEventBus().reenableWebdriverCalls();
        return result;
    }

    Object appropriateReturnObject(final Object returnedValue, final Object obj, final Method method) {
        if (returnedValue != null) {
            return returnedValue;
        } else {
            return appropriateReturnObject(obj, method);
        }
    }

    Object appropriateReturnObject(final Object obj, final Method method) {
        if (method.getReturnType().isAssignableFrom(obj.getClass())) {
            return obj;
        } else {
            return null;
        }
    }

    private boolean shouldSkip(final Method step) {
        return aPreviousStepHasFailed() ||  testIsPending() || isPending(step) || isIgnored(step);
    }

    private boolean testIsPending() {
        return StepEventBus.getEventBus().currentTestIsPending();
    }

    private boolean aPreviousStepHasFailed() {
        boolean aPreviousStepHasFailed = false;
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()
                && !StepEventBus.getEventBus().isCurrentTestDataDriven()) {
            aPreviousStepHasFailed = true;
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
            notifyOfTestFailure(method, args, assertionError);
        }
        catch (WebDriverException webdriverException) {
            error = webdriverException;
            stepExceptions.add(webdriverException);
            notifyOfTestFailure(method, args, webdriverException);
        }
        return result;
    }

    private StepGroup getTestGroupAnnotationFor(final Method method) {
        return method.getAnnotation(StepGroup.class);
    }

    private Step getTestAnnotationFor(final Method method) {
        return method.getAnnotation(Step.class);
    }

    private boolean isATestStep(final Method method) {
        return (getTestAnnotationFor(method) != null) || (getTestGroupAnnotationFor(method) != null);
    }

    private boolean isIgnored(final Method method) {
        return (method.getAnnotation(Ignore.class) != null);
    }

    private Object runTestStep(final Object obj, final Method method,
                               final Object[] args, final MethodProxy proxy) throws Throwable {
        LOGGER.info("Running test step " + getTestNameFrom(method, args, false));
        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
            notifyStepFinishedFor(method, args);
        } catch (AssertionError assertionError) {
            error = assertionError;
            stepExceptions.add(assertionError);
            LOGGER.debug("Addertion error caught - notifying of failure " + assertionError);
            notifyOfStepFailure(method, args, assertionError);
            return appropriateReturnObject(obj, method);
        } catch (WebDriverException webdriverException) {
            error = webdriverException;
            AssertionError webdriverAssertionError = new WebdriverAssertionError(error.getMessage(), error);
            stepExceptions.add(webdriverAssertionError);
            notifyOfStepFailure(method, args, webdriverAssertionError);
        } catch (Throwable generalException) {
            error = generalException;
            AssertionError essertionError = new WebdriverAssertionError(error.getMessage(), error);
            stepExceptions.add(essertionError);
            notifyOfStepFailure(method, args, essertionError);
        }

        LOGGER.info("Test step done: " + getTestNameFrom(method, args, false));
        return result;
    }

    private Object invokeMethod(final Object obj, final Method method,
                                final Object[] args, final MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }

    private boolean isPending(final Method method) {
        return (method.getAnnotation(Pending.class) != null);
    }

    private void notifyStepFinishedFor(final Method method, final Object[] args) {
        StepEventBus.getEventBus().stepFinished();
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
        if (isPending(method)) {
            StepEventBus.getEventBus().stepPending();
        } else {
            StepEventBus.getEventBus().stepIgnored();
        }
    }

    private void notifyOfStepFailure(final Method method, final Object[] args,
                                     final Throwable cause) throws Exception {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));

        StepFailure failure = new StepFailure(description, cause);
        StepEventBus.getEventBus().stepFailed(failure);
    }

    private void notifyOfTestFailure(final Method method, final Object[] args,
                                     final Throwable cause) throws Exception {
        StepEventBus.getEventBus().testFailed(cause);
    }

    private void notifyStepStarted(final Method method, final Object[] args) {

        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args));
        StepEventBus.getEventBus().stepStarted(description);
    }
}
