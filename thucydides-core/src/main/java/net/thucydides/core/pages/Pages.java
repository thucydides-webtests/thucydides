package net.thucydides.core.pages;

import static net.thucydides.core.WebdriverSystemProperty.BASE_URL;

import java.lang.reflect.Constructor;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Preconditions;

/**
 * The Pages object keeps track of what web pages a test visits, and helps with mapping pages to Page Objects.
 * A Pages object is associated with a WebDriver driver instance, so you need a Pages object for any
 * given WebDriver driver.
 * 
 * @author johnsmart
 *
 */
public class Pages {

    private final WebDriver driver;

    private String defaultBaseUrl;
    
    public Pages(final WebDriver driver) {
        this.driver = driver;
    }

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    private String getBaseUrl() {
        String systemDefinedBaseUrl = System.getProperty(BASE_URL.getPropertyName());
        if (systemDefinedBaseUrl != null) {
            return systemDefinedBaseUrl;
        } else {
            return defaultBaseUrl;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
    
    /**
     * Opens a browser on the application home page, as defined by the base URL.
     */
    public void start() {        
        Preconditions.checkNotNull(driver);
        
        final String homeUrl = getBaseUrl();
        driver.get(homeUrl);
    }

    public PageObject currentPageAt(final Class<? extends PageObject> pageObjectClass) {
        PageObject pageCandidate = getCurrentPageOfType(pageObjectClass);
        String currentUrl = driver.getCurrentUrl();
        if (!pageCandidate.compatibleWithUrl(currentUrl)) {
            thisIsNotThePageYourLookingFor(pageObjectClass);
        }
        
        return pageCandidate;
    }
    
    public boolean isCurrentPageAt(final Class<? extends PageObject> pageObjectClass) {
        try {
            PageObject pageCandidate = getCurrentPageOfType(pageObjectClass);
            String currentUrl = driver.getCurrentUrl();
            return (pageCandidate.compatibleWithUrl(currentUrl));
        } catch (WrongPageException e) {
            return false;
        }
    }

    /**
     * Create a new Page Object of the given type.
     * The Page Object must have a constructor 
     * @param pageObjectClass
     * @return
     * @throws IllegalArgumentException
     */
    private PageObject getCurrentPageOfType(final Class<? extends PageObject> pageObjectClass) 
      throws WrongPageException {
        PageObject currentPage = null;
        try {
            @SuppressWarnings("rawtypes")
            Class[] constructorArgs = new Class[1];
            constructorArgs[0] = WebDriver.class;
            Constructor<? extends PageObject> constructor 
                = (Constructor<? extends PageObject>) pageObjectClass.getConstructor(constructorArgs);
            currentPage = (PageObject) constructor.newInstance(driver);
            currentPage.setDriver(driver);
        } catch (Exception e) {
            thisIsNotThePageYourLookingFor(pageObjectClass, e);
        }        
        return currentPage;
    }

    private void thisIsNotThePageYourLookingFor(final Class<? extends PageObject> pageObjectClass) 
        throws WrongPageException {
        thisIsNotThePageYourLookingFor(pageObjectClass, null);
    }
            
    private void thisIsNotThePageYourLookingFor(
            final Class<? extends PageObject> pageObjectClass, final Exception e)
            throws WrongPageException {
        
        String errorDetails = "This is not the page you're looking for:\n"
            + "I was looking for a page compatible with " + pageObjectClass + "\n"
            + "I was at the URL " + driver.getCurrentUrl();
        
        if (e != null) {
            e.printStackTrace();
            throw new WrongPageException(errorDetails, e);
        } else {
            throw new WrongPageException(errorDetails);
        }
    }
}
