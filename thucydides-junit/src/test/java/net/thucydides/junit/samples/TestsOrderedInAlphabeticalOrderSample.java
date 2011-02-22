package net.thucydides.junit.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class TestsOrderedInAlphabeticalOrderSample {
    
    @Managed
    public WebDriver webdriver;
            
    @Test
    public void step_2_should_do_this_step_second() {
    }
    
    @Test 
    public void step_3_should_do_this_step_third() {
    }
    
    @Test
    public void step_1_shoud_do_this_step_first() {
    }
    
    @Test
    public void step_4_should_do_this_step_forth() {
    }
    
}
