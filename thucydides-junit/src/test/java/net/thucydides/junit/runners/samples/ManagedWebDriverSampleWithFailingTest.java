package net.thucydides.junit.runners.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(ThucydidesRunner.class)
public class ManagedWebDriverSampleWithFailingTest {
    
    @Managed
    public WebDriver webdriver;
    
    @Test
    public void should_do_this_step_1() {
    }
        
    @Test
    public void should_do_that_step_2() {
    }
    
    @Test
    public void then_gets_here_step_3() {
        assertThat(true, is(false));
    }
    
    @Test
    public void dont_get_to_here() {
    }
    
    @Test
    public void or_to_here() {
        
    }
}
