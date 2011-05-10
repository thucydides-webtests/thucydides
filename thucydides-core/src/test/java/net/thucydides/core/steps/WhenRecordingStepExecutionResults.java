package net.thucydides.core.steps;

import net.thucydides.core.model.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.samples.FlatScenarioSteps;
import net.thucydides.core.steps.samples.NestedScenarioSteps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import static net.thucydides.core.hamcrest.Matchers.containsInOrder;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingStepExecutionResults {

    StepListener stepListener;

    StepFactory stepFactory;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;

    File screenshot;

    @Mock
    FirefoxDriver driver;

    @Mock
    Pages pages;

    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");
        screenshot = temporaryFolder.newFile("screenshot.jpg");
        stepListener = new BaseStepListener(driver, outputDirectory);
        stepListener.testRunStarted("Test Run");
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);
    }

    @Test
    public void to_use_a_step_listener_you_need_to_instantiate_a_step_library_using_the_step_factory() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();

        assertThat(results.size(), is(1));
    }

    @Test
    public void the_step_listener_should_record_each_step_called() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getStepCount(), is(2));
    }

    @Test
    public void the_step_listener_be_informed_of_the_story_name() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);

        stepListener.testRunStarted(ExecutedStepDescription.withTitle("Test Run"));
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getTitle(), is("Test Run"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void the_step_listener_should_record_each_step_executed_in_order() {

        FlatScenarioSteps steps = (FlatScenarioSteps) stepFactory.newSteps(FlatScenarioSteps.class);
        steps.step1();
        steps.step2();

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        List<String> executedStepNames = namesFrom(executedSteps);

        assertThat(executedStepNames, containsInOrder("step1", "step2"));

    }

    @Test
    public void any_nested_steps_should_also_be_executed() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

    }

    @Test
    public void steps_with_nested_steps_should_be_recorded_as_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();
        assertThat(executedSteps.get(0), instanceOf(TestStepGroup.class));
        assertThat(executedSteps.get(1), instanceOf(TestStepGroup.class));
    }

    @Test
    public void nested_steps_should_be_recorded_inside_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = firstTestResultRecordedIn(stepListener.getTestRunResults());
        List<TestStep> executedSteps = testRun.getTestSteps();

        TestStepGroup topLevelStepGroup = (TestStepGroup) executedSteps.get(0);
        List<String> executedStepNames = namesFrom(topLevelStepGroup.getSteps());

        assertThat(executedStepNames, containsInOrder("step1", "step2", "step3"));
    }

    private AcceptanceTestRun firstTestResultRecordedIn(List<AcceptanceTestRun> testRunResults) {
        return stepListener.getTestRunResults().get(0);
    }

    private List<String> namesFrom(List<TestStep> testSteps) {
        List<String> descriptions = new ArrayList<String>();
        for (TestStep step : testSteps) {
            descriptions.add(step.getDescription());
        }
        return descriptions;
    }


}
