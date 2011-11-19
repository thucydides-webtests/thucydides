package net.thucydides.core.webdriver;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 *
 * @author johnsmart
 */
public class WebDriverFactory {


    private final WebdriverInstanceFactory webdriverInstanceFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);

    private ProfilesIni allProfiles;
    private static final int DEFAULT_HEIGHT = ThucydidesSystemProperty.DEFAULT_HEIGHT;
    private static final int DEFAULT_WIDTH = ThucydidesSystemProperty.DEFAULT_WIDTH;

    private final EnvironmentVariables environmentVariables;
    private final FirefoxProfileEnhancer firefoxProfileEnhancer;

    public WebDriverFactory() {
        this(new WebdriverInstanceFactory(), Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables) {
        this(new WebdriverInstanceFactory(), environmentVariables);
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables) {
        this(webdriverInstanceFactory,
             environmentVariables,
             new FirefoxProfileEnhancer(environmentVariables));
    }

    public WebDriverFactory(WebdriverInstanceFactory webdriverInstanceFactory,
                            EnvironmentVariables environmentVariables,
                            FirefoxProfileEnhancer firefoxProfileEnhancer) {
        this.webdriverInstanceFactory = webdriverInstanceFactory;
        this.environmentVariables = environmentVariables;
        this.firefoxProfileEnhancer = firefoxProfileEnhancer;
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

    /**
     * This method is synchronized because multiple webdriver instances can be created in parallel.
     * However, they may use common system resources such as ports, so may potentially interfere
     * with each other.
     *
     * @param driverClass
     * @return
     */
    protected synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        try {
            WebDriver driver;
            LOGGER.info("Instanciating new browser");
            if (isAFirefoxDriver(driverClass)) {
                driver = firefoxDriverFrom(driverClass);
            } else if (isAnHtmlUnitDriver(driverClass)) {
                driver = webdriverInstanceFactory.newInstanceOf(driverClass);
                activateJavascriptSupportFor((HtmlUnitDriver) driver);
            } else {
                driver = webdriverInstanceFactory.newInstanceOf(driverClass);
            }
            if (supportsScreenResizing(driver)) {
                LOGGER.info("Redimentioning browser");
                redimensionBrowser(driver);
            }
            return driver;
        } catch (Exception cause) {
            LOGGER.error("Could not create new Webdriver instance", cause);
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }

    private WebDriver firefoxDriverFrom(Class<? extends WebDriver> driverClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return webdriverInstanceFactory.newInstanceOf(driverClass, buildFirefoxProfile());
    }

    private void activateJavascriptSupportFor(HtmlUnitDriver driver) {
        driver.setJavascriptEnabled(true);
    }

    private boolean supportsScreenResizing(final WebDriver driver) {
        return (isAFirefoxDriver(driver.getClass()) || isAnInternetExplorerDriver(driver.getClass()));
    }

    private void redimensionBrowser(final WebDriver driver) {
        int height = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.SNAPSHOT_HEIGHT.getPropertyName(),
                DEFAULT_HEIGHT);
        int width = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.SNAPSHOT_WIDTH.getPropertyName(),
                DEFAULT_WIDTH);
        resizeBrowserTo((JavascriptExecutor) driver, height, width);
    }

    private void resizeBrowserTo(JavascriptExecutor driver, int height, int width) {
        String resizeWindow = "window.resizeTo(" + width + "," + height + ")";
        driver.executeScript(resizeWindow);
    }

    private boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isAnHtmlUnitDriver(Class<? extends WebDriver> driverClass) {
        return (HtmlUnitDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isAnInternetExplorerDriver(Class<? extends WebDriver> driverClass) {
        return (InternetExplorerDriver.class.isAssignableFrom(driverClass));
    }

    protected FirefoxProfile createNewFirefoxProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setAlwaysLoadNoFocusLib(true);
        return profile;
    }

    protected FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    private FirefoxProfile buildFirefoxProfile() {
        String profileName = environmentVariables.getProperty("webdriver.firefox.profile");
        FirefoxProfile profile;
        if (profileName == null) {
            profile = createNewFirefoxProfile();
        } else {
            profile = getProfileFrom(profileName);
        }

        firefoxProfileEnhancer.enableNativeEventsFor(profile);

        if (firefoxProfileEnhancer.shouldActivateFirebugs()) {
            LOGGER.info("Adding Firebugs to Firefox profile");
            firefoxProfileEnhancer.addFirebugsTo(profile);
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
        return !(environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.ASSUME_UNTRUSTED_CERTIFICATE_ISSUER.getPropertyName(), true));
    }

    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver) {
        Configuration configuration = Injectors.getInjector().getInstance(Configuration.class);
        ElementLocatorFactory finder = new DisplayedElementLocatorFactory(driver, configuration.getElementTimeout());
        PageFactory.initElements(finder, pageObject);
    }

    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver, int timeout) {
        ElementLocatorFactory finder = new DisplayedElementLocatorFactory(driver, timeout);
        PageFactory.initElements(finder, pageObject);
    }

}
