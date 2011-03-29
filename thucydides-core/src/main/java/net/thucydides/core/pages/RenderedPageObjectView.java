package net.thucydides.core.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ElementNotDisplayedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A page view that handles checking and waiting for element visibility.
 */
class RenderedPageObjectView {

    private final WebDriver driver;
    private final long waitForTimeout;

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RenderedPageObjectView.class);

    public RenderedPageObjectView(final WebDriver driver, final long waitForTimeout) {
        this.driver = driver;
        this.waitForTimeout = waitForTimeout;
    }

    public void waitFor(final By byElementCriteria) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (elementIsDisplayed(byElementCriteria)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        checkThatElementAppeared(byElementCriteria);
        checkThatElementIsDisplayed(byElementCriteria);
    }

    private void checkThatElementIsDisplayed(final By byElementCriteria) {
        if (!elementIsDisplayed(byElementCriteria)) {
            throw new ElementNotDisplayedException("Element not displayed: "
                    + byElementCriteria);
        }
    }

    private boolean elementIsDisplayed(final By byElementCriteria) {
        boolean isDisplayed = false;
        try {
            RenderedWebElement renderedElement = (RenderedWebElement) driver
                    .findElement(byElementCriteria);
            isDisplayed = renderedElement.isDisplayed();
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
        }
        return isDisplayed;
    }

    private void checkThatElementAppeared(final By byElementCriteria) {
        driver.findElement(byElementCriteria);
    }

    protected void waitABit(final long timeInMilliseconds) {
        try {
            Thread.sleep(timeInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForText(final String expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (containsText(expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!containsText(expectedText)) {
            throw new ElementNotDisplayedException(
                    "Expected text was not displayed: '" + expectedText + "'");
        }
    }

    public boolean containsText(final String textValue) {
        String textInBody = String.format("//body[contains(.,\"%s\")]",
                textValue);
        List<WebElement> elements = driver.findElements(By.xpath(textInBody));
        if (elements.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean containsText(final WebElement element, final String textValue) {
        String textInBody = String.format("//body[contains(.,\"%s\")]", textValue);
        List<WebElement> elements = element.findElements(By.xpath(textInBody));
        if (elements.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean userCanSee(final WebElement field) {
        if (RenderedWebElement.class.isAssignableFrom(field.getClass())) {
            return ((RenderedWebElement) field).isDisplayed();
        } else {
            return false;
        }
    }

    public void waitForTextToDisappear(final String expectedText, final long timeout) {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            if (!containsText(expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (containsText(expectedText)) {
            throw new ElementNotDisplayedException("Text was still displayed after timeout: '" + expectedText + "'");
        }
    }

    public void waitForAnyTextToAppear(final String... expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (pageContains(expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!pageContains(expectedText)) {
            throw new ElementNotDisplayedException("Expected text was not displayed: '" + expectedText + "'");
        }
    }
    
    public void waitForAnyTextToAppear(WebElement element, String[] expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (elementContains(element, expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!elementContains(element, expectedText)) {
            throw new ElementNotDisplayedException("Expected text was not displayed: '" + expectedText + "'");
        }
        
    }

    private boolean elementContains(final WebElement element, final String... expectedTexts) {
        for(String expectedText : expectedTexts) {
            if (containsText(element, expectedText)) {
                return true;
            }
        }
        return false;
    }

    private boolean pageContains(final String... expectedTexts) {
        for(String expectedText : expectedTexts) {
            if (containsText(expectedText)) {
                return true;
            }
        }
        return false;
    }

    public void waitForAllTextToAppear(final String... expectedTexts) {
        long end = System.currentTimeMillis() + waitForTimeout;

        List<String> requestedTexts = buildInitialListOfExpectedTextsFrom(expectedTexts);
        
        boolean allTextsFound = false;
        while (System.currentTimeMillis() < end) {
            requestedTexts = removeAnyTextsPresentOnPageFrom(requestedTexts);

            if (requestedTexts.isEmpty()) {
                allTextsFound = true;
                break;
            }
            
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!allTextsFound) {
            throw new ElementNotDisplayedException("Expected text was not displayed: '" 
                                                    + Arrays.toString(requestedTexts.toArray()) + "'");
        }
    }
    

    private List<String> buildInitialListOfExpectedTextsFrom(final String... expectedTexts) {
        List<String> requestedTexts = new ArrayList<String>();
        requestedTexts.addAll(Arrays.asList(expectedTexts));
        return requestedTexts;
    }

    private List<String> removeAnyTextsPresentOnPageFrom(final List<String> requestedTexts) {
        List<String> updatedList = new ArrayList<String>();
        updatedList.addAll(requestedTexts);
        
        for(String requestedText : requestedTexts) {
            if (pageContains(requestedText)) {
                updatedList.remove(requestedText);
            }
        }
        return updatedList;
    }
  
}
