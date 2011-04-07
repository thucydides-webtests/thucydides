package net.thucydides.gwt.widgets;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ElementNotDisplayedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GWT button.
 * This provides some extra features, such as knowing whether a button is enabled
 * and waiting until it is enabled if required.
 */
public class GwtButton {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final int TIMEOUT = 60 * 1000;
    
    private long waitForTimeout = TIMEOUT;

    private static final Logger LOGGER = LoggerFactory.getLogger(GwtButton.class);

    private final String label;
    private final WebElement button;

    public GwtButton(final String label,final WebElement button) {
        this.label = label;
        this.button = button;
    }

    public GwtButton(final WebElement button) {
        this.button = button;
        this.label = button.getText();
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }
    
    public String getLabel() {
        return label;
    }

    public Boolean isEnabled() {
        RenderedWebElement renderedButton = (RenderedWebElement) button;
        return renderedButton.isEnabled();
    }

    public Boolean isVisible() {
        RenderedWebElement renderedButton = (RenderedWebElement) button;
        return renderedButton.isDisplayed();
    }

    public Boolean isDisabled() {
        RenderedWebElement renderedButton = (RenderedWebElement) button;
        return !renderedButton.isEnabled();
    }

    public void click() {
        button.click();
    }

    public GwtButton waitUntilEnabled() {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (isEnabled()) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (isDisabled()) {
            throw new ElementNotDisplayedException("The '" + label + "' button should be enabled but was not.");
        }
        return this;

    }

    protected void waitABit(final long timeInMilliseconds) {
        try {
            Thread.sleep(timeInMilliseconds);
        } catch (InterruptedException e) {
            LOGGER.error("Wait interrupted", e);
        }
    }
    
}
