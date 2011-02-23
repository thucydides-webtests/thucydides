package net.thucydides.junit.integration.samples;

import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.ManagedPages;
import net.thucydides.junit.annotations.Steps;
import net.thucydides.junit.annotations.Title;
import net.thucydides.junit.runners.TestStepRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(TestStepRunner.class)
public class OpenApacheHomePageWithTitleSample {

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.apache.org")
    public Pages pages;
    
    @Steps
    public ApacheScenarioSteps steps;
        
    @Test
    @Title("The user navigates to the Apache project page.")
    public void the_user_opens_the_page() {
        pages.start();
        steps.clickOnProjects();
        steps.clickOnCategories();
        steps.done();
    }    
}
