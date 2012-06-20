package net.thucydides.core.pages;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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

    private final Configuration configuration;

    private WebdriverProxyFactory proxyFactory;

    private transient boolean usePreviousPage = false;

    public Pages(Configuration configuration) {
        this.configuration = configuration;
        proxyFactory = WebdriverProxyFactory.getFactory();
    }

    public Pages() {
        this(Injectors.getInjector().getInstance(Configuration.class));
    }

    public Pages(final WebDriver driver) {
        this(Injectors.getInjector().getInstance(Configuration.class));
        this.driver = driver;
    }

    public Pages(final WebDriver driver, Configuration Configuration) {
        this(Configuration);
        this.driver = driver;
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
    public void start() {
        String startingUrl = getStartingUrl();
        if (!currentUrlIs(startingUrl)) {
            getDriver().get(startingUrl);
        }
    }

    private boolean currentUrlIs(String startingUrl) {
        String currentUrl = getDriver().getCurrentUrl();
        if (currentUrl == null || startingUrl == null) {
            return false;
        }

        try {
            return URI.create(currentUrl).equals(URI.create(startingUrl));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    PageObject currentPage = null;

    PagesEventListener eventListener = null;

    public <T extends PageObject> T getAt(final Class<T> pageObjectClass) {
        return currentPageAt(pageObjectClass);
    }

    public Configuration getConfiguration() {
       return configuration;
    }

    @SuppressWarnings("unchecked")
	public <T extends PageObject> T get(final Class<T> pageObjectClass) {
        T nextPage;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = getCurrentPageOfType(pageObjectClass);
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
            //nextPage.addJQuerySupport();
        }
        usePreviousPage = false;
        return nextPage;
    }

    @SuppressWarnings("unchecked")
    public <T extends PageObject> T currentPageAt(final Class<T> pageObjectClass) {
        T nextPage;
        if (shouldUsePreviousPage(pageObjectClass)) {
            nextPage = (T) currentPage;
        } else {
            T pageCandidate = getCurrentPageOfType(pageObjectClass);
            if (!pageCandidate.matchesAnyUrl()) {
                String currentUrl = getDriver().getCurrentUrl();
                if (!pageCandidate.compatibleWithUrl(currentUrl)) {
                    thisIsNotThePageYourLookingFor(pageObjectClass);
                }
            }
            pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());
            cacheCurrentPage(pageCandidate);
            nextPage = pageCandidate;
            nextPage.addJQuerySupport();
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
            Class[] constructorArgs = new Class[1];
            constructorArgs[0] = WebDriver.class;
            Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
            currentPage = (T) constructor.newInstance(driver);
        } catch (NoSuchMethodException e) {
            LOGGER.info("This page object does not appear have a constructor that takes a WebDriver parameter: {} ({})",
                    pageObjectClass, e.getMessage());
            thisIsNotThePageYourLookingFor(pageObjectClass);
        } catch (Exception e) {
            LOGGER.info("Failed to instantiate page of type {} ({})", pageObjectClass, e.getMessage());
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
        if (isNotEmpty(getConfiguration().getBaseUrl())) {
            baseUrl = getConfiguration().getBaseUrl();
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
        if (eventListener == null) {
            eventListener = new PagesEventListener(this);
            getProxyFactory().registerListener(eventListener);
        }

        if ((getDriver() != null) && !usingProxiedWebDriver()) {
            start();
        }
    }

    private boolean usingProxiedWebDriver() {
        return (getDriver() instanceof WebDriverFacade);
    }

    public Pages onSamePage() {
        usePreviousPage = true;
        return this;
    }
}
