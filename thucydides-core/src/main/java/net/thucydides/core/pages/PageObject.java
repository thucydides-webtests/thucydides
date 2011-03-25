package net.thucydides.core.pages;

import java.util.*;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.thucydides.core.annotations.At;
import net.thucydides.core.webelements.Checkbox;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ElementNotDisplayedException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.Select;

import com.google.common.collect.ImmutableList;

/**
 * A base class representing a WebDriver page object.
 * 
 * @author johnsmart
 * 
 */
public abstract class PageObject {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final int TIMEOUT = 60;
    
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

    public PageObject(final WebDriver driver) {
        ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, TIMEOUT);
        this.driver = driver;
        this.waitForTimeout = WAIT_FOR_TIMEOUT;
        PageFactory.initElements(finder, this);
        fetchMatchingPageExpressions();
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
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
        return ((compatibleWithAnnotation.value() != null) && (compatibleWithAnnotation.value()
                .length() > 0));
    }

    private void worksWithUrlPattern(final String urlPattern) {
        String processedUrlPattern = substituteMacrosIn(urlPattern);
        matchingPageExpressions.add(Pattern.compile(processedUrlPattern));
    }

    private String substituteMacrosIn(final String urlPattern) {
        String patternWithExpandedMacros = urlPattern;
        for (String macro : MACROS.keySet()) {
            String expanded = MACROS.get(macro);
            patternWithExpandedMacros = patternWithExpandedMacros.replaceAll(macro, expanded);
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
     * 
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

    private boolean urlIsCompatibleWithThisPattern(final String currentUrl, final Pattern pattern) {
        return pattern.matcher(currentUrl).matches();
    }
    
    public PageObject waitForRenderedElements(final By byElementCriteria) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (elementIsDisplayed(byElementCriteria)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        checkThatElementAppeared(byElementCriteria);
        checkThatElementIsDisplayed(byElementCriteria);
        return this;
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(String expectedText) {
        long end = System.currentTimeMillis() + waitForTimeout;
        while (System.currentTimeMillis() < end) {
            if (containsText(expectedText)) {
                break;
            }
            waitABit(WAIT_FOR_ELEMENT_PAUSE_LENGTH);
        }
        if (!containsText(expectedText)) {
            throw new ElementNotDisplayedException("Expected text was not displayed: '" + expectedText + "'");
        }
        return this;
    }
    
    /**
     * Waits for any of a number of text blocks to appear anywhere on the screen
     * @param expectedText
     * @return
     */
    public PageObject waitForAnyTextToAppear(String... expectedText) {
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
        return this;
    }

    /**
     * Waits for all of a number of text blocks to appear somewhere on the screen
     * @param expectedText
     * @return
     */
    public PageObject waitForAllTextToAppear(String... expectedTexts) {
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
            throw new ElementNotDisplayedException("Expected text was not displayed: '" + requestedTexts + "'");
        }
        return this;
    }

    private List<String> buildInitialListOfExpectedTextsFrom(
            String... expectedTexts) {
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

    private boolean pageContains(String... expectedTexts) {
        for(String expectedText : expectedTexts) {
            if (containsText(expectedText)) {
                return true;
            }
        }
        return false;
    }

    private void checkThatElementIsDisplayed(final By byElementCriteria) {
        if (!elementIsDisplayed(byElementCriteria)) {
            throw new ElementNotDisplayedException("Element not displayed: " + byElementCriteria);
        }
    }

    protected void waitABit(final long timeInMilliseconds) {
        try {
            Thread.sleep(timeInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean elementIsDisplayed(final By byElementCriteria) {
        boolean isDisplayed = false;
        try {
            RenderedWebElement renderedElement = (RenderedWebElement) driver.findElement(byElementCriteria);
            isDisplayed = renderedElement.isDisplayed();
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.info("No such element " + noSuchElement);
        }
        return isDisplayed;
    }

    private void checkThatElementAppeared(final By byElementCriteria) {
        driver.findElement(byElementCriteria);
    }

    public List<WebElement> thenReturnElementList(final By byListCriteria) {
        return driver.findElements(byListCriteria);
    }    
    
    /**
     * Check that the specified text appears somewhere in the page.
     */
    public void shouldContainText(final String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format("The text '%s' was not found in the page", textValue);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Clear a field and enter a value into it.
     */
    public void typeInto(WebElement field, String value) {
        field.clear();
        field.sendKeys(value);
    }
    
    public void selectFromDropdown(final WebElement dropdown, final String visibleLabel) {
        Select dropdownSelect = findSelectFor(dropdown);
        dropdownSelect.selectByVisibleText(visibleLabel);
    }

    public void selectMultipleItemsFromDropdown(WebElement dropdown, String... selectedLabels) {
        for(String selectedLabel : selectedLabels) {
            String optionPath = String.format("//option[.='%s']", selectedLabel);
            WebElement option = dropdown.findElement(By.xpath(optionPath));
            option.click();
        }
    }

    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        Set<String> selectedOptions = new HashSet<String>();

        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        for(WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getText());
            }
        }
        return selectedOptions;
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        Set<String> selectedOptions = new HashSet<String>();

        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        for(WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getValue());
            }
        }
        return selectedOptions;
    }

    public void setCheckbox(WebElement field, boolean value) {
        Checkbox checkbox = new Checkbox(field);
        checkbox.setChecked(value);
    }
    
    protected Select findSelectFor(WebElement dropdownList) {
        return new Select(dropdownList);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public boolean containsText(final String textValue) {
        String textInBody = String.format("//body[contains(.,\"%s\")]", textValue);
        List<WebElement> elements = driver.findElements(By.xpath(textInBody));
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
    
    public void shouldBeVisible(final WebElement field) {
        if (!userCanSee(field)) {
            throw new AssertionError("The " + field + " element should be visible");
        }
    }
}
