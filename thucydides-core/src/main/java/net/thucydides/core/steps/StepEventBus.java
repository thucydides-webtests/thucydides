package net.thucydides.core.steps;

import com.google.common.collect.ImmutableList;
import com.google.inject.internal.Lists;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(StepEventBus.class);

    /**
     * The event bus used to inform listening classes about when tests and test steps start and finish.
     */
    public static synchronized StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            stepEventBusThreadLocal.set(new StepEventBus());
        }
        return stepEventBusThreadLocal.get();
    }

    private List<StepListener> registeredListeners = new ArrayList<StepListener>();

    private TestResultTally resultTally;

    private Stack<String> stepStack = new Stack<String>();
    private Stack<Boolean> webdriverSuspensions = new Stack<Boolean>();

    private Set<StepListener> customListeners;

    private boolean stepFailed;

    private boolean pendingTest;

    private Class<?> classUnderTest;
    private Story storyUnderTest;

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

    public void testStarted(final String newTestName, final Story story) {
        startSuiteWithStoryForFirstTest(story);
        testStarted(newTestName);
    }

    public void testStarted(final String newTestName, final Class<?> testClass) {
        startSuiteForFirstTest(testClass);
        testStarted(newTestName);
    }

    private void startSuiteForFirstTest(final Class<?> testClass) {
        if ((classUnderTest == null) || (classUnderTest != testClass)) {
            testSuiteStarted(testClass);
        }
    }

    private void startSuiteWithStoryForFirstTest(final Story story) {
        if ((storyUnderTest == null) || (storyUnderTest != story)) {
            testSuiteStarted(story);
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
                    LOGGER.info("Registering custom listener " + listener);
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
        LOGGER.debug("Test suite started for {}", testClass);
        clear();
        updateClassUnderTest(testClass);
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(testClass);
        }
    }

    private void updateClassUnderTest(final Class<?> testClass) {
        classUnderTest = testClass;
    }


    private void updateStoryUnderTest(final Story story) {
        storyUnderTest = story;
    }

    public void testSuiteStarted(final Story story) {
        LOGGER.debug("Test suite started for story {}", story);
        updateStoryUnderTest(story);
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(story);
        }
    }

    public void clear() {
        stepStack.clear();
        clearStepFailures();
        currentTestIsNotPending();
        resultTally = null;
        classUnderTest = null;
        storyUnderTest = null;
        webdriverSuspensions.clear();
    }

    private void currentTestIsNotPending() {
        pendingTest = false;
    }

    private TestResultTally getResultTally() {
        if (resultTally == null) {
            resultTally = TestResultTally.forTestClass(classUnderTest);
        }
        return resultTally;
    }

    public void testFinished(TestOutcome result) {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(result);
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

    /**
     * Start the execution of a test step.
     * @param executedStepDescription
     */
    public void stepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepStarted(executedStepDescription);
        }
    }

    /**
     * Record a step that is not scheduled to be executed (e.g. a skipped or ignored step).
     * @param executedStepDescription
     */
    public void skippedStepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : getAllListeners()) {
            stepListener.skippedStepStarted(executedStepDescription);
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
        if (!stepStack.empty()) {
            popStep();
        }
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

    public void notifyScreenChange() {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.notifyScreenChange();
        }
    }
}
