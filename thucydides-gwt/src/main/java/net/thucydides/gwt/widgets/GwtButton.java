package net.thucydides.gwt.widgets;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ElementNotDisplayedException;

public class GwtButton {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final int TIMEOUT = 60 * 1000;
    
    private long waitForTimeout = TIMEOUT;

    private final String label;
    private final WebElement button;

    public GwtButton(String label, WebElement button) {
        this.label = label;
        this.button = button;
    }

    public GwtButton(WebElement button) {
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
            e.printStackTrace();
        }
    }
    
}
