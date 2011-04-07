package net.thucydides.core.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.webelements.Checkbox;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
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

    private static final int TIMEOUT = 120;

    private long waitForTimeout = WAIT_FOR_ELEMENT_PAUSE_LENGTH;

    private static final String OPTIONAL_PARAMS = "/?(\\?.*)?";

    private static final Map<String, String> MACROS = new HashMap<String, String>();

    private static final Logger LOGGER = LoggerFactory.getLogger(PageObject.class);

    private static final long WAIT_FOR_TIMEOUT = 30000;

    {
        MACROS.put("#HOST", "https?://[^/]+");
    }

    private WebDriver driver;

    private List<Pattern> matchingPageExpressions = new ArrayList<Pattern>();

    private RenderedPageObjectView renderedView;

    private String defaultBaseUrl;

    public PageObject(final WebDriver driver) {
        ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver,
                TIMEOUT);
        this.driver = driver;
        this.waitForTimeout = WAIT_FOR_TIMEOUT;
        PageFactory.initElements(finder, this);
        fetchMatchingPageExpressions();
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }

    public void setDefaultBaseUrl(String baseUrl) {
        this.defaultBaseUrl = baseUrl;
    }

    public String getDefaultBaseUrl() {
        if (defaultBaseUrl == null) {
            return PageConfiguration.getCurrentConfiguration().getBaseUrl();
        } else {
            return defaultBaseUrl;
        }
    }

    private RenderedPageObjectView getRenderedView() {
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, waitForTimeout);
        }
        return renderedView;
    }

    private void fetchMatchingPageExpressions() {
        At compatibleWithAnnotation = this.getClass().getAnnotation(At.class);
        if (compatibleWithAnnotation != null) {
            if (valueIsDefinedFor(compatibleWithAnnotation)) {
                worksWithUrlPattern(compatibleWithAnnotation.value());
            } else {
                worksWithUrlPatternList(compatibleWithAnnotation.urls());
            }
        }

    }

    private void worksWithUrlPatternList(final String[] urls) {
        for (String url : urls) {
            worksWithUrlPattern(url);
        }
    }

    private boolean valueIsDefinedFor(final At compatibleWithAnnotation) {
        return ((compatibleWithAnnotation.value() != null) && (compatibleWithAnnotation
                .value().length() > 0));
    }

    private void worksWithUrlPattern(final String urlPattern) {
        String processedUrlPattern = substituteMacrosIn(urlPattern);
        matchingPageExpressions.add(Pattern.compile(processedUrlPattern));
    }

    private String substituteMacrosIn(final String urlPattern) {
        String patternWithExpandedMacros = urlPattern;
        for (String macro : MACROS.keySet()) {
            String expanded = MACROS.get(macro);
            patternWithExpandedMacros = patternWithExpandedMacros.replaceAll(
                    macro, expanded);
        }
        patternWithExpandedMacros = patternWithExpandedMacros + OPTIONAL_PARAMS;
        return patternWithExpandedMacros;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
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
        boolean pageWorksHere = false;
        for (Pattern pattern : matchingPageExpressions) {
            if (urlIsCompatibleWithThisPattern(currentUrl, pattern)) {
                pageWorksHere = true;
                break;
            }
        }
        return pageWorksHere;
    }

    private boolean thereAreNoPatternsDefined() {
        return matchingPageExpressions.isEmpty();
    }

    private boolean urlIsCompatibleWithThisPattern(final String currentUrl,
                                                   final Pattern pattern) {
        return pattern.matcher(currentUrl).matches();
    }

    public PageObject waitForRenderedElements(final By byElementCriteria) {
        getRenderedView().waitFor(byElementCriteria);
        return this;
    }

    public PageObject waitForRenderedElementsToDisappear(final By byElementCriteria) {
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
    public PageObject waitForTextToAppear(WebElement element, final String expectedText) {
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
     * Waits for any of a number of text blocks to appear anywhere on the screen
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
     * Waits for all of a number of text blocks to appear somewhere on the
     * screen
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
            String optionPath = String
                    .format("//option[.='%s']", selectedLabel);
            WebElement option = dropdown.findElement(By.xpath(optionPath));
            option.click();
        }
    }

    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        Set<String> selectedOptions = new HashSet<String>();

        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getText());
            }
        }
        return selectedOptions;
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        Set<String> selectedOptions = new HashSet<String>();

        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getValue());
            }
        }
        return selectedOptions;
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
            throw new AssertionError("The " + field + " element should be visible");
        }
    }

    /**
     * Open the webdriver browser to the base URL, determined by the DefaultUrl annotation if present.
     * If the DefaultUrl annotation is not present, the default base URL will be used.
     */
    public void open() {
        String startingUrl = startFromUrlAnnotationOrBaseUrl();
        getDriver().get(startingUrl);
    }

    /**
     * Open the webdriver browser using a paramaterized URL.
     * Parameters are represented in the URL using {0}, {1}, etc.
     */
    public void open(final String... parameterValues) {
        String startingUrlTemplate = startFromUrlAnnotationOrBaseUrl();
        String startingUrl = urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
        getDriver().get(startingUrl);
    }

    public static String[] withParameters(String... parameterValues) {
        return parameterValues;
    }

    public void open(final String urlTemplateName, final String[] parameterValues) {
        String startingUrlTemplate = getNamedUrl(urlTemplateName);
        String startingUrl = urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
        getDriver().get(startingUrl);
    }

    /**
     * Returns true if at least one matching element is found on the page and is visible.
     */
    public Boolean isElementVisible(By byCriteria) {
        return getRenderedView().elementIsDisplayed(byCriteria);
    }

    private String urlWithParametersSubstituted(final String template, final String[] parameterValues) {

        String url = template;
        for (int i = 0; i < parameterValues.length; i++) {
            String variable = String.format("{%d}", i + 1);
            url = url.replace(variable, parameterValues[i]);
        }
        return addDefaultBaseUrlIfRelative(url);
    }

    private String startFromUrlAnnotationOrBaseUrl() {
        DefaultUrl urlAnnotation = getClass().getAnnotation(DefaultUrl.class);
        if (urlAnnotation != null) {
            String annotatedBaseUrl = urlAnnotation.value();
            return addDefaultBaseUrlIfRelative(annotatedBaseUrl);
        }

        return getDefaultBaseUrl();
    }

    private String addDefaultBaseUrlIfRelative(String url) {
        if (isARelativeUrl(url)) {
            return getDefaultBaseUrl() + url;
        } else {
            return url;
        }
    }

    private boolean isARelativeUrl(String url) {
        return url.startsWith("/");
    }

    private String getNamedUrl(final String name) {
        NamedUrls urlAnnotation = getClass().getAnnotation(NamedUrls.class);
        if (urlAnnotation != null) {
            NamedUrl[] namedUrlList = urlAnnotation.value();
            for (NamedUrl namedUrl : namedUrlList) {
                if (namedUrl.name().equals(name)) {
                    return namedUrl.url();
                }
            }
        }
        throw new IllegalArgumentException("No URL named " + name + " was found in this class");
    }
}
