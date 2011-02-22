package net.thucydides.junit.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.annotations.ForUserStory;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
@ForUserStory(SampleUserStory.class)
public class ScenarioInStory {
    
    @Managed
    public WebDriver webdriver;

    @Test @Step(1)
    public void should_do_this_step_1() {
    }
        
    @Test @Step(2)
    public void should_do_that_step_2() {
    }
    
    @Test @Step(3)
    public void then_gets_here_step_3() {
    }
    
    @Test @Step(4)
    public void finally_gets_here_step_4() {
    }
    
    @Test @Step(5)
    public void and_at_the_end_step_5() {
        
    }
}
