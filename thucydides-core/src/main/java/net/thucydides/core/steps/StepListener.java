package net.thucydides.core.steps;


import java.util.List;

import net.thucydides.core.model.TestOutcome;

import org.openqa.selenium.WebDriver;

/**
 * Represents a class interested in knowing about test execution flow and results.
 */
public interface StepListener {

    /**
     * Start a test run using a test case or a user story.
     * For JUnit tests, the test case should be provided. The test case should be annotated with the
     * Story annotation to indicate what user story it tests. Otherwise, the test case itself will
     * be treated as a user story.
     * For easyb stories, the story class can be provided directly.
     */
    void testSuiteStarted(final Class<?> storyClass);

    /**
     * A test with a given name has started.
     */
    void testStarted(final String description);

    /**
     * Called when all tests have finished.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    void testFinished(TestStepResult result);

    /**
     * Called when a test step is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    void stepStarted(ExecutedStepDescription description);

    /**
     * Called when an test step has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    void stepFinished(ExecutedStepDescription description);

    /**
     * Called when a test step fails.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    void stepFailed(StepFailure failure);

    /**
     * Called when a step will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    void stepIgnored(ExecutedStepDescription description);

    /**
     * The step is marked as pending
     */
    void stepPending();

    /**
     * A step listener should be able to return a set of test results at the end of the test run.
     */
    List<TestOutcome> getTestOutcomes();

    /**
     * Used to update the webdriver driver for screenshots if a listener is reused between scenarios.
     */
    void setDriver(final WebDriver driver);

    /**
     * The currently-used WebDriver instance for these tests.
     */
    WebDriver getDriver();

    /**
     *  Should return true if a step failure has been logged.
     *  We need to share this information if multiple step libraries are used.
     */
    boolean aStepHasFailed();

    /**
     * If a test failed, what was the error.
     */
    Throwable getTestFailureCause();

    /**
     * The test failed, but not while executing a step.
     * @param cause
     */
    void testFailed(final Throwable cause);

    /**
     * The test as a whole was skipped or ignored.
     */
    void testIgnored();
}
