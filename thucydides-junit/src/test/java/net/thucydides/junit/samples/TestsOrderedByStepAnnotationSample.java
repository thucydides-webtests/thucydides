package net.thucydides.junit.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class TestsOrderedByStepAnnotationSample {
    
    @Managed
    public WebDriver webdriver;
            
    @Test @Step(2)
    public void should_do_this_step_second() {
    }
    
    @Test @Step(3)
    public void should_do_this_step_third() {
    }
    
    @Test @Step(1)
    public void shoud_do_this_step_first() {
    }
    
    @Test @Step(4)
    public void should_do_this_step_forth() {
    }
    
}
