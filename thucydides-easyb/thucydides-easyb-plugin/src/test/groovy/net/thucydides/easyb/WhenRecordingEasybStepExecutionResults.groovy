package net.thucydides.easyb;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.hamcrest.Matchers.containsInOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when
import net.thucydides.easyb.samples.SampleSteps
import net.thucydides.easyb.samples.NestedScenarioSteps;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingEasybStepExecutionResults {

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
        ThucydidesExecutionListener executionListener = new ThucydidesExecutionListener(stepListener);

        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);
    }

    @Test
    public void to_use_a_step_listener_you_need_to_instantiate_a_step_library_using_the_step_factory() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();

        assertThat(results.size(), is(1));
    }

    @Test
    public void the_step_listener_should_record_each_step_called() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getStepCount(), is(2));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
        steps.step1();
        steps.step2();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

        AcceptanceTestRun testRun = results.get(0);
        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void the_step_listener_should_record_each_step_executed_in_order() {

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);
        stepListener.testRunStarted "Test Run"
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
        stepListener.testRunStarted "Test Run"
        steps.step1();

        List<AcceptanceTestRun> results = stepListener.getTestRunResults();
        assertThat(results.size(), is(1));

    }

    @Test
    public void steps_with_nested_steps_should_be_recorded_as_step_groups() {

        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);
        stepListener.testRunStarted "Test Run"
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
        stepListener.testRunStarted "Test Run"
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
