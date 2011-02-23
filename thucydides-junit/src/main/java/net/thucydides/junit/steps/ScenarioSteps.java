package net.thucydides.junit.steps;

import net.thucydides.core.pages.Pages;

import org.openqa.selenium.WebDriver;

/**
 * A set of reusable steps for use in an acceptance test suite.
 * A step corresponds to an action taken during a web test - clicking on a button or a link,
 * for example. Steps may be reused across more than one test, and may take parameters.
 *
 */
public class ScenarioSteps {
    
    private Pages pages;
    
    public ScenarioSteps(final Pages pages) {
        this.pages = pages;
    }
    
    public WebDriver getDriver() {
        return pages.getDriver();
    }
    
    public Pages getPages() {
        return pages;
    }

    /**
     * Marks the last step in a requirements test.
     * You <em>must</em> place this as the last method in your test
     * case for reporting to work correctly.
     */
    public void done() {}
}
