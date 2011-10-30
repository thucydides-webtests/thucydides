package net.thucydides.core.pages;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * The Pages object keeps track of what web pages a test visits, and helps with mapping pages to Page Objects.
 * A Pages object is associated with a WebDriver driver instance, so you need a Pages object for any
 * given WebDriver driver.
 *
 * @author johnsmart
 */
public class Pages implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(Pages.class);

    private String defaultBaseUrl;

    private final PageConfiguration pageConfiguration;

    private WebdriverProxyFactory proxyFactory;

    private transient boolean usePreviousPage = false;

    public Pages(PageConfiguration pageConfiguration) {
        this.pageConfiguration = pageConfiguration;
        proxyFactory = WebdriverProxyFactory.getFactory();
    }

    public Pages() {
        this(Injectors.getInjector().getInstance(PageConfiguration.class));
    }

    public Pages(final WebDriver driver) {
        this(Injectors.getInjector().getInstance(PageConfiguration.class));
        this.driver = driver;
    }

    public Pages(final WebDriver driver, PageConfiguration pageConfiguration) {
        this(pageConfiguration);
        this.driver = driver;
    }

    public PageConfiguration getPageConfiguration() {
        return pageConfiguration;
    }

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    /**
     * Opens a browser on the application home page, as defined by the base URL.
     */
    protected void start() {
        getDriver().get(getStartingUrl());
    }

    PageObject currentPage = null;

    public <T extends PageObject> T getAt(final Class<T> pageObjectClass) {
        return currentPageAt(pageObjectClass);
    }

    @SuppressWarnings("unchecked")
	public <T extends PageObject> T get(final Class<T> pageObjectClass) {
        T nextPage = null;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = (T) getCurrentPageOfType(pageObjectClass);
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
        }
        usePreviousPage = false;
        return nextPage;
    }

    @SuppressWarnings("unchecked")
    public <T extends PageObject> T currentPageAt(final Class<T> pageObjectClass) {
        T nextPage = null;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = (T) getCurrentPageOfType(pageObjectClass);
            if (!pageCandidate.matchesAnyUrl()) {
                String currentUrl = getDriver().getCurrentUrl();
                if (!pageCandidate.compatibleWithUrl(currentUrl)) {
                    thisIsNotThePageYourLookingFor(pageObjectClass);
                }
            }
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
        }
        usePreviousPage = false;
        return nextPage;
    }

    private <T extends PageObject> boolean shouldUsePreviousPage(final Class<T> pageObjectClass) {
        if (!usePreviousPage) {
            return false;
        } else {
            return currentPageIsSameTypeAs(pageObjectClass);
        }
    }

    private void cacheCurrentPage(PageObject newPage) {
        this.currentPage = newPage;
    }

    private <T extends PageObject> boolean currentPageIsSameTypeAs(Class<T> pageObjectClass) {
        return (currentPage != null) && (currentPage.getClass().equals(pageObjectClass));
    }

    public boolean isCurrentPageAt(final Class<? extends PageObject> pageObjectClass) {
        try {
            PageObject pageCandidate = getCurrentPageOfType(pageObjectClass);
            String currentUrl = getDriver().getCurrentUrl();
            return pageCandidate.compatibleWithUrl(currentUrl);
        } catch (WrongPageError e) {
            return false;
        }
    }


    /**
     * Create a new Page Object of the given type.
     * The Page Object must have a constructor
     *
     * @param pageObjectClass
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    private <T extends PageObject> T getCurrentPageOfType(final Class<T> pageObjectClass) {
        T currentPage = null;
        try {
            @SuppressWarnings("rawtypes")
            Class[] constructorArgs = new Class[1];
            constructorArgs[0] = WebDriver.class;
            Constructor<? extends PageObject> constructor
                    = (Constructor<? extends PageObject>) pageObjectClass.getConstructor(constructorArgs);
            currentPage = (T) constructor.newInstance(driver);
        } catch (NoSuchMethodException e) {
            LOGGER.info("This page object does not appear have a constructor that takes a WebDriver parameter: "
                    + pageObjectClass, e);
            thisIsNotThePageYourLookingFor(pageObjectClass);
        } catch (Exception e) {
            LOGGER.info("Failed to instantiate page of type " + pageObjectClass, e);
            thisIsNotThePageYourLookingFor(pageObjectClass);
        }
        return currentPage;
    }

    private void thisIsNotThePageYourLookingFor(final Class<? extends PageObject> pageObjectClass) {

        String errorDetails = "This is not the page you're looking for:\n"
                + "I was looking for a page compatible with " + pageObjectClass + "\n"
                + "I was at the URL " + getDriver().getCurrentUrl();

        throw new WrongPageError(errorDetails);
    }

    /**
     * The default URL for this set of tests, or the system default URL if undefined.
     */
    public String getDefaultBaseUrl() {

        String baseUrl = defaultBaseUrl;
        if (isNotEmpty(pageConfiguration.getBaseUrl())) {
            baseUrl = pageConfiguration.getBaseUrl();
        }
        return baseUrl;
    }

    /**
     * Set a default base URL for a specific set of tests.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getStartingUrl() {
        return PageUrls.getUrlFrom(getDefaultBaseUrl());
    }

    public void notifyWhenDriverOpens() {
        PagesEventListener eventListener = new PagesEventListener(this);
        if ((getDriver() != null) && !usingProxiedWebDriver()) {
            start();
        }
        getProxyFactory().registerListener(eventListener);
    }

    private boolean usingProxiedWebDriver() {
        return (getDriver() instanceof WebDriverFacade);
    }

    public Pages onSamePage() {
        usePreviousPage = true;
        return this;
    }
}
