package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 * 
 * @author johnsmart
 *
 */
public class WebDriverFactory {

    private final WebdriverInstanceFactory webdriverInstanceFactory;


    public WebDriverFactory() {
        this.webdriverInstanceFactory = new WebdriverInstanceFactory();
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory) {
        this.webdriverInstanceFactory = webdriverInstanceFactory;
    }

    /***
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType)  {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }

        return newWebdriverInstance(driverType.getWebdriverClass());
    }

    public static Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType)  {
        return driverType.getWebdriverClass();
    }

    protected WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        try {
            if (isAFirefoxDriver(driverClass)) {
               return webdriverInstanceFactory.newInstanceOf(driverClass, buildFirefoxProfile());
            } else {
                return webdriverInstanceFactory.newInstanceOf(driverClass);
            }
        } catch (Exception cause) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }

    private boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    protected FirefoxProfile createNewFirefoxProfile() {
        return new FirefoxProfile();
    }

    private FirefoxProfile buildFirefoxProfile() {
        FirefoxProfile profile = createNewFirefoxProfile();
        if (dontAssumeUntrustedCertificateIssuer()) {
            profile.setAssumeUntrustedCertificateIssuer(false);
        }
        return profile;
    }

    private boolean dontAssumeUntrustedCertificateIssuer() {
        return !(ThucydidesSystemProperty.getBooleanValue(ThucydidesSystemProperty.ASSUME_UNTRUSTED_CERTIFICATE_ISSUER,
                                                          true));
    }

    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver) {
        ElementLocatorFactory finder = new DisplayedElementLocatorFactory(driver, Configuration.getElementTimeout());
        PageFactory.initElements(finder, pageObject);
    }

    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver, int timeout) {
        ElementLocatorFactory finder = new DisplayedElementLocatorFactory(driver, timeout);
        PageFactory.initElements(finder, pageObject);
    }

}
