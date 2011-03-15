package net.thucydides.core.steps;

import net.thucydides.core.pages.Pages;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of reusable steps for use in an acceptance test suite.
 * A step corresponds to an action taken during a web test - clicking on a button or a link,
 * for example. Steps may be reused across more than one test, and may take parameters.
 *
 */
public class ScenarioSteps {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioSteps.class);

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
     * Pause the test to wait for the page to display completely.
     * This is not normally recommended practice, but is useful from time to time.
     */
    public void waitABit(final long delayInMilliseconds) {
        try {
            Thread.sleep(delayInMilliseconds);
        } catch (InterruptedException e) {
            LOGGER.warn("Wait a bit method was interrupted.", e);
        }
    }
    /**
     * Marks the last step in a requirements test.
     * You <em>must</em> place this as the last method in your test
     * case for reporting to work correctly. 
     */
    public void done() {}
}
