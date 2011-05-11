package net.thucydides.easyb;


import junit.textui.TestRunner;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;

import static org.easyb.util.BehaviorStepType.*;
import static org.mockito.Mockito.*;

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


    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");
        screenshot = temporaryFolder.newFile("screenshot.jpg");
        stepListener = new BaseStepListener(driver, outputDirectory);

        when(driver.getScreenshotAs(any(OutputType.class))).thenReturn(screenshot);

        stepFactory = new StepFactory(pages);
        stepFactory.addListener(stepListener);

        executionListener = new ThucydidesExecutionListener(stepListener);

    }



    @Mock
    private Behavior behavior;

    private ThucydidesExecutionListener executionListener;

    @Test
    public void an_easyb_scenario_should_run_the_steps() {

        run_story_with_one_scenario();

        AcceptanceTestRun testRun = stepListener.getTestRunResults().get(0);

        assertThat(testRun.getResult(), is(TestResult.SUCCESS));
        assertThat(testRun.getTestSteps().size(), is(1));
    }

    @Test
    public void an_easyb_scenario_should_take_screenshots_for_each_step_including_the_given_when_thens() {

        run_story_with_one_scenario();

        AcceptanceTestRun testRun = stepListener.getTestRunResults().get(0);

        verify(driver, times(3)).getScreenshotAs((OutputType<?>) anyObject());
    }


    private void run_story_with_one_scenario() {
        stepListener.testRunStarted("Test Run");

        BehaviorStep story = new BehaviorStep(STORY, "A User Story");
        BehaviorStep scenario = new BehaviorStep(SCENARIO, "Test Scenario");
        BehaviorStep given = new BehaviorStep(GIVEN, "a condition");
        BehaviorStep when = new BehaviorStep(WHEN, "an action");
        BehaviorStep then = new BehaviorStep(THEN, "an outcome");

        SampleSteps steps = (SampleSteps) stepFactory.newSteps(SampleSteps.class);

        executionListener.startBehavior(behavior);
        executionListener.startStep(story);
        executionListener.startStep(scenario);
        executionListener.startStep(given);
        steps.step1();
        executionListener.stopStep(); // given
        executionListener.stopStep(); //scenario
        executionListener.stopStep(); //story
        executionListener.stopBehavior(story, behavior);
    }


}
