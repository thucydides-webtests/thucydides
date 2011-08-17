package net.thucydides.core.steps;

import java.io.Serializable;

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
public class ScenarioSteps implements Serializable {
    
    private static final long serialVersionUID = 1L;

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

    public Pages pages() {
        return getPages();
    }

    public <T extends ScenarioSteps> T onSamePage(Class<T> stepsType) {
        getPages().onSamePage();
        return (T) stepsType.cast(this);
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Marks the last step in a requirements test.
     * Used internally to trigger JUnit listener events at the end of a test case.
     */
    public void done() {}
}
