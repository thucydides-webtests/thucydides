package net.thucydides.core.steps;


import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;

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
     * Start a test run using a specific story, without a corresponding Java class.
     */
    void testSuiteStarted(final Story story);

    /**
     * A test with a given name has started.
     */
    void testStarted(final String description);

    /**
     * Called when a test finishes.
     *
     * @param result
     */
    void testFinished(final TestOutcome result);

    /**
     * Called when a test step is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    void stepStarted(final ExecutedStepDescription description);

    /**
     * Called when a test step fails.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    void stepFailed(final StepFailure failure);

    /**
     * Called when a step will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     */
    void stepIgnored();

    /**
     * The step is marked as pending.
     */
    void stepPending();

    /**
     * Called when an test step has finished successfully
     */
    void stepFinished();

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
