package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.awt.*;
import java.io.File;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 *
 * @author johnsmart
 */
public class WebDriverFactory {

    private final WebdriverInstanceFactory webdriverInstanceFactory;

    private ProfilesIni allProfiles;

    public WebDriverFactory() {
        this.webdriverInstanceFactory = new WebdriverInstanceFactory();
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory) {
        this.webdriverInstanceFactory = webdriverInstanceFactory;
    }

    protected ProfilesIni getAllProfiles() {
        if (allProfiles == null) {
            allProfiles = new ProfilesIni();
        }
        return allProfiles;
    }

    /**
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType) {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }

        return newWebdriverInstance(driverType.getWebdriverClass());
    }

    public static Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType) {
        return driverType.getWebdriverClass();
    }

    protected WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        try {
            WebDriver driver;
            if (isAFirefoxDriver(driverClass)) {
                driver = webdriverInstanceFactory.newInstanceOf(driverClass, buildFirefoxProfile());
            } else {
                driver = webdriverInstanceFactory.newInstanceOf(driverClass);
            }
            redimensionBrowser(driver);
            return driver;
        } catch (Exception cause) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }

    private void redimensionBrowser(final WebDriver driver) {
        int height = ThucydidesSystemProperty.getIntegerValue(ThucydidesSystemProperty.SNAPSHOT_HEIGHT, 0);
        int width = ThucydidesSystemProperty.getIntegerValue(ThucydidesSystemProperty.SNAPSHOT_WIDTH, 0);

        if ((height > 0) && (width > 0)) {
            resizeBrowserTo((JavascriptExecutor) driver, height, width);
        } else {
            maximizeBrowserDimensions((JavascriptExecutor) driver);
        }
    }

    private void resizeBrowserTo(JavascriptExecutor driver, int height, int width) {
        String resizeWindow = "window.resizeTo(" + width + "," + height + ")";
        driver.executeScript(resizeWindow);
    }

    private void maximizeBrowserDimensions(JavascriptExecutor driver) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        resizeBrowserTo(driver, screenSize.height, screenSize.width);
    }

    private boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    protected FirefoxProfile createNewFirefoxProfile() {
        return new FirefoxProfile();
    }

    protected FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    private FirefoxProfile buildFirefoxProfile() {

        String profileName = System.getProperty("webdriver.firefox.profile");

        FirefoxProfile profile;
        if (profileName == null) {
            profile = createNewFirefoxProfile();
        } else {
            profile = getProfileFrom(profileName);
        }
        if (dontAssumeUntrustedCertificateIssuer()) {
            profile.setAssumeUntrustedCertificateIssuer(false);
        }
        return profile;
    }

    private FirefoxProfile getProfileFrom(final String profileName) {
        FirefoxProfile profile = getAllProfiles().getProfile(profileName);
        if (profile == null) {
            profile = useExistingFirefoxProfile(new File(profileName));
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
