package net.thucydides.junit.integration.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * 
 * @author johnsmart
 * 
 */
@RunWith(ThucydidesRunner.class)
public class FailingAndPendingTestsSample {

    @Managed
    public WebDriver driver;

    @Test
    @Step(1)
    public void step1() {
    }

    @Test
    @Step(2)
    public void step2() {
    }

    @Test
    @Step(3)
    public void step3() {
        assertThat(1, is(2));
    }

    @Test
    @Step(4)
    public void step4() {
    }

    @Test
    @Step(5)
    @Ignore
    public void step5() {
    }

    @Test
    @Ignore
    public void step6() {
    }

    @Test
    @Step(7)
    @Pending
    public void step7() {
    }

    @Test
    @Step(8)
    @Pending
    public void step8() {
    }

    @Test
    @Step(9)
    @Pending
    public void step9() {
    }

}
