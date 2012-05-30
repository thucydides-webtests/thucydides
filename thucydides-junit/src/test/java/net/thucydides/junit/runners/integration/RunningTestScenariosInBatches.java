package net.thucydides.junit.runners.integration;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.SampleNonWebSteps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class RunningTestScenariosInBatches {


    static List<Integer> executedTests = new CopyOnWriteArrayList<Integer>();
    static AtomicInteger testCount = new AtomicInteger(0);

    private void runTestCases(SystemVariableBasedBatchManager batchManager) {
        try {
            new ThucydidesRunner(TestScenario1.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario2.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario3.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario4.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario5.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario6.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario7.class, batchManager).run(new RunNotifier());
            new ThucydidesRunner(TestScenario8.class, batchManager).run(new RunNotifier());
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
        }
    }

    @Before
    public void clearCounters() {
        executedTests.clear();
    }

    @Test
    public void the_thread_for_batch_1_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(1));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(3));
        assertThat(executedTests, hasItems(1, 4, 7));
    }

    @Test
    public void the_thread_for_batch_2_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(2));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(3));
        assertThat(executedTests, hasItems(2, 5, 8));
    }


    @Test
    public void the_thread_for_batch_3_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(3));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(2));
        assertThat(executedTests, hasItems(3, 6));
    }
    // TEST CLASSES USED IN THE MAIN TESTS.

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario1 {

        int testNumber;

        public TestScenario1() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(1);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario2 {

        int testNumber;

        public TestScenario2() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(2);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario3 {

        int testNumber;

        public TestScenario3() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(3);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }


    @RunWith(ThucydidesRunner.class)
    public static class TestScenario4 {

        int testNumber;

        public TestScenario4() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(4);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }



    @RunWith(ThucydidesRunner.class)
    public static class TestScenario5 {

        int testNumber;

        public TestScenario5() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(5);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario6 {

        int testNumber;

        public TestScenario6() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(6);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario7 {

        int testNumber;

        public TestScenario7() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(7);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(ThucydidesRunner.class)
    public static class TestScenario8 {

        int testNumber;

        public TestScenario8() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(8);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }
}

