package net.thucydides.junit.runners.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class TestUsingPendingAnnotationSample {
    
    @Managed
    public WebDriver webdriver;
    
    @Test @Step(1)
    public void should_do_this_step_1() {
    }
        
    @Test @Step(2)
    public void should_do_that_step_2() {
    }
    
    @Pending
    @Test @Step(3)    
    public void skip_this_pending_step() {
    }
    
    @Pending
    @Test @Step(4)
    public void this_step_is_pending_too() {
    }
}
