package net.thucydides.junit.runners.integration;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RetryingFailedTests {

    private static final String MAX_RETRIES = "max.retries";
    private String oldMaxRetries;

    @Before
    public void init() {
        oldMaxRetries = System.getProperty(MAX_RETRIES, "0");
    }

    @After
    public void cleanup() {
        System.setProperty("max.retries", oldMaxRetries);
    }

    @Test
    public void result_is_a_pass_despite_initial_failure() throws Exception {
        System.setProperty("max.retries", "5");
        ThucydidesRunner runner = new ThucydidesRunner(FailThenPass.class);

        CapturingNotifier notifier = new CapturingNotifier();
        runner.run(notifier);
        List<TestOutcome> outcomes = runner.getTestOutcomes();

        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(notifier.failed, is(false));
    }

    public static class FailThenPass {

        private static int failureCount;

        @Steps
        public FailThenPassSteps failThenPassSteps;

        @BeforeClass
        public static void initCounter() {
            failureCount = 0;
        }

        @Test
        public void fail_twice_then_pass() {
            failThenPassSteps.attemptSomething((failureCount++ < 2));
        }
    }

    public static class FailThenPassSteps {

        @Step
        public void attemptSomething(boolean shouldFail) {
            if (shouldFail) {
                fail();
            }
        }
    }

    static class CapturingNotifier extends RunNotifier {

        public boolean failed = false;

        @Override
        public void fireTestFailure(Failure failure) {
            failed = true;
            super.fireTestFailure(failure);
        }
    }
}
