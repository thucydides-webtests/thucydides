package net.thucydides.core.pages;

import org.apache.commons.collections.ListUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A page view that handles checking and waiting for element visibility.
 */
class RenderedPageObjectView {

    private final transient WebDriver driver;
    private transient long waitForTimeout;
    private final Clock webdriverClock;
    private final Sleeper sleeper;

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RenderedPageObjectView.class);

    public RenderedPageObjectView(final WebDriver driver, final long waitForTimeout) {
        this.driver = driver;
        this.waitForTimeout = waitForTimeout;
        this.webdriverClock = new SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
    }

    public Wait<WebDriver> waitForCondition() {
        return new FluentWait<WebDriver>(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeout, TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    private ExpectedCondition<Boolean> elementDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsDisplayed(byElementCriteria));
            }
        };
    }

    private ExpectedCondition<Boolean> elementPresent(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsDisplayed(byElementCriteria));
            }
        };
    }
    /**
     * This method will wait until an element is present and visible on the screen.
     */
    public void waitFor(final By byElementCriteria) {
        waitForCondition().until(elementDisplayed(byElementCriteria));

        checkThatElementAppeared(byElementCriteria);
        checkThatElementIsDisplayed(byElementCriteria);
    }

    /**
     * This method will wait until an element is present on the screen, though not necessarily visible.
     */
    public void waitForPresenceOf(final By byElementCriteria) {
        waitForCondition().until(elementPresent(byElementCriteria));
        checkThatElementIsPresent(byElementCriteria);
    }

    private void checkThatElementIsDisplayed(final By byElementCriteria) {
        if (!elementIsDisplayed(byElementCriteria)) {
            throw new ElementNotVisibleException("Element not displayed: "
                    + byElementCriteria);
        }
    }

    private void checkThatElementIsPresent(final By byElementCriteria) {
        if (!elementIsPresent(byElementCriteria)) {
            throw new ElementNotVisibleException("Element not present: "
                    + byElementCriteria);
        }
    }

    public boolean elementIsPresent(final By byElementCriteria) {
        boolean isDisplayed = true;
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            if (matchingElements.isEmpty()) {
                return false;
            }
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
        }
        return isDisplayed;
    }

    public boolean elementIsDisplayed(final By byElementCriteria) {
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            return (matchingElementsArePresent(matchingElements)) && matchingElements.get(0).isDisplayed();
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
            return false;
        } catch (StaleElementReferenceException se) {
            LOGGER.trace("Element no longer attached to the DOM " + se);
            return false;
        }
    }

    private boolean matchingElementsArePresent(List<WebElement> matchingElements) {
        return (matchingElements != null) && (!matchingElements.isEmpty());
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

    private ExpectedCondition<Boolean> textPresent(final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(expectedText));
            }
        };
    }

    public void waitForText(final String expectedText) {
        waitForCondition().until(textPresent(expectedText));

        if (!containsText(expectedText)) {
            throw new ElementNotVisibleException(
                    "Expected text was not displayed: '" + expectedText + "'");
        }
    }

    private ExpectedCondition<Boolean> textPresentInElement(final WebElement element, final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(element, expectedText));
            }
        };
    }

    public void waitForText(final WebElement element, final String expectedText) {
        waitForCondition().until(textPresentInElement(element, expectedText));
        if (!containsText(element, expectedText)) {
            throw new ElementNotVisibleException(
                    "Expected text was not displayed: '" + expectedText + "'");
        }
    }

    private ExpectedCondition<Boolean> titlePresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return titleIs(expectedTitle);
            }
        };
    }

    public void waitForTitle(final String expectedTitle) {
        waitForCondition().until(titlePresent(expectedTitle));

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
        if (foundNo(elements)) {
            return false;
        }
        return true;
    }

    private boolean foundNo(List<WebElement> elements) {
        return ((elements == null) || (elements.isEmpty()));
    }

    public boolean containsText(final WebElement element, final String textValue) {
        String textInBody = String.format("//body[contains(.,\"%s\")]", textValue);
        List<WebElement> elements = element.findElements(By.xpath(textInBody));
        if (foundNo(elements)) {
            return false;
        }
        return true;
    }

    private ExpectedCondition<Boolean> textNotPresent(final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !containsText(expectedText);
            }
        };
    }

    public void waitForTextToDisappear(final String expectedText, final long timeout) {
        waitForCondition().until(textNotPresent(expectedText));
        if (containsText(expectedText)) {
            throw new ElementNotVisibleException("Text was still displayed after timeout: '" + expectedText + "'");
        }
    }

    private ExpectedCondition<Boolean> titleNotPresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !titleIs(expectedTitle);
            }
        };
    }

    public void waitForTitleToDisappear(final String expectedTitle) {
        waitForCondition().until(titleNotPresent(expectedTitle));
        if (titleIs(expectedTitle)) {
            throw new ElementNotVisibleException("Title was still displayed after timeout: '" + expectedTitle + "'");
        }
    }

    private ExpectedCondition<Boolean> anyTextPresent(final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return pageContainsAny(expectedTexts);
            }
        };
    }

    public void waitForAnyTextToAppear(final String... expectedTexts) {
        waitForCondition().until(anyTextPresent(expectedTexts));

        if (!pageContainsAny(expectedTexts)) {
            throw new ElementNotVisibleException("Expected text was not displayed: Was expecting any of '"
                    + Arrays.toString(expectedTexts));
        }
    }

    private ExpectedCondition<Boolean> anyTextPresentInElement(final WebElement element, final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return elementContains(element, expectedTexts);
            }
        };
    }

    public void waitForAnyTextToAppear(final WebElement element, final String... expectedTexts) {
        waitForCondition().until(anyTextPresentInElement(element, expectedTexts));

        if (!elementContains(element, expectedTexts)) {
            throw new ElementNotVisibleException("Expected text was not displayed: '"
                    + Arrays.toString(expectedTexts) + "'");
        }

    }

    private boolean elementContains(final WebElement element, final String... expectedTexts) {
        for (String expectedText : expectedTexts) {
            if (containsText(element, expectedText)) {
                return true;
            }
        }
        return false;
    }

    private boolean pageContainsAny(final String... expectedTexts) {
        for (String expectedText : expectedTexts) {
            if (containsText(expectedText)) {
                return true;
            }
        }
        return false;
    }

    private ExpectedCondition<Boolean> allTextPresent(final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                for(String expectedText : expectedTexts) {
                    if (!containsText(expectedText)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public void waitForAllTextToAppear(final String... expectedTexts) {
        waitForCondition().until(allTextPresent(expectedTexts));

        if (!allTextPresent(expectedTexts).apply(driver)) {
            throw new ElementNotVisibleException("Expected text was not displayed: was expecting all of "
                    + printableFormOf(Arrays.asList(expectedTexts)));
        }
    }

    private String printableFormOf(final List<String> texts) {
        return Arrays.toString(texts.toArray());
    }


//    private List<String> buildInitialListOfExpectedTextsFrom(final String... expectedTexts) {
//        List<String> requestedTexts = new ArrayList<String>();
//        requestedTexts.addAll(Arrays.asList(expectedTexts));
//        return requestedTexts;
//    }
//
//    private List<String> removeAnyTextsPresentOnPageFrom(final List<String> requestedTexts) {
//        List<String> updatedList = new ArrayList<String>();
//        updatedList.addAll(requestedTexts);
//
//        for (String requestedText : requestedTexts) {
//            if (pageContainsAny(requestedText)) {
//                updatedList.remove(requestedText);
//            }
//        }
//        return updatedList;
//    }

    private ExpectedCondition<Boolean> elementNotDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (!elementIsDisplayed(byElementCriteria));
            }
        };
    }

    public void waitForElementsToDisappear(final By byElementCriteria) {
        waitForCondition().until(elementNotDisplayed(byElementCriteria));

        if (elementIsDisplayed(byElementCriteria)) {
            throw new UnexpectedElementVisibleException("Element should not be displayed displayed: "
                    + byElementCriteria);
        }
    }

    private ExpectedCondition<Boolean> anyElementPresent(final By... expectedElements) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                for(By expectedElement : expectedElements) {
                    if (elementIsDisplayed(expectedElement)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    public void waitForAnyRenderedElementOf(final By[] expectedElements) {
        waitForCondition().until(anyElementPresent(expectedElements));

        if (!anyElementPresent(expectedElements).apply(driver)) {
            throw new ElementNotVisibleException("None of the expected elements where displayed: '"
                    + Arrays.toString(expectedElements) + "'");
        }
    }

//    private boolean anyElementRenderedIn(final By[] expectedElements) {
//        boolean elementRendered = false;
//        for (By expectedElement : expectedElements) {
//            if (elementIsDisplayed(expectedElement)) {
//                elementRendered = true;
//                break;
//            }
//        }
//        return elementRendered;
//    }

    public void setWaitForTimeout(long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }
}
