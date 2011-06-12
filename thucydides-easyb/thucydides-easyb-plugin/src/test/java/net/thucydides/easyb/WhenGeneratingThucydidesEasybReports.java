package net.thucydides.easyb;


import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.easyb.samples.NestedScenarioSteps;
import net.thucydides.easyb.samples.SampleSteps;
import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.result.Result;
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
import java.util.Arrays;
import java.util.Collection;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static org.easyb.util.BehaviorStepType.GIVEN;
import static org.easyb.util.BehaviorStepType.SCENARIO;
import static org.easyb.util.BehaviorStepType.STORY;
import static org.easyb.util.BehaviorStepType.THEN;
import static org.easyb.util.BehaviorStepType.WHEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenGeneratingThucydidesEasybReports {


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

    private final BehaviorStep story = new BehaviorStep(STORY, "A User Story");
    private final BehaviorStep scenario = new BehaviorStep(SCENARIO, "Test Scenario");
    private final BehaviorStep given = new BehaviorStep(GIVEN, "a condition");
    private final BehaviorStep when = new BehaviorStep(WHEN, "an action");
    private final BehaviorStep then = new BehaviorStep(THEN, "an outcome");

    ReportService reportService;

    @Mock
    private Behavior behavior;

    private ThucydidesExecutionListener executionListener;


    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");
        screenshot = temporaryFolder.newFile("screenshot.jpg");
        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        stepListener.setDriver(driver);
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);

        reportService = new ReportService(outputDirectory,getDefaultReporters());

        executionListener = new ThucydidesExecutionListener(stepListener);

    }

    @Test
    public void an_easyb_scenario_should_run_the_steps() {

        run_story_with_one_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        assertThat(testOutcome.getResult(), is(SUCCESS));
        assertThat(testOutcome.getTestSteps().size(), is(1));
    }

    @Test
    public void the_report_service_should_assign_any_qualifiers_to_its_report_listeners() {

        AcceptanceTestReporter mockReport = mock(AcceptanceTestReporter.class);

        Collection<AcceptanceTestReporter> reports = Arrays.asList(mockReport);
        ReportService reportService = new ReportService(outputDirectory,reports);

        reportService.useQualifier("datavalue_1");

        verify(mockReport).setQualifier("datavalue_1");
    }

    @Test
    public void an_easyb_scenario_should_run_the_steps_with_nested_steps() {

        run_story_with_nested_steps();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        assertThat(testOutcome.getResult(), is(SUCCESS));
        TestStepGroup scenario = (TestStepGroup) testOutcome.getTestSteps().get(0);
        TestStepGroup given = (TestStepGroup) scenario.getSteps().get(0);

        assertThat(given.getSteps().size(), is(2));

        TestStepGroup step = (TestStepGroup) given.getSteps().get(0);
        assertThat(step.getSteps().size(), is(3));

    }

    @Test
    public void an_easyb_scenario_with_nested_steps_should_generate_a_report_with_nested_steps() {

        run_story_with_nested_steps();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        reportService.generateReportsFor(stepListener.getTestRunResults());
    }

    @Test
    public void the_report_service_should_generate_reports_for_successful_tests() {

        run_story_with_one_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        reportService.generateReportsFor(stepListener.getTestRunResults());

    }

    public Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLTestOutcomeReporter(),
        new HtmlAcceptanceTestReporter());
    }

    @Test
    public void an_easyb_scenario_with_a_failing_step_should_fail() {

        run_story_with_a_failing_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        assertThat(testOutcome.getResult(), is(FAILURE));
    }


    @Test
    public void the_report_service_should_generate_reports_for_failing_tests() {

        run_story_with_a_failing_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        reportService.generateReportsFor(stepListener.getTestRunResults());

    }

    @Test
    public void an_easyb_scenario_with_a_failing_step_should_skip_subsequent_steps() {

        run_story_with_a_failing_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        TestStepGroup scenario = (TestStepGroup) testOutcome.getTestSteps().get(0);
        TestStepGroup given = (TestStepGroup) scenario.getSteps().get(0);

        assertThat(given.getSteps().size(), is(3));
        assertThat(given.getSteps().get(2).getResult(), is(SKIPPED));
    }

    @Test
    public void an_easyb_scenario_with_a_failing_easyb_clause_should_skip_subsequent_steps() {

        run_story_with_a_failing_easyb_clause();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        TestStepGroup scenario = (TestStepGroup) testOutcome.getTestSteps().get(0);
        TestStepGroup given = (TestStepGroup) scenario.getSteps().get(0);
        TestStepGroup when = (TestStepGroup) scenario.getSteps().get(1);
        TestStepGroup then = (TestStepGroup) scenario.getSteps().get(2);

        assertThat(given.getResult(), is(SUCCESS));
        assertThat(when.getResult(), is(FAILURE));
        assertThat(then.getResult(), is(SKIPPED));
    }


    @Test
    public void an_easyb_scenario_with_a_pending_step_should_be_pending() {

        run_story_with_a_pending_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        assertThat(testOutcome.getResult(), is(PENDING));
    }

    @Test
    public void an_easyb_scenario_should_take_screenshots_for_each_step_including_the_given_when_thens() {

        run_story_with_one_scenario();

        TestOutcome testOutcome = stepListener.getTestRunResults().get(0);

        verify(driver, times(3)).getScreenshotAs((OutputType<?>) anyObject());
    }



    private void run_story_with_one_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);

        stepListener.testRunStarted("Test Run");
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_nested_steps() {
        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.newSteps(NestedScenarioSteps.class);

        stepListener.testRunStarted("Test Run");
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        steps.step2();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_a_failing_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);

        stepListener.testRunStarted("Test Run");
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        steps.failingStep();
        steps.step2();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_a_failing_easyb_clause() {
        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);

        stepListener.testRunStarted("Test Run");
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopStep(); // given
        executionListener.startStep(when);
        executionListener.gotResult(new Result(new Exception("When clause failed")));
        executionListener.stopStep(); // when
        executionListener.startStep(then);
        executionListener.stopStep(); // when
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_a_pending_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);

        stepListener.testRunStarted("Test Run");
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        steps.pendingStep();
        steps.step2();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }
}
