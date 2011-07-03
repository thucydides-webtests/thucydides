package net.thucydides.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A page view that handles checking and waiting for element visibility.
 */
class RenderedPageObjectView {

    private final transient WebDriver driver;
    private final transient long waitForTimeout;

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
            throw new ElementNotVisibleException("Element not displayed: "
                    + byElementCriteria);
        }
    }

    public boolean elementIsDisplayed(final By byElementCriteria) {
        boolean isDisplayed = false;
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            if (matchingElements.isEmpty()) {
                return false;
            }            
            WebElement renderedElement  = matchingElements.get(0);
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
            LOGGER.error("Wait interrupted", e);
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
            throw new ElementNotVisibleException(
                    "Expected text was not displayed: '" + expectedText + "'");
        }
    }

    public void waitForText(final WebElement element, final String expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (containsText(element, expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!containsText(element, expectedText)) {
            throw new ElementNotVisibleException(
                    "Expected text was not displayed: '" + expectedText + "'");
        }
    }

    public void waitForTitle(final String expectedTitle) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (titleIs(expectedTitle)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!titleIs(expectedTitle)) {
            throw new ElementNotVisibleException(
                    "Expected title was not displayed: '" + expectedTitle + "'");
        }
    }

    private boolean titleIs(final String expectedTitle) {
        return ((driver.getTitle() != null) && (driver.getTitle().equals(expectedTitle)));
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

    public void waitForTextToDisappear(final String expectedText, final long timeout) {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            if (!containsText(expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (containsText(expectedText)) {
            throw new ElementNotVisibleException("Text was still displayed after timeout: '" + expectedText + "'");
        }
    }

    public void waitForTitleToDisappear(final String expectedTitle) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (!titleIs(expectedTitle)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (titleIs(expectedTitle)) {
            throw new ElementNotVisibleException("Title was still displayed after timeout: '" + expectedTitle + "'");
        }
    }

    public void waitForAnyTextToAppear(final String... expectedTexts) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (pageContains(expectedTexts)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!pageContains(expectedTexts)) {
            throw new ElementNotVisibleException("Expected text was not displayed: Was expecting any of '"
                      + Arrays.toString(expectedTexts));
        }
    }
    
    public void waitForAnyTextToAppear(final WebElement element, final String[] expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (elementContains(element, expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!elementContains(element, expectedText)) {
            throw new ElementNotVisibleException("Expected text was not displayed: '"
                                                    + Arrays.toString(expectedText) + "'");
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
            throw new ElementNotVisibleException("Expected text was not displayed: was expecting all of "
                                                    + printableFormOf(requestedTexts));
        }
    }

    private String printableFormOf(final List<String> texts) {
        return Arrays.toString(texts.toArray());
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

    public void waitForElementsToDisappear(final By byElementCriteria) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (!elementIsDisplayed(byElementCriteria)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (elementIsDisplayed(byElementCriteria)) {
            throw new ElementNotVisibleException("Element should not be displayed displayed: "
                    + byElementCriteria);
        }
    }

    public void waitForAnyRenderedElementOf(final By[] expectedElements) {
        long end = System.currentTimeMillis() + waitForTimeout;
        boolean renderedElementFound = false;
        while (System.currentTimeMillis() < end) {
            if (anyElementRenderedIn(expectedElements)) {
                renderedElementFound = true;
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!renderedElementFound) {
            throw new ElementNotVisibleException("None of the expected elements where displayed: '"
                                                    + Arrays.toString(expectedElements) + "'");
        }
    }

    private boolean anyElementRenderedIn(final By[] expectedElements) {
        boolean elementRendered = false;
        for (By expectedElement : expectedElements) {
            if (elementIsDisplayed(expectedElement)) {
                elementRendered = true;
                break;
            }
        }
        return elementRendered;
    }
}
