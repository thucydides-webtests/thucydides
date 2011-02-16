package net.thucydides.junit.runners.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(ThucydidesRunner.class)
public class ManagedWebDriverWithFailingTestSample {
    
    @Managed
    public WebDriver webdriver;
    
    @Test @Step(1)
    public void should_do_this_step_1() {
    }
        
    @Test @Step(2)
    public void should_do_that_step_2() {
    }
    
    @Test @Step(3)
    public void but_fail_here_in_step_3() {
        assertThat(true, is(false));
    }
    
    @Test @Step(4)
    public void dont_get_to_here() {
    }
    
    @Test @Step(5)
    public void or_to_here() {
        
    }
}
