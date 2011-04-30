package net.thucydides.core.pages;

import java.util.List;
import java.util.Set;

import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webelements.Checkbox;

import net.thucydides.core.webelements.MultipleSelect;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class representing a WebDriver page object.
 *
 * @author johnsmart
 */
public abstract class PageObject {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final int ONE_SECOND = 1000;

    private long waitForTimeout = WAIT_FOR_ELEMENT_PAUSE_LENGTH;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PageObject.class);

    private static final long WAIT_FOR_TIMEOUT = 30000;

    private WebDriver driver;

    private MatchingPageExpressions matchingPageExpressions;

    private RenderedPageObjectView renderedView;

    private PageUrls pageUrls;

    public PageObject(final WebDriver driver) {
        this.driver = driver;
        this.waitForTimeout = WAIT_FOR_TIMEOUT;

        setupPageUrls();

        WebDriverFactory.initElementsWithAjaxSupport(this, driver);

    }

    private void setupPageUrls() {
        pageUrls = new PageUrls(this);
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }

    private RenderedPageObjectView getRenderedView() {
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, waitForTimeout);
        }
        return renderedView;
    }

    private MatchingPageExpressions getMatchingPageExpressions() {
        if (matchingPageExpressions == null) {
            matchingPageExpressions = new MatchingPageExpressions(this);
        }
        return matchingPageExpressions;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }


    /**
     * Does this page object work for this URL? When matching a URL, we check
     * with and without trailing slashes
     */
    public final boolean compatibleWithUrl(final String currentUrl) {
        if (thereAreNoPatternsDefined()) {
            return true;
        } else {
            return matchUrlAgainstEachPattern(currentUrl);
        }
    }

    private boolean matchUrlAgainstEachPattern(final String currentUrl) {
        return getMatchingPageExpressions().matchUrlAgainstEachPattern(currentUrl);
    }

    private boolean thereAreNoPatternsDefined() {
        return getMatchingPageExpressions().isEmpty();
    }

    public PageObject waitForRenderedElements(final By byElementCriteria) {
        getRenderedView().waitFor(byElementCriteria);
        return this;
    }

    public PageObject waitForRenderedElementsToDisappear(
            final By byElementCriteria) {
        getRenderedView().waitForElementsToDisappear(byElementCriteria);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final String expectedText) {
        getRenderedView().waitForText(expectedText);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final WebElement element,
                                          final String expectedText) {
        getRenderedView().waitForText(element, expectedText);
        return this;
    }

    public PageObject waitForTextToDisappear(final String expectedText) {
        return waitForTextToDisappear(expectedText, waitForTimeout);
    }

    /**
     * Waits for a given text to not be anywhere on the page.
     */
    public PageObject waitForTextToDisappear(final String expectedText,
                                             final long timeout) {
        getRenderedView().waitForTextToDisappear(expectedText, timeout);
        return this;
    }

    /**
     * Waits for any of a number of text blocks to appear anywhere on the
     * screen.
     */
    public PageObject waitForAnyTextToAppear(final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(expectedText);
        return this;
    }

    public PageObject waitForAnyTextToAppear(final WebElement element,
                                             final String... expectedText) {
        getRenderedView().waitForAnyTextToAppear(element, expectedText);
        return this;
    }

    /**
     * Waits for all of a number of text blocks to appear on the screen.
     */
    public PageObject waitForAllTextToAppear(final String... expectedTexts) {
        getRenderedView().waitForAllTextToAppear(expectedTexts);
        return this;
    }

    protected void waitABit(final long timeInMilliseconds) {
        try {
            Thread.sleep(timeInMilliseconds);
        } catch (InterruptedException e) {
            LOGGER.error("Wait interrupted", e);
        }
    }

    public List<WebElement> thenReturnElementList(final By byListCriteria) {
        return driver.findElements(byListCriteria);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public void shouldContainText(final String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the page", textValue);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Clear a field and enter a value into it.
     */
    public void typeInto(final WebElement field, final String value) {
        field.clear();
        field.sendKeys(value);
    }

    public void selectFromDropdown(final WebElement dropdown,
                                   final String visibleLabel) {
        Select dropdownSelect = findSelectFor(dropdown);
        dropdownSelect.selectByVisibleText(visibleLabel);
    }

    public void selectMultipleItemsFromDropdown(final WebElement dropdown,
                                                final String... selectedLabels) {
        for (String selectedLabel : selectedLabels) {
            String optionPath = String.format("//option[.='%s']", selectedLabel);
            WebElement option = dropdown.findElement(By.xpath(optionPath));
            option.click();
        }
    }


    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        MultipleSelect multipleSelect = new MultipleSelect(dropdown);
        return multipleSelect.getSelectedOptionLabels();
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        MultipleSelect multipleSelect = new MultipleSelect(dropdown);
        return multipleSelect.getSelectedOptionValues();
    }

    public void setCheckbox(final WebElement field, final boolean value) {
        Checkbox checkbox = new Checkbox(field);
        checkbox.setChecked(value);
    }

    protected Select findSelectFor(final WebElement dropdownList) {
        return new Select(dropdownList);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public boolean containsText(final String textValue) {
        return getRenderedView().containsText(textValue);

    }

    public boolean userCanSee(final WebElement field) {
        return getRenderedView().userCanSee(field);
    }

    public void shouldBeVisible(final WebElement field) {
        if (!userCanSee(field)) {
            throw new AssertionError("The " + field
                    + " element should be visible");
        }
    }

    /**
     * Open the webdriver browser to the base URL, determined by the DefaultUrl
     * annotation if present. If the DefaultUrl annotation is not present, the
     * default base URL will be used.
     */
    public void open() {
        String startingUrl = pageUrls.getStartingUrl();
        getDriver().get(startingUrl);
    }

    /**
     * Open the webdriver browser using a paramaterized URL. Parameters are
     * represented in the URL using {0}, {1}, etc.
     */
    public void open(final String... parameterValues) {
        String startingUrl = pageUrls.getStartingUrl(parameterValues);
        getDriver().get(startingUrl);
    }

    public static String[] withParameters(final String... parameterValues) {
        return parameterValues;
    }

    public void open(final String urlTemplateName,
                     final String[] parameterValues) {
        String startingUrl = pageUrls.getNamedUrl(urlTemplateName, parameterValues);
        getDriver().get(startingUrl);
    }

    public void clickOn(final WebElement webElement) {
        try {
            webElement.click();
        } catch (WebDriverException e) {
            LOGGER.warn(
                    "Click failed. This could be a flicking failure, so I'll wait 1 second and try again",
                    e);
            waitABit(ONE_SECOND);
        }
        webElement.click();

    }

    /**
     * Returns true if at least one matching element is found on the page and is
     * visible.
     */
    public Boolean isElementVisible(final By byCriteria) {
        return getRenderedView().elementIsDisplayed(byCriteria);
    }

    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        pageUrls.overrideDefaultBaseUrl(defaultBaseUrl);
    }
}
