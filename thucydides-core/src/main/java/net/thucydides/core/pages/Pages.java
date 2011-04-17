package net.thucydides.core.pages;

import java.lang.reflect.Constructor;

import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * The Pages object keeps track of what web pages a test visits, and helps with mapping pages to Page Objects.
 * A Pages object is associated with a WebDriver driver instance, so you need a Pages object for any
 * given WebDriver driver.
 *
 * @author johnsmart
 */
public class Pages {

    private final WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(Pages.class);

    private String defaultBaseUrl;

    private PageConfiguration pageConfiguration;


    public Pages(final WebDriver driver) {
        this.driver = driver;
        this.pageConfiguration = new PageConfiguration();
    }

    public WebDriver getDriver() {
        return driver;
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
            return (pageCandidate.compatibleWithUrl(currentUrl));
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
        if (defaultBaseUrl != null) {
            return defaultBaseUrl;
        } else {
            return pageConfiguration.getBaseUrl();
        }
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

}
