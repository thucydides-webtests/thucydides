package net.thucydides.core.pages;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.handler.BySelector;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.convert;


/**
 * A proxy class for a web element, providing some more methods.
 */
public class WebElementFacade {

    private final WebElement webElement;
    private final WebDriver driver;
    private final long timeoutInMilliseconds;
    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 250;
    private final Sleeper sleeper;
    private final Clock webdriverClock;
    private JavascriptExecutorFacade javascriptExecutorFacade;
    private InternalSystemClock clock = new InternalSystemClock();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementFacade.class);

    public WebElementFacade(final WebDriver driver,
                            final WebElement webElement,
                            final long timeoutInMilliseconds) {
        this.driver = driver;
        this.webElement = webElement;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
        this.webdriverClock = new SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);

    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        return javascriptExecutorFacade;
    }

    protected InternalSystemClock getClock() {
        return clock;
    }


    public WebElementFacade then(String xpathOrCssSelector) {
        return findBy(xpathOrCssSelector);
    }

    public WebElementFacade findBy(String xpathOrCssSelector) {
        WebElement nestedElement = null;
        if (PageObject.isXPath(xpathOrCssSelector)) {
            nestedElement = webElement.findElement((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElement = webElement.findElement((By.cssSelector(xpathOrCssSelector)));
        }
        return new  WebElementFacade(driver, nestedElement, timeoutInMilliseconds);
    }

    public List<WebElementFacade> thenFindAll(String xpathOrCssSelector) {
        List<WebElement> nestedElements = Lists.newArrayList();
        if (PageObject.isXPath(xpathOrCssSelector)) {
            nestedElements = webElement.findElements((By.xpath(xpathOrCssSelector)));
        } else {
            nestedElements = webElement.findElements((By.cssSelector(xpathOrCssSelector)));
        }

        return webElementFacadesFrom(nestedElements);
    }

    private List<WebElementFacade> webElementFacadesFrom(List<WebElement> nestedElements) {
        List<WebElementFacade> results = Lists.newArrayList();
        for(WebElement element : nestedElements) {
            results.add(new  WebElementFacade(driver, element, timeoutInMilliseconds));
        }
        return results;
    }

    public WebElementFacade findBy(By selector) {
        WebElement nestedElement = webElement.findElement(selector);
        return new WebElementFacade(driver, nestedElement, timeoutInMilliseconds);
    }

    public WebElementFacade then(By bySelector) {
        return findBy(bySelector);
    }

    public List<WebElementFacade> thenFindAll(By selector) {
        List<WebElement> nestedElements = driver.findElements(selector);
        return webElementFacadesFrom(nestedElements);
    }

    public long getTimeoutInMilliseconds() {
        return timeoutInMilliseconds;
    }

    public WebElementFacade withTimeoutOf(int timeout, TimeUnit unit) {
        return new WebElementFacade(driver, webElement,
                TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    public boolean isVisible() {

        try {
            return (webElement != null) && (webElement.isDisplayed());
        } catch (ElementNotVisibleException e) {
            return false;
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    public WebElementFacade and() {
        return this;
    }

    /**
     * Convenience method to chain method calls more fluently.
     */
    public WebElementFacade then() {
        return this;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * The method will fail immediately if the element is not visible on the screen.
     * There is a little black magic going on here - the web element class will detect if it is being called
     * by a method called "isCurrently*" and, if so, fail immediately without waiting as it would normally do.
     */
    public boolean isCurrentlyVisible() {
        return isVisible();
    }

    public boolean isCurrentlyEnabled() {
        try {
            return webElement.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException se) {
            return false;
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldBeVisible() {
        if (!isVisible()) {
            throw new AssertionError("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldBeCurrentlyVisible() {
        if (!isCurrentlyVisible()) {
            throw new AssertionError("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldNotBeVisible() {
        if (isCurrentlyVisible()) {
            throw new AssertionError("Element should not be visible");
        }
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldNotBeCurrentlyVisible() {
        if (isCurrentlyVisible()) {
            throw new AssertionError("Element should not be visible");
        }
    }

    /**
     * Does this element currently have the focus.
     */
    public boolean hasFocus() {
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        WebElement activeElement = (WebElement) js.executeScript("return window.document.activeElement");
        return webElement.equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    public boolean containsText(final String value) {
        return ((webElement != null) && (webElement.getText().contains(value)));
    }

    /**
     * Does this dropdown contain the specified value.
     */
    public boolean containsSelectOption(final String value) {
        return getSelectOptions().contains(value);
    }

    public List<String> getSelectOptions() {
        List<WebElement> results = Collections.emptyList();
        if (webElement != null) {
            results = webElement.findElements(By.tagName("option"));
        }
        return convert(results, new ExtractText());
    }

    class ExtractText implements Converter<WebElement, String> {
        public String convert(WebElement from) {
            return from.getText();
        }
    }

    /**
     * Check that an element contains a text value
     *
     * @param textValue
     */
    public void shouldContainText(String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    public void shouldContainSelectedOption(String textValue) {
        if (!containsSelectOption(textValue)) {
            String errorMessage = String.format(
                    "The list element '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Check that an element does not contain a text value
     *
     * @param textValue
     */
    public void shouldNotContainText(String textValue) {
        if (containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    public void shouldBeEnabled() {
        if (!isEnabled()) {
            String errorMessage = String.format(
                    "Field '%s' should be enabled", webElement);
            throw new AssertionError(errorMessage);
        }
    }

    public boolean isEnabled() {
        return (webElement != null) && (webElement.isEnabled());
    }

    public void shouldNotBeEnabled() {
        if (isEnabled()) {
            String errorMessage = String.format(
                    "Field '%s' should not be enabled", webElement);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param value
     */
    public WebElementFacade type(final String value) {
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        clear();
        webElement.sendKeys(value);
        notifyScreenChange();
        return this;
    }

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     *
     * @param value
     */
    public WebElementFacade typeAndEnter(final String value) {
        waitUntilElementAvailable();
        clear();
        webElement.sendKeys(value, Keys.ENTER);
        notifyScreenChange();
        return this;
    }

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * This currently is not supported by all browsers, notably Firefox.
     *
     * @param value
     */
    public WebElementFacade typeAndTab(final String value) {
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        clear();

        webElement.sendKeys(value);
        webElement.sendKeys(Keys.TAB);

        getClock().pauseFor(100);
        notifyScreenChange();
        return this;
    }

    public void setWindowFocus() {
        getJavascriptExecutorFacade().executeScript("window.focus()");
    }

    public WebElementFacade selectByVisibleText(final String label) {
        waitUntilElementAvailable();
        Select select = new Select(webElement);
        select.selectByVisibleText(label);
        notifyScreenChange();
        return this;
    }

    public String getSelectedVisibleTextValue() {
        waitUntilVisible();
        Select select = new Select(webElement);
        return select.getFirstSelectedOption().getText();
    }

    public WebElementFacade selectByValue(String value) {
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        Select select = new Select(webElement);
        select.selectByValue(value);
        notifyScreenChange();
        return this;
    }

    public String getSelectedValue() {
        waitUntilVisible();
        Select select = new Select(webElement);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public WebElementFacade selectByIndex(int indexValue) {
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        Select select = new Select(webElement);
        select.selectByIndex(indexValue);
        notifyScreenChange();
        return this;
    }

    private void waitUntilElementAvailable() {
        waitUntilEnabled();
    }

    public boolean isPresent() {
        try {
            return (webElement != null) && (webElement.isDisplayed() || !webElement.isDisplayed());
        } catch (NoSuchElementException e) {
            if (e.getCause().getMessage().contains("Element is not usable")) {
                return true;
            }
            return false;
        }
    }

    public void shouldBePresent() {
        if (!isPresent()) {
            String errorMessage = String.format(
                    "Field should be present");
            throw new AssertionError(errorMessage);
        }
    }

    public void shouldNotBePresent() {
        if (isPresent()) {
            String errorMessage = String.format(
                    "Field should not be present");
            throw new AssertionError(errorMessage);
        }
    }

    public WebElementFacade waitUntilVisible() {
        try {
            waitForCondition().until(elementIsDisplayed());
        } catch (Throwable error) {
            throwErrorWithCauseIfPresent(error, error.getMessage());
        }
        return this;
    }

    public WebElementFacade waitUntilPresent() {
        try {
            waitForCondition().until(elementIsPresent());
        } catch (TimeoutException timeout) {
            throwErrorWithCauseIfPresent(timeout, timeout.getMessage());
        }
        return this;
    }


    private void throwErrorWithCauseIfPresent(final Throwable timeout, final String defaultMessage) {
        String timeoutMessage = (timeout.getCause() != null) ? timeout.getCause().getMessage() : timeout.getMessage();
        String finalMessage = (StringUtils.isNotEmpty(timeoutMessage)) ? timeoutMessage : defaultMessage;
        throw new ElementNotVisibleException(finalMessage, timeout);
    }

    private ExpectedCondition<Boolean> elementIsDisplayed() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return (webElement != null) && (webElement.isDisplayed());
                } catch (NullPointerException e) {
                    // Selenium sometimes throws a NPE if the element is not present at all on the page.
                    return false;
                }
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsPresent() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return isPresent();
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsNotDisplayed() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !isCurrentlyVisible();
            }
        };
    }

    private ExpectedCondition<Boolean> elementIsEnabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((webElement != null) && (!isDisabledField(webElement)));
            }
        };
    }

    private boolean isDisabledField(WebElement webElement) {
        return (isAFormElement(webElement) && (!webElement.isEnabled()));
    }

    private final List<String> HTML_FORM_TAGS = Arrays.asList("input", "button", "select", "textarea", "link", "option");

    private boolean isAFormElement(WebElement webElement) {
        if ((webElement == null) || (webElement.getTagName() == null)) {
            return false;
        }
        String tag = webElement.getTagName().toLowerCase();
        return HTML_FORM_TAGS.contains(tag);

    }

    private static final List<String> HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE = ImmutableList.of("input", "button", "option");

    private boolean hasValueAttribute(WebElement webElement) {
        String tag = webElement.getTagName().toLowerCase();
        return HTML_ELEMENTS_WITH_VALUE_ATTRIBUTE.contains(tag);

    }

    private ExpectedCondition<Boolean> elementIsNotEnabled() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((webElement != null) && (!webElement.isEnabled()));
            }
        };
    }

    public Wait<WebDriver> waitForCondition() {
        return new FluentWait<WebDriver>(driver, webdriverClock, sleeper)
                .withTimeout(timeoutInMilliseconds, TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public WebElementFacade waitUntilNotVisible() {
        try {
            waitForCondition().until(elementIsNotDisplayed());
        } catch (TimeoutException timeout) {
            throwErrorWithCauseIfPresent(timeout, "Expected hidden element was displayed");
        }
        notifyScreenChange();
        return this;
    }

    public String getValue() {
        waitUntilVisible();
        return webElement.getAttribute("value");
    }

    public boolean isSelected() {
        waitUntilVisible();
        return webElement.isSelected();
    }

    public String getText() {
        waitUntilVisible();
        return webElement.getText();
    }

    public WebElementFacade waitUntilEnabled() {
        try {
            waitForCondition().until(elementIsEnabled());
            notifyScreenChange();
            return this;
        } catch (TimeoutException timeout) {
            throw new ElementNotVisibleException("Expected enabled element " + this + "was not enabled" , timeout);
        }
    }

    public WebElementFacade waitUntilDisabled() {
        try {
            waitForCondition().until(elementIsNotEnabled());
            notifyScreenChange();
            return this;
        } catch (TimeoutException timeout) {
            throw new ElementNotVisibleException("Expected disabled element " + this + "  was not disabled", timeout);
        }
    }

    public String getTextValue() {
        waitUntilPresent();

        if (!isVisible()) {
            return "";
        }

        if (valueAttributeSupportedAndDefinedIn(webElement)) {
            return getValue();
        }

        if (!StringUtils.isEmpty(webElement.getText())) {
            return webElement.getText();
        }
        return "";
    }


    private boolean valueAttributeSupportedAndDefinedIn(final WebElement webElement) {
        return hasValueAttribute(webElement) && StringUtils.isNotEmpty(getValue());
    }

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    public WebElementFacade click() {
        enableHighlightingIfRequired();
        waitUntilElementAvailable();
        logClick();
        webElement.click();
        notifyScreenChange();
        return this;
    }

    private void logClick() {
        LOGGER.info("Click on " + humanizedTabfNameFor(webElement));
    }


    private String humanizedTabfNameFor(WebElement webElement) {
        return HtmlTag.from(webElement).inHumanReadableForm();
    }

    public void clear() {
        webElement.sendKeys(Keys.chord(Keys.CONTROL,"a"), Keys.DELETE);
        webElement.clear();
    }

    private void enableHighlightingIfRequired() {
        JQueryEnabledPage jQueryEnabledPage = JQueryEnabledPage.withDriver(driver);
        if (jQueryEnabledPage.isJQueryEnabled()) {
            jQueryEnabledPage.injectJQueryPlugins();
        }
    }
    private void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

    @Override
    public String toString() {
        if (webElement != null) {
            return webElement.toString();
        } else {
            return "<Undefined web element>";
        }
    }
}

