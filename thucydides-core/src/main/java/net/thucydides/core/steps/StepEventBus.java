package net.thucydides.core.steps;

import com.google.common.collect.ImmutableList;
import com.google.inject.internal.Lists;
import net.thucydides.core.model.Story;
import sun.misc.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * An event bus for Step-related notifications.
 * Use this to integrate Thucydides listeners with testing tools.
 * You create a listener (e.g. an instance of BaseStepListener, or your own), register it using
 * 'registerListener', and then implement the various methods (testStarted(), stepStarted()). Thucydides
 * will call these events on your listener as they occur.
 *
 * You can register a new Thucydides listener by implementing the StepListener interface and
 * placing your class in the classpath. Thucydides will automatically detect the listener and add it to the
 * registered listeners. It will load custom listeners automatically when a test starts for the first time.
 *
 */
public class StepEventBus {

    private static ThreadLocal<StepEventBus> stepEventBusThreadLocal = new ThreadLocal<StepEventBus>();
    private static final String CORE_THUCYDIDES_PACKAGE = "net.thucydides.core";

    /**
     * The event bus used to inform listening classes about when tests and test steps start and finish.
     */
    public static StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            stepEventBusThreadLocal.set(new StepEventBus());
        }
        return stepEventBusThreadLocal.get();
    }

    private List<StepListener> registeredListeners = new ArrayList<StepListener>();

    private TestStepResult resultTally;

    private Stack<String> stepStack = new Stack<String>();
    private Stack<Boolean> webdriverSuspensions = new Stack<Boolean>();

    private Set<StepListener> customListeners;

    private boolean stepFailed;

    private boolean pendingTest;

    /**
     * Register a listener to receive notification at different points during a test's execution.
     * If you are writing your own listener, you shouldn't need to call this method - just set up your
     * listener implementation as a service (see http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html),
     * place the listener class on the classpath and it will be detected automatically.
     */
    public StepEventBus registerListener(final StepListener listener) {
        registeredListeners.add(listener);
        return this;
    }

    public void testStarted(final String testName) {

        clear();

        for(StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName);
        }
    }

    protected List<StepListener> getAllListeners() {
        List<StepListener> allListeners = Lists.newArrayList(registeredListeners);
        allListeners.addAll(getCustomListeners());
        return ImmutableList.copyOf(allListeners);
    }

    private Set<StepListener> getCustomListeners() {

        if (customListeners == null) {
            customListeners = Collections.synchronizedSet(new HashSet<StepListener>());
            Iterator<?> listenerImplementations = Service.providers(StepListener.class);

            while (listenerImplementations.hasNext()) {
                StepListener listener = (StepListener) listenerImplementations.next();
                if (!isACore(listener)) {
                    customListeners.add(listener);
                }
            }
        }
        return customListeners;
    }

    private boolean isACore(final StepListener listener) {
        return listener.getClass().getPackage().getName().startsWith(CORE_THUCYDIDES_PACKAGE);
    }

    public void testSuiteStarted(final Class<?> testClass) {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(testClass);
        }
    }

    public void testSuiteStarted(final Story story) {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(story);
        }
    }

    public void clear() {
        stepStack.clear();
        clearStepFailures();
        currentTestIsNotPending();
        this.resultTally = new TestStepResult();
        webdriverSuspensions.clear();
    }

    private void currentTestIsNotPending() {
        pendingTest = false;
    }

    private TestStepResult getResultTally() {
        if (resultTally == null) {
            resultTally = new TestStepResult();
        }
        return resultTally;
    }

    public void testFinished() {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(getResultTally());
        }
        clear();
    }

    private void pushStep(String stepName) {
        stepStack.push(stepName);
    }

    private void popStep() {
        stepStack.pop();
    }

    private void clearStepFailures() {
        stepFailed = false;
    }

    public boolean aStepInTheCurrentTestHasFailed() {
        return stepFailed;
    }

    public boolean isCurrentTestDataDriven() {
        return DataDrivenStep.inProgress();
    }

    public void stepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepStarted(executedStepDescription);
        }
    }

    public void stepFinished() {
        stepDone();
        getResultTally().logExecutedTest();
        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepFinished();
        }
    }

    private void stepDone() {
        popStep();
    }

    public void stepFailed(final StepFailure failure) {

        stepDone();
        getResultTally().logFailure(failure);

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepFailed(failure);
        }
        stepFailed = true;
    }

    public void stepIgnored() {

        stepDone();
        getResultTally().logIgnoredTest();

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepIgnored();
        }
    }

    public void stepPending() {

        stepDone();
        getResultTally().logIgnoredTest();

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepPending();
        }
    }

    public void dropListener(final StepListener stepListener) {
        registeredListeners.remove(stepListener);
    }

    public void dropAllListeners() {
        registeredListeners.clear();
    }

    public boolean webdriverCallsAreSuspended() {
        return aStepInTheCurrentTestHasFailed() || !webdriverSuspensions.isEmpty();
    }

    public void reenableWebdriverCalls() {
        webdriverSuspensions.pop();
    }

    public void temporarilySuspendWebdriverCalls() {
        webdriverSuspensions.push(true);
    }

    /**
     * The test failed, but not during the execution of a step.
     * @param cause the underlying cause of the failure.
     */
    public void testFailed(final Throwable cause) {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testFailed(cause);
        }
    }

    /**
     * Mark the current test method as pending.
     * The test will stil be executed to record the steps, but any webdriver calls will be skipped.
     */
    public void testPending() {
        pendingTest = true;
    }

    public boolean currentTestIsPending() {
        return pendingTest;
    }

    public void testIgnored() {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testIgnored();
        }
    }

    public boolean areStepsRunning() {
        return !stepStack.isEmpty();
    }
}
