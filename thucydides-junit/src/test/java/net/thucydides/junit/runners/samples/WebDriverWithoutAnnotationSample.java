package net.thucydides.junit.runners.samples;

import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class WebDriverWithoutAnnotationSample {
    
    public WebDriver webdriver;
    
    @Test
    public void shoud_do_this_step_1() {
    }
        
    @Test
    public void should_do_that_step_2() {
    }
    
    @Test
    public void then_gets_here_step_3() {
    }
    
    @Test
    public void finally_gets_here_step_4() {
    }
}
