package net.thucydides.core.pages;

import com.google.common.base.Preconditions;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * The Pages object keeps track of what web pages a test visits, and helps with mapping pages to Page Objects.
 * A Pages object is associated with a WebDriver driver instance, so you need a Pages object for any
 * given WebDriver driver.
 *
 * @author johnsmart
 */
public class Pages {

    private transient WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(Pages.class);

    private String defaultBaseUrl;

    private final transient PageConfiguration pageConfiguration;

    private WebdriverProxyFactory proxyFactory;

    public Pages() {
        this.pageConfiguration = new PageConfiguration();
        proxyFactory = WebdriverProxyFactory.getFactory();
    }

    public Pages(final WebDriver driver) {
        this();
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
        Preconditions.checkNotNull(getDriver());

        getDriver().get(getStartingUrl());
    }

    public PageObject currentPageAt(final Class<? extends PageObject> pageObjectClass) {
        PageObject pageCandidate = getCurrentPageOfType(pageObjectClass);
        String currentUrl = getDriver().getCurrentUrl();
        if (!pageCandidate.compatibleWithUrl(currentUrl)) {
            thisIsNotThePageYourLookingFor(pageObjectClass);
        }
        pageCandidate.setDefaultBaseUrl(getDefaultBaseUrl());

        return pageCandidate;
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
    private PageObject getCurrentPageOfType(final Class<? extends PageObject> pageObjectClass) {
        PageObject currentPage = null;
        try {
            @SuppressWarnings("rawtypes")
            Class[] constructorArgs = new Class[1];
            constructorArgs[0] = WebDriver.class;
            Constructor<? extends PageObject> constructor
                    = (Constructor<? extends PageObject>) pageObjectClass.getConstructor(constructorArgs);
            currentPage = (PageObject) constructor.newInstance(driver);
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
        if (isNotEmpty(PageConfiguration.getCurrentConfiguration().getBaseUrl())) {
            baseUrl = PageConfiguration.getCurrentConfiguration().getBaseUrl();
        }
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

}
