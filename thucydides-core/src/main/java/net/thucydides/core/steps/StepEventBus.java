package net.thucydides.core.steps;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.screenshots.ScreenshotProcessor;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
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
     * There is a separate event bus for each thread.
     */
    public static synchronized StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            stepEventBusThreadLocal.set(Injectors.getInjector().getInstance(StepEventBus.class));
        }
        return stepEventBusThreadLocal.get();
    }

    private List<StepListener> registeredListeners = new ArrayList<StepListener>();
    /**
     * A reference to the base step listener, if registered.
     */
    private BaseStepListener baseStepListener;

    private TestResultTally resultTally;

    private Stack<String> stepStack = new Stack<String>();
    private Stack<Boolean> webdriverSuspensions = new Stack<Boolean>();

    private Set<StepListener> customListeners;

    private boolean stepFailed;

    private boolean pendingTest;

    private Class<?> classUnderTest;
    private Story storyUnderTest;

    private final ScreenshotProcessor screenshotProcessor;

    @Inject
    public StepEventBus(ScreenshotProcessor screenshotProcessor) {
        this.screenshotProcessor = screenshotProcessor;
    }

    /**
     * Register a listener to receive notification at different points during a test's execution.
     * If you are writing your own listener, you shouldn't need to call this method - just set up your
     * listener implementation as a service (see http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html),
     * place the listener class on the classpath and it will be detected automatically.
     */
    public StepEventBus registerListener(final StepListener listener) {
        if (!registeredListeners.contains(listener)) {
            registeredListeners.add(listener);
            if (listener.getClass().isAssignableFrom(BaseStepListener.class)) {
                baseStepListener = (BaseStepListener) listener;
            }
        }
        return this;
    }

    private BaseStepListener getBaseStepListener() {
        Preconditions.checkNotNull(baseStepListener, "No BaseStepListener has been registered");
        return baseStepListener;
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
        if (newTestName != null) {
            startSuiteForFirstTest(testClass);
            testStarted(newTestName);
        }
    }

    private void startSuiteForFirstTest(final Class<?> testClass) {
//        if ((classUnderTest == null) || (classUnderTest != testClass)) {
//            testSuiteStarted(testClass);
//        }
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

    public void testFinished() {
        screenshotProcessor.waitUntilDone();
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(outcome);
        }
        clear();
    }

    public void testFinished(TestOutcome result) {
        screenshotProcessor.waitUntilDone();
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
     */
    public void stepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : getAllListeners()) {
            stepListener.stepStarted(executedStepDescription);
        }
    }

    /**
     * Record a step that is not scheduled to be executed (e.g. a skipped or ignored step).
     */
    public void skippedStepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for(StepListener stepListener : getAllListeners()) {
            stepListener.skippedStepStarted(executedStepDescription);
        }
    }

    private void updateCurrentStepTitleTo(String updatedStepTitle) {
        getBaseStepListener().updateCurrentStepTitle(updatedStepTitle);
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

    public void lastStepFailed(final StepFailure failure) {

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
        stepPending(null);

    }

    public void stepPending(String message) {
        testPending();
        stepDone();
        getResultTally().logIgnoredTest();

        for(StepListener stepListener : getAllListeners()) {
            if (message != null) {
                stepListener.stepPending(message);
            } else {
                stepListener.stepPending();
            }
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
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        for(StepListener stepListener : getAllListeners()) {
            try {
                stepListener.testFailed(outcome, cause);
            } catch (AbstractMethodError ame) {
                LOGGER.warn("Caught abstract method error - this seems to be mostly harmless.");
            }
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

    public void testSuiteFinished() {
        for(StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteFinished();
        }
        ThucydidesWebDriverSupport.closeAllDrivers();
        storyUnderTest = null;
    }

    public void updateCurrentStepTitle(String stepTitle) {
        getBaseStepListener().updateCurrentStepTitle(stepTitle);
    }

    public void addIssuesToCurrentStory(List<String> issues) {
        baseStepListener.addIssuesToCurrentStory(issues);
    }

    public void addIssuesToCurrentTest(List<String> issues) {
        baseStepListener.getCurrentTestOutcome().addIssues(issues);
    }

    public void addTagsToCurrentTest(List<TestTag> tags) {
        baseStepListener.getCurrentTestOutcome().addTags(tags);
    }

    public void addTagsToCurrentStory(List<TestTag> tags) {
        baseStepListener.addTagsToCurrentStory(tags);
    }
}
