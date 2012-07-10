package net.thucydides.core.pages;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.WhenPageOpens;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.components.Dropdown;
import net.thucydides.core.pages.components.FileToUpload;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.scheduling.FluentWaitWithRefresh;
import net.thucydides.core.scheduling.NormalFluentWait;
import net.thucydides.core.scheduling.ThucydidesFluentWait;
import net.thucydides.core.steps.StepDelayer;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webelements.Checkbox;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.isHeadlessDriver;

/**
 * A base class representing a WebDriver page object.
 *
 * @author johnsmart
 */
public abstract class PageObject {

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 250;

    private static final int ONE_SECOND = 1000;

    private long waitForTimeout = 5 * ONE_SECOND;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PageObject.class);

    private static final long WAIT_FOR_TIMEOUT = 30000;

    private WebDriver driver;

    private MatchingPageExpressions matchingPageExpressions;

    private RenderedPageObjectView renderedView;

    private PageUrls pageUrls;

    private net.thucydides.core.pages.SystemClock clock;

    private final Sleeper sleeper;
    private final Clock webdriverClock;
    private final JavascriptExecutorFacade javascriptExecutorFacade;

    private boolean jquerySupportActivated = false;

    public PageObject(final WebDriver driver, final int ajaxTimeout) {
        this.driver = driver;
        this.waitForTimeout = ajaxTimeout;
        this.webdriverClock = new SystemClock();
        this.clock = Injectors.getInjector().getInstance(net.thucydides.core.pages.SystemClock.class);
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);

        setupPageUrls();

        WebDriverFactory.initElementsWithAjaxSupport(this, driver, ajaxTimeout);

    }

    public PageObject(final WebDriver driver) {
        this.driver = driver;
        this.waitForTimeout = WAIT_FOR_TIMEOUT;
        this.webdriverClock = new SystemClock();
        this.clock = Injectors.getInjector().getInstance(net.thucydides.core.pages.SystemClock.class);
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.javascriptExecutorFacade = new JavascriptExecutorFacade(driver);

        setupPageUrls();

        WebDriverFactory.initElementsWithAjaxSupport(this, driver);

    }

    public FileToUpload upload(final String filename) {
        return new FileToUpload(filename);
    }

    private void setupPageUrls() {
        setPageUrls(new PageUrls(this));
    }

    /**
     * Only for testing purposes.
     */
    public void setPageUrls(PageUrls pageUrls) {
        this.pageUrls = pageUrls;
    }

    public void setWaitForTimeout(final long waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
        getRenderedView().setWaitForTimeout(waitForTimeout);
    }

    protected RenderedPageObjectView getRenderedView() {
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, waitForTimeout);
        }
        return renderedView;
    }

    protected net.thucydides.core.pages.SystemClock getClock() {
        return clock;
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


    protected boolean matchesAnyUrl() {
        return thereAreNoPatternsDefined();
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

    public PageObject waitFor(String xpathOrCssSelector) {
        return waitForRenderedElements(xpathOrCssSelector(xpathOrCssSelector));
    }

    public PageObject waitFor(ExpectedCondition expectedCondition) {
        getRenderedView().waitFor(expectedCondition);
        return this;
    }

    public PageObject waitForRenderedElementsToBePresent(final By byElementCriteria) {
        getRenderedView().waitForPresenceOf(byElementCriteria);
        return this;
    }

    public PageObject waitForPresenceOf(String xpathOrCssSelector) {
        return waitForRenderedElementsToBePresent(xpathOrCssSelector(xpathOrCssSelector));
    }


    public PageObject waitForRenderedElementsToDisappear(
            final By byElementCriteria) {
        getRenderedView().waitForElementsToDisappear(byElementCriteria);
        return this;
    }

    public PageObject waitForAbsenceOf(String xpathOrCssSelector) {
        return waitForRenderedElementsToDisappear(xpathOrCssSelector(xpathOrCssSelector));
    }

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToAppear(final String expectedText) {
        getRenderedView().waitForText(expectedText);
        return this;
    }

    public PageObject waitForTitleToAppear(final String expectedTitle) {
        getRenderedView().waitForTitle(expectedTitle);
        return this;
    }

    public PageObject waitForTitleToDisappear(final String expectedTitle) {
        getRenderedView().waitForTitleToDisappear(expectedTitle);
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

    /**
     * Waits for a given text to appear anywhere on the page.
     */
    public PageObject waitForTextToDisappear(final WebElement element,
                                             final String expectedText) {
        waitForCondition().until(elementDoesNotContain(element, expectedText));
        return this;
    }


    private ExpectedCondition<Boolean> elementDoesNotContain(final WebElement element, final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !element.getText().contains(expectedText);
            }
        };
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

    public PageObject waitForAnyRenderedElementOf(final By... expectedElements) {
        getRenderedView().waitForAnyRenderedElementOf(expectedElements);
        return this;
    }

    protected void waitABit(final long timeInMilliseconds) {
        getClock().pauseFor(timeInMilliseconds);
    }

    public StepDelayer.WaitForBuilder waitFor(int duration) {
        return new StepDelayer(clock).waitFor(duration);
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
     * Check that all of the specified texts appears somewhere in the page.
     */
    public void shouldContainAllText(final String... textValues) {
        if (!containsAllText(textValues)) {
            String errorMessage = String.format(
                    "One of the text elements in '%s' was not found in the page", (Object[]) textValues);
            throw new NoSuchElementException(errorMessage);
        }
    }

    /**
     * Does the specified web element contain a given text value. Useful for dropdowns and so on.
     *
     * @deprecated use element(webElement).containsText(textValue)
     */
    @Deprecated
    public boolean containsTextInElement(final WebElement webElement, final String textValue) {
        return element(webElement).containsText(textValue);
    }

    /*
     * Check that the element contains a given text.
     * @deprecated use element(webElement).shouldContainText(textValue)
     */
    @Deprecated
    public void shouldContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldContainText(textValue);
    }

    /*
     * Check that the element does not contain a given text.
     * @deprecated use element(webElement).shouldNotContainText(textValue)
     */
    @Deprecated
    public void shouldNotContainTextInElement(final WebElement webElement, final String textValue) {
        element(webElement).shouldNotContainText(textValue);
    }

    /**
     * Clear a field and enter a value into it.
     */
    public void typeInto(final WebElement field, final String value) {
        element(field).type(value);
    }

    /**
     * Clear a field and enter a value into it.
     * This is a more fluent alternative to using the typeInto method.
     */
    public FieldEntry enter(final String value) {
        return new FieldEntry(value);
    }

    public void selectFromDropdown(final WebElement dropdown,
                                   final String visibleLabel) {

        Dropdown.forWebElement(dropdown).select(visibleLabel);
        notifyScreenChange();
    }

    public void selectMultipleItemsFromDropdown(final WebElement dropdown,
                                                final String... selectedLabels) {
        Dropdown.forWebElement(dropdown).selectMultipleItems(selectedLabels);
        notifyScreenChange();
    }


    public Set<String> getSelectedOptionLabelsFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionLabels();
    }

    public Set<String> getSelectedOptionValuesFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedOptionValues();
    }

    public String getSelectedValueFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedValue();
    }

    public String getSelectedLabelFrom(final WebElement dropdown) {
        return Dropdown.forWebElement(dropdown).getSelectedLabel();
    }

    public void setCheckbox(final WebElement field, final boolean value) {
        Checkbox checkbox = new Checkbox(field);
        checkbox.setChecked(value);
        notifyScreenChange();
    }

    public boolean containsText(final String textValue) {
        return getRenderedView().containsText(textValue);
    }

    /**
     * Check that the specified text appears somewhere in the page.
     */
    public boolean containsAllText(final String... textValues) {
        for (String textValue : textValues) {
            if (!getRenderedView().containsText(textValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fail the test if this element is not displayed (rendered) on the screen.
     */
    public void shouldBeVisible(final WebElement field) {
        element(field).shouldBeVisible();
    }

    public void shouldBeVisible(final By byCriteria) {
        WebElement element = getDriver().findElement(byCriteria);
        shouldBeVisible(element);
    }

    public void shouldNotBeVisible(final WebElement field) {
        try {
            element(field).shouldNotBeVisible();
        } catch (NoSuchElementException e) {
            // A non-existant element is not visible
        }
    }

    public void shouldNotBeVisible(final By byCriteria) {
        try {
            WebElement element = getDriver().findElement(byCriteria);
            shouldNotBeVisible(element);
        } catch (NoSuchElementException e) {
            // A non-existant element is not visible
        }
    }

    public String updateUrlWithBaseUrlIfDefined(final String startingUrl) {

        String baseUrl = pageUrls.getBaseUrl();
        if ((baseUrl != null) && (!StringUtils.isEmpty(baseUrl))) {
            return replaceHost(startingUrl, baseUrl);
        } else {
            return startingUrl;
        }
    }

    private String replaceHost(final String starting, final String base) {

        String updatedUrl = starting;

        try {
            URL startingUrl = new URL(starting);
            URL baseUrl = new URL(base);

            String startingHostComponent = hostComponentFrom(startingUrl.getProtocol(),
                    startingUrl.getHost(),
                    startingUrl.getPort());
            String baseHostComponent = hostComponentFrom(baseUrl.getProtocol(),
                    baseUrl.getHost(),
                    baseUrl.getPort());
            updatedUrl = starting.replaceFirst(startingHostComponent, baseHostComponent);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to analyse default page URL: Starting URL: {}, Base URL: {}", starting, base);
            LOGGER.error("URL analysis failed with exception:", e);
        }

        return updatedUrl;
    }

    private String hostComponentFrom(final String protocol, final String host, final int port) {
        StringBuffer hostComponent = new StringBuffer(protocol);
        hostComponent.append("://");
        hostComponent.append(host);
        if (port > 0) {
            hostComponent.append(":");
            hostComponent.append(port);
        }
        return hostComponent.toString();
    }

    /**
     * Open the webdriver browser using a paramaterized URL. Parameters are
     * represented in the URL using {0}, {1}, etc.
     */
    public final void open(final String... parameterValues) {
        String startingUrl = pageUrls.getStartingUrl(parameterValues);
        LOGGER.debug("Opening page at url {}", startingUrl);
        openPageAtUrl(startingUrl);
        callWhenPageOpensMethods();
        LOGGER.debug("Page opened");
    }

    public final void open(final String urlTemplateName,
                           final String[] parameterValues) {
        String startingUrl = pageUrls.getNamedUrl(urlTemplateName, parameterValues);
        LOGGER.debug("Opening page at url {}", startingUrl);
        openPageAtUrl(startingUrl);
        callWhenPageOpensMethods();
        LOGGER.debug("Page opened");
    }

    /**
     * Open the webdriver browser to the base URL, determined by the DefaultUrl
     * annotation if present. If the DefaultUrl annotation is not present, the
     * default base URL will be used. If the DefaultUrl annotation is present, a
     * URL based on the current base url from the system-wide defulat url
     * and the relative path provided in the DefaultUrl annotation will be used to
     * determine the URL to open. For example, consider the following class:
     * <pre>
     *     <code>
     *         @DefaultUrl("http://localhost:8080/client/list")
     *         public class ClientList extends PageObject {
     *             ...
     *
     *             @WhenPageOpens
     *             public void waitUntilTitleAppears() {...}
     *         }
     *     </code>
     * </pre>
     * <p/>
     * Suppose you are using a base URL of http://stage.acme.com. When you call open() for this class,
     * it will open http://stage.acme.com/client/list. It will then invoke the waitUntilTitleAppears() method.
     */
    final public void open() {
        String startingUrl = updateUrlWithBaseUrlIfDefined(pageUrls.getStartingUrl());
        openPageAtUrl(startingUrl);
        callWhenPageOpensMethods();
    }

    /**
     * Override this method
     */
    public void callWhenPageOpensMethods() {
        for (Method annotatedMethod : methodsAnnotatedWithWhenPageOpens()) {
            try {
                annotatedMethod.setAccessible(true);
                annotatedMethod.invoke(this);
            } catch (Exception e) {
                LOGGER.error("Could not execute @WhenPageOpens annotated method: " + e.getMessage());
                throw new UnableToInvokeWhenPageOpensMethods("Could not execute @WhenPageOpens annotated method: "
                        + e.getMessage(), e);
            }
        }

    }

    private List<Method> methodsAnnotatedWithWhenPageOpens() {
        Method[] methods = this.getClass().getDeclaredMethods();
        List<Method> annotatedMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getAnnotation(WhenPageOpens.class) != null) {
                if (method.getParameterTypes().length == 0) {
                    annotatedMethods.add(method);
                } else {
                    throw new UnableToInvokeWhenPageOpensMethods("Could not execute @WhenPageOpens annotated method: WhenPageOpens method cannot have parameters: " + method);
                }
            }
        }
        return annotatedMethods;
    }

    public static String[] withParameters(final String... parameterValues) {
        return parameterValues;
    }

    private void openPageAtUrl(final String startingUrl) {
        getDriver().get(startingUrl);
        addJQuerySupport();
    }

    public void clickOn(final WebElement webElement) {
        element(webElement).click();
    }

    /**
     * Returns true if at least one matching element is found on the page and is visible.
     */
    public Boolean isElementVisible(final By byCriteria) {
        return getRenderedView().elementIsDisplayed(byCriteria);
    }

    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        pageUrls.overrideDefaultBaseUrl(defaultBaseUrl);
    }

    /**
     * Returns true if the specified element has the focus.
     *
     * @deprecated Use element(webElement).hasFocus() instead
     */
    public boolean hasFocus(final WebElement webElement) {
        return element(webElement).hasFocus();
    }

    public void blurActiveElement() {
        getJavascriptExecutorFacade().executeScript("document.activeElement.blur();");
    }

    protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
        return javascriptExecutorFacade;
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public WebElementFacade element(WebElement webElement) {
        return new WebElementFacade(driver, webElement, waitForTimeout);
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public WebElementFacade element(By bySelector) {
        WebElement webElement = getDriver().findElement(bySelector);
        return new WebElementFacade(driver, webElement, waitForTimeout);
    }

    public WebElementFacade find(By selector) {
        return element(selector);
    }

    /**
     * Provides a fluent API for querying web elements.
     */
    public WebElementFacade element(String xpathOrCssSelector) {
        return element(xpathOrCssSelector(xpathOrCssSelector));
    }

    private By xpathOrCssSelector(String xpathOrCssSelector) {
        if (isXPath(xpathOrCssSelector)) {
            return By.xpath(xpathOrCssSelector);
        } else {
            return By.cssSelector(xpathOrCssSelector);
        }
    }

    public WebElementFacade findBy(String xpathOrCssSelector) {
        return element(xpathOrCssSelector);
    }

    public static boolean isXPath(String xpathExpression) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            xpath.compile(xpathExpression);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Object evaluateJavascript(final String script) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeScript(script);
    }

    public Object evaluateJavascript(final String script, final Object... params) {
        addJQuerySupport();
        JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
        return js.executeScript(script, params);
    }

    public void addJQuerySupport() {
        JQueryEnabledPage jQueryEnabledPage = JQueryEnabledPage.withDriver(getDriver());
        if (!jQueryEnabledPage.isJQueryEnabled()) {
            jQueryEnabledPage.injectJQuery();
            jQueryEnabledPage.injectJQueryPlugins();
        }
        jquerySupportActivated = true;
    }


    private boolean driverIsEnabled() {
        if (WebDriverFacade.class.isAssignableFrom(getDriver().getClass())) {
            WebDriverFacade driverFacade = (WebDriverFacade) getDriver();
            return driverFacade.isEnabled();
        }
        return true;
    }

    public ThucydidesFluentWait<WebDriver> waitForWithRefresh() {
        return new FluentWaitWithRefresh<WebDriver>(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeout, TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public ThucydidesFluentWait<WebDriver> waitForCondition() {
        return new NormalFluentWait<WebDriver>(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeout, TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public Alert getAlert() {
        return driver.switchTo().alert();
    }

    public Actions withAction() {
        WebDriver proxiedDriver = ((WebDriverFacade) getDriver()).getProxiedDriver();
        return new Actions(proxiedDriver);
    }

    public class FieldEntry {

        private final String value;

        public FieldEntry(final String value) {
            this.value = value;
        }

        public void into(final WebElement field) {
            element(field).type(value);
        }

        public void intoField(final By bySelector) {
            WebElement field = getDriver().findElement(bySelector);
            element(field).type(value);

        }
    }

    private void notifyScreenChange() {
        StepEventBus.getEventBus().notifyScreenChange();
    }

}
