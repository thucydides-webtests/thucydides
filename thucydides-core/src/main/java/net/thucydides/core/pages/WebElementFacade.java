package net.thucydides.core.pages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.thucydides.core.annotations.DelayElementLocation;
import net.thucydides.core.annotations.implementedBy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.Wait;

@implementedBy(WebElementFacadeImpl.class)
public interface WebElementFacade extends WebElement, WrapsElement, Locatable{

	public abstract WebElementFacade then(String xpathOrCssSelector);

	public abstract WebElementFacade findBy(String xpathOrCssSelector);

	public abstract List<WebElementFacade> thenFindAll(
			String xpathOrCssSelector);

	public abstract WebElementFacade findBy(By selector);

	public abstract WebElementFacade find(By bySelector);

	public abstract WebElementFacade then(By bySelector);

	public abstract String getAttribute(String name);

	public abstract List<WebElementFacade> thenFindAll(By selector);

	public abstract long getTimeoutInMilliseconds();

	public abstract WebElementFacade withTimeoutOf(int timeout,
			TimeUnit unit);

	/**
	 * Is this web element present and visible on the screen
	 * This method will not throw an exception if the element is not on the screen at all.
	 * If the element is not visible, the method will wait a bit to see if it appears later on.
	 */
	@DelayElementLocation
	public abstract boolean isVisible();

	/**
	 * Convenience method to chain method calls more fluently.
	 */
	public abstract WebElementFacade and();

	/**
	 * Convenience method to chain method calls more fluently.
	 */
	public abstract WebElementFacade then();

	/**
	 * Is this web element present and visible on the screen
	 * This method will not throw an exception if the element is not on the screen at all.
	 * The method will fail immediately if the element is not visible on the screen.
	 * There is a little black magic going on here - the web element class will detect if it is being called
	 * by a method called "isCurrently*" and, if so, fail immediately without waiting as it would normally do.
	 */
	@DelayElementLocation
	public abstract boolean isCurrentlyVisible();

	public abstract boolean isCurrentlyEnabled();

	/**
	 * Checks whether a web element is visible.
	 * Throws an AssertionError if the element is not rendered.
	 */
	@DelayElementLocation
	public abstract void shouldBeVisible();

	/**
	 * Checks whether a web element is visible.
	 * Throws an AssertionError if the element is not rendered.
	 */
	@DelayElementLocation
	public abstract void shouldBeCurrentlyVisible();

	/**
	 * Checks whether a web element is not visible.
	 * Throws an AssertionError if the element is not rendered.
	 */
	@DelayElementLocation
	public abstract void shouldNotBeVisible();

	/**
	 * Checks whether a web element is not visible straight away.
	 * Throws an AssertionError if the element is not rendered.
	 */
	@DelayElementLocation
	public abstract void shouldNotBeCurrentlyVisible();

	/**
	 * Does this element currently have the focus.
	 */
	public abstract boolean hasFocus();

	/**
	 * Does this element contain a given text?
	 */
	public abstract boolean containsText(String value);

	/**
	 * Does this element exactly match  given text?
	 */
	public abstract boolean containsOnlyText(String value);

	/**
	 * Does this dropdown contain the specified value.
	 */
	public abstract boolean containsSelectOption(String value);

	public abstract List<String> getSelectOptions();

	/**
	 * Check that an element contains a text value
	 *
	 * @param textValue
	 */
	public abstract void shouldContainText(String textValue);

	/**
	 * Check that an element exactly matches a text value
	 *
	 * @param textValue
	 */
	public abstract void shouldContainOnlyText(String textValue);

	public abstract void shouldContainSelectedOption(String textValue);

	/**
	 * Check that an element does not contain a text value
	 *
	 * @param textValue
	 */
	public abstract void shouldNotContainText(String textValue);

	public abstract void shouldBeEnabled();

	public abstract boolean isEnabled();

	public abstract void shouldNotBeEnabled();

	/**
	 * Type a value into a field, making sure that the field is empty first.
	 *
	 * @param value
	 */
	public abstract WebElementFacade type(String value);

	/**
	 * Type a value into a field and then press Enter, making sure that the field is empty first.
	 *
	 * @param value
	 */
	public abstract WebElementFacade typeAndEnter(String value);

	/**
	 * Type a value into a field and then press TAB, making sure that the field is empty first.
	 * This currently is not supported by all browsers, notably Firefox.
	 *
	 * @param value
	 */
	public abstract WebElementFacade typeAndTab(String value);

	public abstract void setWindowFocus();

	public abstract WebElementFacade selectByVisibleText(String label);

	public abstract String getSelectedVisibleTextValue();

	public abstract WebElementFacade selectByValue(String value);

	public abstract String getSelectedValue();

	public abstract WebElementFacade selectByIndex(int indexValue);
	
	@DelayElementLocation
	public abstract boolean isPresent();
	
	@DelayElementLocation
	public abstract void shouldBePresent();
	
	@DelayElementLocation
	public abstract void shouldNotBePresent();

	public abstract WebElementFacade waitUntilVisible();

	public abstract WebElementFacade waitUntilPresent();

	public abstract Wait<WebDriver> waitForCondition();
	
	@DelayElementLocation
	public abstract WebElementFacade waitUntilNotVisible();

	public abstract String getValue();

	public abstract boolean isSelected();

	public abstract String getText();

	public abstract WebElementFacade waitUntilEnabled();

	public abstract WebElementFacade waitUntilDisabled();

	public abstract String getTextValue();

	/**
	 * Wait for an element to be visible and enabled, and then click on it.
	 */
	public abstract void click();

	public abstract void clear();

	public abstract String toString();

}