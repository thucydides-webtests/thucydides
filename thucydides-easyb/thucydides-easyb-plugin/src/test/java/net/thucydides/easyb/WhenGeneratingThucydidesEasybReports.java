package net.thucydides.easyb;


import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.ConsoleStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.easyb.samples.NestedScenarioSteps;
import net.thucydides.easyb.samples.SampleSteps;
import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.junit.After;
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
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static org.easyb.util.BehaviorStepType.GIVEN;
import static org.easyb.util.BehaviorStepType.SCENARIO;
import static org.easyb.util.BehaviorStepType.STORY;
import static org.easyb.util.BehaviorStepType.THEN;
import static org.easyb.util.BehaviorStepType.WHEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenGeneratingThucydidesEasybReports {


    BaseStepListener stepListener;
    StepListener textStepListener;

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
    private final BehaviorStep storyWithIssues = new BehaviorStep(STORY, "A User Story");
    private final BehaviorStep scenario = new BehaviorStep(SCENARIO, "Test Scenario");
    private final BehaviorStep scenarioWithIssues = new BehaviorStep(SCENARIO, "Test Scenario (#{ISSUE-1})");
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
        screenshot = screenshotFileFrom("google_page1.jpg");
        stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        stepListener.setDriver(driver);
        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);
        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");

        stepFactory = new StepFactory(pages);

        reportService = new ReportService(outputDirectory,getDefaultReporters());

        executionListener = new ThucydidesExecutionListener();
        textStepListener = new ConsoleStepListener();
        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(stepListener);
        StepEventBus.getEventBus().registerListener(textStepListener);
    }

    @After
    public void unregisterListener() {
        StepEventBus.getEventBus().dropListener(stepListener);
    }


    @Test
    public void an_easyb_scenario_should_run_the_steps() {

        run_story_with_one_scenario();

        String testOutcome = textStepListener.toString();
        String expectedOutcome = "TEST Test Scenario\n" +
                                "-Given a condition\n" +
                                "--step1\n" +
                                "----> STEP DONE\n";

        System.out.println(testOutcome);
        assertThat(testOutcome, is(expectedOutcome));
    }

    @Test
    public void an_easyb_scenario_with_several_clauses_should_nest_the_steps() {

        run_story_with_given_when_then();


        String testOutcome = textStepListener.toString();
        String expectedOutcome = "TEST Test Scenario\n" +
                                "-Given a condition\n" +
                                "--step1\n" +
                                "----> STEP DONE\n" +
                                "--When an action\n" +
                                "---step2\n" +
                                "-----> STEP DONE\n" +
                                "---Then an outcome\n" +
                                "----step2\n" +
                                "------> STEP DONE\n";

        assertThat(testOutcome, is(expectedOutcome));
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
    public void an_easyb_scenario_with_nested_steps_should_generate_a_report_with_nested_steps() {

        run_story_with_nested_steps();

        stepListener.getTestOutcomes().get(0);

        reportService.generateReportsFor(stepListener.getTestOutcomes());
    }

    @Test
    public void the_report_service_should_remove_accolades_from_titles_for_tests_with_issues() {

        run_story_with_issues();

        String testOutcome = textStepListener.toString();
        String expectedOutcome = "TEST Test Scenario (#ISSUE-1)\n" +
                                "-Given a condition\n" +
                                "--step1\n" +
                                "----> STEP DONE\n";
        assertThat(testOutcome, is(expectedOutcome));
    }


    @Test
    public void the_test_outcome_should_find_contained_issues() {

        run_story_with_issues();

        assertThat(stepListener.getTestOutcomes().get(0).getIssues(), hasItem("#ISSUE-1"));
    }

    @Test
    public void the_report_service_should_generate_reports_for_successful_tests() {

        run_story_with_one_scenario();

        stepListener.getTestOutcomes().get(0);

        reportService.generateReportsFor(stepListener.getTestOutcomes());

    }


    @Test
    public void an_easyb_scenario_with_a_failing_step_should_fail() {

        run_story_with_a_failing_scenario();

        TestOutcome testOutcome = stepListener.getTestOutcomes().get(0);

        assertThat(testOutcome.getResult(), is(FAILURE));
    }


    @Test
    public void the_report_service_should_generate_reports_for_failing_tests() {

        run_story_with_a_failing_scenario();

        stepListener.getTestOutcomes().get(0);

        reportService.generateReportsFor(stepListener.getTestOutcomes());

    }

    @Test
    public void an_easyb_scenario_with_a_failing_step_should_skip_subsequent_steps() {

        run_story_with_a_failing_scenario();

        TestOutcome testOutcome = stepListener.getTestOutcomes().get(0);

        TestStep given = testOutcome.getTestSteps().get(0);

        assertThat(given.getChildren().size(), is(3));
        assertThat(given.getChildren().get(2).getResult(), is(SKIPPED));
    }

    @Test
    public void an_easyb_scenario_with_a_pending_step_should_be_pending() {

        run_story_with_a_pending_scenario();

        TestOutcome testOutcome = stepListener.getTestOutcomes().get(0);

        assertThat(testOutcome.getResult(), is(PENDING));
    }

    @Test
    public void an_easyb_scenario_should_take_screenshots_for_each_step_including_the_given_when_thens() {

        run_story_with_one_scenario();

        stepListener.getTestOutcomes().get(0);

        verify(driver, times(2)).getScreenshotAs((OutputType<?>) anyObject());
    }



    private void run_story_with_one_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_given_when_then() {
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopStep();
        executionListener.startStep(when);
        steps.step2();
        executionListener.stopStep();
        executionListener.startStep(then);
        steps.step2();
        executionListener.stopStep();
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_issues() {
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
        executionListener.startBehavior(behavior);
        executionListener.startStep(storyWithIssues);
        executionListener.startStep(scenarioWithIssues);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_nested_steps() {
        NestedScenarioSteps steps = (NestedScenarioSteps) stepFactory.getStepLibraryFor(NestedScenarioSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
        executionListener.startBehavior(behavior);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        steps.step2();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_a_failing_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
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
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        steps.failingStep();
        steps.step3();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
        executionListener.completeTesting();
    }

    private void run_story_with_a_pending_scenario() {
        SampleSteps steps = (SampleSteps) stepFactory.getStepLibraryFor(SampleSteps.class);

        StepEventBus.getEventBus().testSuiteStarted(Story.withId("storyId","Story name"));
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

    private File screenshotFileFrom(final String screenshot) {
        URL sourcePath = getClass().getResource(screenshot);
        if (sourcePath != null) {
            return new File(sourcePath.getPath());
        } else {
            return null;
        }
    }

    public Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLTestOutcomeReporter(),
        new HtmlAcceptanceTestReporter());
    }

}
