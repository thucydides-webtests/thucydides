package net.thucydides.core.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

public class WhenOrganizingTestStepsInAScenario {

    class SimpleTestScenarioSteps extends ScenarioSteps {

        public SimpleTestScenarioSteps(Pages pages) {
            super(pages);
        }

        @Step
        public void step1(){}

        @Step
        public void step2(){}
        
        @Step
        public void step3(){}
    }
    
    @Mock
    Pages pages;
    
    @Mock
    WebDriver driver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(pages.getDriver()).thenReturn(driver);
    }
    
    @Test
    public void a_scenario_steps_library_uses_a_Pages_library() {
        
        SimpleTestScenarioSteps steps = new SimpleTestScenarioSteps(pages);        
        assertThat(steps.getPages(), is(pages));
    }

    @Test
    public void the_pages_method_can_also_be_used_to_obtain_the_Pages_library() {

        SimpleTestScenarioSteps steps = new SimpleTestScenarioSteps(pages);
        assertThat(steps.pages(), is(pages));
    }

    @Test
    public void a_user_runs_the_tests_by_calling_the_step_methos() {
        SimpleTestScenarioSteps steps = new SimpleTestScenarioSteps(pages);        
        steps.step1();
        steps.step2();
        steps.step3();
    }

    
    @Test
    public void a_scenario_steps_library_can_provide_the_supporting_web_driver() {
        
        SimpleTestScenarioSteps steps = new SimpleTestScenarioSteps(pages);        
        assertThat(steps.getDriver(), is(driver));
    }

    @Test
    public void the_scenario_can_be_paused_during_the_test() {        
        SimpleTestScenarioSteps steps = new SimpleTestScenarioSteps(pages);      
        long startTime = System.currentTimeMillis();
        steps.waitABit(100);
        long endTime = System.currentTimeMillis();
        assertThat(endTime, greaterThanOrEqualTo(startTime + 100));
    }
    
}
