package net.thucydides.junit.integration.samples;

import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.DemoSiteSteps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(ThucydidesRunner.class)
@UserStoryCode("US101")
public class OpenStaticDemoPageSample {

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public DemoSiteSteps steps;
        
    @Test
    @Title("The user opens the index page")
    @TestsRequirement("R123")
    public void the_user_opens_the_page() {
        pages.start();
        steps.enter_values("Label 1", true);
    }    
    
    @Test
    @Title("The user enters different values.")
    public void the_user_opens_another_page() {
        pages.start();
        steps.enter_values("Label 2", true);
        steps.done();
    }    


}
