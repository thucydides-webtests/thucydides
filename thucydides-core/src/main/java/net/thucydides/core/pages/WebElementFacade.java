package net.thucydides.core.pages;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


/**
 * A proxy class for a web element, providing some more methods.
 */
public class WebElementFacade {

    private final WebElement webElement;
    private final WebDriver driver;
    private final long timeoutInMilliseconds;
    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 100;
    private InternalSystemClock clock;
    private JavaScriptExecutorFacade javaScriptExecutorFacade;


    public WebElementFacade(final WebDriver driver,
                            final WebElement webElement,
                            final long timeoutInMilliseconds) {
        this.driver = driver;
        this.webElement = webElement;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
        this.clock = new InternalSystemClock();
        this.javaScriptExecutorFacade = new JavaScriptExecutorFacade(driver);
    }


    protected JavaScriptExecutorFacade getJavaScriptExecutorFacade() {
        return javaScriptExecutorFacade;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    public boolean isVisible() {
        try {
            return webElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
		} catch (StaleElementReferenceException se) {
			return false;
		}
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
        if (isVisible()) {
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
        JavaScriptExecutorFacade js = new JavaScriptExecutorFacade(driver);
        WebElement activeElement = (WebElement) js.executeScript("return window.document.activeElement");
        return webElement.equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    public boolean containsText(final String value) {
        return webElement.getText().contains(value);
    }

    /**
     * Check that an element contains a text value
     * @param textValue
     */
    public void shouldContainText(String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Check that an element does not contain a text value
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
        return webElement.isEnabled();
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
     * @param value
     */
    public void type(final String value) {
        webElement.clear();
        webElement.sendKeys(value);
    }

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     * @param value
     */
    public void typeAndEnter(final String value) {
        webElement.clear();
        webElement.sendKeys(value, Keys.ENTER);
    }

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * @param value
     */
    public void typeAndTab(final String value) {
        webElement.clear();
        webElement.sendKeys(value + Keys.TAB);
    }

    public void setWindowFocus() {
        getJavaScriptExecutorFacade().executeScript("window.focus()");
    }

    public void selectByVisibleText(final String label) {
        Select select = new Select(webElement);
        select.selectByVisibleText(label);
    }

    public String getSelectedVisibleTextValue() {
        Select select = new Select(webElement);
        return select.getFirstSelectedOption().getText();
    }

    public void selectByValue(String value) {
        Select select = new Select(webElement);
        select.selectByValue(value);
    }

    public String getSelectedValue() {
        Select select = new Select(webElement);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public void selectByIndex(int indexValue) {
        Select select = new Select(webElement);
        select.selectByIndex(indexValue);
    }

    public boolean isPresent() {
        try {
            return webElement.isDisplayed() || !webElement.isDisplayed() ;
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

    public void waitUntilVisible() {
        long end = System.currentTimeMillis() + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            if (isCurrentlyVisible()) {
                break;
            }
            clock.pauseFor(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!isCurrentlyVisible()) {
            throw new ElementNotVisibleException(
                    "Expected element was not displayed");
        }
    }

    public void waitUntilNotVisible() {
        long end = System.currentTimeMillis() + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            if (!isCurrentlyVisible()) {
                break;
            }
            clock.pauseFor(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (isCurrentlyVisible()) {
            throw new ElementNotVisibleException(
                    "Expected hidden element was displayed");
        }
    }

    public String getValue() {
        return webElement.getAttribute("value");
    }

    public String getText() {
        return webElement.getText();
    }

    public void waitUntilEnabled() {
        long end = System.currentTimeMillis() + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            if (isCurrentlyEnabled()) {
                break;
            }
            clock.pauseFor(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!isCurrentlyEnabled()) {
            throw new ElementNotVisibleException(
                    "Expected element was not enabled");
        }
    }

    public void waitUntilDisabled() {
        long end = System.currentTimeMillis() + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            if (!isCurrentlyEnabled()) {
                break;
            }
            clock.pauseFor(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (isCurrentlyEnabled()) {
            throw new ElementNotVisibleException(
                    "Expected disabled element was enabled");
        }
    }

    public String getTextValue(){
        if(!getText().isEmpty())
        {
            return webElement.getText();
        }
        if (!getValue().isEmpty())
        {
            return getValue();
        }
        return "";
    }

}
