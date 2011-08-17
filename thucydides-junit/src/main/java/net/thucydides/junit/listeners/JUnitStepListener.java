package net.thucydides.junit.listeners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Intercepts JUnit events and reports them to Thucydides.
 */
public class JUnitStepListener extends RunListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JUnitStepListener.class);

	private BaseStepListener baseStepListener;

	public JUnitStepListener(final File outputDirectory, final Pages pages) {
		baseStepListener = new BaseStepListener(outputDirectory, pages);
		StepEventBus.getEventBus().registerListener(baseStepListener);
	}

	public BaseStepListener getBaseStepListener() {
		return baseStepListener;
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		super.testRunStarted(description);

	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
	}

	private boolean firstTest = true;

	/**
	 * Called when a test starts. We also need to start the test suite the first
	 * time, as the testRunStarted() method is not invoked for some reason.
	 */
	@Override
	public void testStarted(final Description description) {
		LOGGER.debug("Junit notification: test started for {}", description.getMethodName());

		StepEventBus.getEventBus().clear();
		if (firstTest) {
			StepEventBus.getEventBus().testSuiteStarted(description.getTestClass());
			firstTest = false;
		}
		StepEventBus.getEventBus().testStarted(description.getMethodName());

	}

	@Override
	public void testFinished(final Description description) throws Exception {
		LOGGER.debug("Junit notification: test finished for {}",
				description.getMethodName());
	}

	@Override
	public void testFailure(final Failure failure) throws Exception {
		StepEventBus.getEventBus().testFailed(failure.getException());
	}

	@Override
	public void testIgnored(final Description description) throws Exception {
		StepEventBus.getEventBus().testIgnored();
	}

	public List<TestOutcome> getTestOutcomes() {
		return baseStepListener.getTestOutcomes();
	}

	public Throwable getError() {
		return baseStepListener.getTestFailureCause();
	}

	public boolean hasRecordedFailures() {
		return baseStepListener.aStepHasFailed();
	}

	public void close() {
		StepEventBus.getEventBus().dropListener(baseStepListener);

	}
}
