package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.UnableToCreateProfileException;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.activateJavascriptSupportFor;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 *
 * @author johnsmart
 */
public class WebDriverFactory {
    public static final String DEFAULT_DRIVER = "firefox";

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

    public Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType) {
        if (usesSauceLabs() && (driverType != SupportedWebDriver.HTMLUNIT)) {
            return RemoteWebDriver.class;
        } else {
            return driverType.getWebdriverClass();
        }
    }

    public boolean usesSauceLabs() {
        return StringUtils.isNotEmpty(ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables));
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
            if (isARemoteDriver(driverClass) || shouldUseARemoteDriver()) {
                driver = newRemoteDriver();
            } else if (isAFirefoxDriver(driverClass)) {
                driver = firefoxDriverFrom(driverClass);
            } else if (isAnHtmlUnitDriver(driverClass)) {
                driver = htmlunitDriverFrom(driverClass);
            } else {
                driver = newDriverInstanceFrom(driverClass);
            }
            setImplicitTimeoutsIfSpecified(driver);
            redimensionBrowser(driver);

            activateJavascriptSupportFor(driver);
            return driver;
        } catch (Exception cause) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }

    private void setImplicitTimeoutsIfSpecified(WebDriver driver) {
        if (ThucydidesSystemProperty.TIMEOUTS_IMPLICIT_WAIT.isDefinedIn(environmentVariables)) {
            int timeout = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.TIMEOUTS_IMPLICIT_WAIT
                                                                                            .getPropertyName(),0);

            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        }
    }

    private boolean shouldUseARemoteDriver() {
        return ThucydidesSystemProperty.REMOTE_URL.isDefinedIn(environmentVariables);
    }

    private WebDriver newDriverInstanceFrom(Class<? extends WebDriver> driverClass) throws IllegalAccessException, InstantiationException {
        return webdriverInstanceFactory.newInstanceOf(driverClass);
    }

    private WebDriver newRemoteDriver() throws MalformedURLException {
        WebDriver driver = null;
        if (saucelabsUrlIsDefined()) {
            driver = buildSaucelabsDriver();
        } else {
            driver = buildRemoteDriver();
        }
        Augmenter augmenter = new Augmenter();
        return augmenter.augment(driver);
    }

    private WebDriver buildRemoteDriver() throws MalformedURLException {
        String remoteUrl = ThucydidesSystemProperty.REMOTE_URL.from(environmentVariables);
        DesiredCapabilities capabilities = buildCapabilities();
        return new RemoteWebDriver(new URL(remoteUrl), capabilities);
    }

    private boolean saucelabsUrlIsDefined() {
        return ThucydidesSystemProperty.SAUCELABS_URL.isDefinedIn(environmentVariables);
    }

    private WebDriver buildSaucelabsDriver() throws MalformedURLException {
        String saucelabsUrl = ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables);
        WebDriver driver = new RemoteWebDriver(new URL(saucelabsUrl), findSaucelabsCapabilities());

        if (isNotEmpty(ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.from(environmentVariables))) {
            int implicitWait = environmentVariables.getPropertyAsInteger(
                    ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.getPropertyName(), 30);
            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        }
        return driver;
    }

    private DesiredCapabilities findSaucelabsCapabilities() {

        String driver = ThucydidesSystemProperty.DRIVER.from(environmentVariables);
        DesiredCapabilities capabilities = capabilitiesForDriver(driver);

        configureBrowserVersion(capabilities);

        configureTargetPlatform(capabilities);

        configureTestName(capabilities);

        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    private void configureBrowserVersion(DesiredCapabilities capabilities) {
        String driverVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
        if (isNotEmpty(driverVersion)) {
            capabilities.setCapability("version", driverVersion);
        }
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities) {
        String platformValue = ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables);
        if (isNotEmpty(platformValue)) {
            capabilities.setCapability("platform", platformFrom(platformValue));
        }
    }

    private void configureTestName(DesiredCapabilities capabilities) {
        String testName = ThucydidesSystemProperty.SAUCELABS_TEST_NAME.from(environmentVariables);
        if (isNotEmpty(testName)) {
            capabilities.setCapability("name", testName);
        } else {
            String guessedTestName = bestGuessOfTestName();
            if (guessedTestName != null) {
                capabilities.setCapability("name", bestGuessOfTestName());
            }
        }
    }

    private String bestGuessOfTestName() {
        for (StackTraceElement elt : Thread.currentThread().getStackTrace()) {
            try {
                Class callingClass = Class.forName(elt.getClassName());
                Method callingMethod = callingClass.getMethod(elt.getMethodName());
                if (isATestMethod(callingMethod)) {
                    return NameConverter.humanize(elt.getMethodName());
                } else if (isASetupMethod(callingMethod)) {
                    return NameConverter.humanize(callingClass.getSimpleName());
                }
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    private boolean isATestMethod(Method callingMethod) {
        return callingMethod.getAnnotation(Test.class) != null;
    }

    private boolean isASetupMethod(Method callingMethod) {
        return (callingMethod.getAnnotation(Before.class) != null)
                || (callingMethod.getAnnotation(BeforeClass.class) != null);
    }

    private Platform platformFrom(String platformValue) {
        return Platform.valueOf(platformValue.toUpperCase());
    }

    private DesiredCapabilities buildCapabilities() {
        String driver = ThucydidesSystemProperty.DRIVER.from(environmentVariables);
        return capabilitiesForDriver(driver);
    }


    private DesiredCapabilities capabilitiesForDriver(String driver) {
        if (driver == null) {
            driver = DEFAULT_DRIVER;
        }
        SupportedWebDriver driverType = SupportedWebDriver.valueOf(driver.toUpperCase());
        switch (driverType) {
            case CHROME:
                return DesiredCapabilities.chrome();

            case FIREFOX:
                return DesiredCapabilities.firefox();

            case HTMLUNIT:
                return DesiredCapabilities.htmlUnit();

            case OPERA:
                return DesiredCapabilities.opera();

            case IEXPLORER:
                return DesiredCapabilities.internetExplorer();
        }
        throw new IllegalArgumentException("Unsupported remote driver type: " + driver);
    }

    private WebDriver htmlunitDriverFrom(Class<? extends WebDriver> driverClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        caps.setJavascriptEnabled(true);
        return new HtmlUnitDriver(caps);
    }

    private WebDriver firefoxDriverFrom(Class<? extends WebDriver> driverClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FirefoxProfile profile = buildFirefoxProfile();
        if (aProfileCouldBeCreated(profile)) {
            return webdriverInstanceFactory.newInstanceOf(driverClass, profile);
        } else {
            return newDriverInstanceFrom(driverClass);
        }
    }

    private boolean aProfileCouldBeCreated(FirefoxProfile profile) {
        return (profile != null);
    }

    private Dimension getRequestedBrowserSize() {
        int height = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.SNAPSHOT_HEIGHT, DEFAULT_HEIGHT);
        int width = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.SNAPSHOT_WIDTH, DEFAULT_WIDTH);
        return new Dimension(width, height);
    }

    private void redimensionBrowser(final WebDriver driver) {
        if (supportsScreenResizing(driver) && broswerDimensionsSpecified()) {
            resizeBrowserTo(driver,
                    getRequestedBrowserSize().height,
                    getRequestedBrowserSize().width);
        }
    }

    private boolean broswerDimensionsSpecified() {
        String snapshotWidth = environmentVariables.getProperty(ThucydidesSystemProperty.SNAPSHOT_WIDTH);
        String snapshotHeight = environmentVariables.getProperty(ThucydidesSystemProperty.SNAPSHOT_HEIGHT);
        return (snapshotWidth != null) || (snapshotHeight != null);
    }

    private boolean supportsScreenResizing(final WebDriver driver) {
        return isNotAMocked(driver) && (!isAnHtmlUnitDriver(getDriverClass(driver)));
    }

    private boolean isNotAMocked(WebDriver driver) {
        return (!driver.getClass().getName().contains("Mock"));
    }

    protected void resizeBrowserTo(WebDriver driver, int height, int width) {

        LOGGER.info("Setting browser dimensions to {}/{}", height, width);

        if (usesFirefox(driver) || usesInternetExplorer(driver)) {
            driver.manage().window().setSize(new Dimension(width, height));
        } else if (usesChrome(driver)) {
            ((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank','width=#{width},height=#{height}');");
            Set<String> windowHandles = driver.getWindowHandles();
            windowHandles.remove(driver.getWindowHandle());
            String newWindowHandle = windowHandles.toArray(new String[]{})[0];
            driver.switchTo().window(newWindowHandle);
        }
        String resizeWindow = "window.resizeTo(" + width + "," + height + ")";
        ((JavascriptExecutor) driver).executeScript(resizeWindow);
    }

    private boolean isARemoteDriver(Class<? extends WebDriver> driverClass) {
        return (RemoteWebDriver.class == driverClass);
    }

    private boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    private boolean usesFirefox(WebDriver driver) {
        return (FirefoxDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private boolean usesInternetExplorer(WebDriver driver) {
        return (InternetExplorerDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private boolean usesChrome(WebDriver driver) {
        return (ChromeDriver.class.isAssignableFrom(getDriverClass(driver)));
    }

    private Class getDriverClass(WebDriver driver) {
        Class driverClass = null;
        if (driver instanceof WebDriverFacade) {
            driverClass = ((WebDriverFacade) driver).getDriverClass();
        } else {
            driverClass = driver.getClass();
        }
        return driverClass;
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
        profile.setEnableNativeEvents(true);
        return profile;
    }

    protected FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    private FirefoxProfile buildFirefoxProfile() {
        FirefoxProfile profile = null;
        try {
            String profileName = environmentVariables.getProperty("webdriver.firefox.profile");
            if (profileName == null) {
                profile = createNewFirefoxProfile();
            } else {
                profile = getProfileFrom(profileName);
            }

            firefoxProfileEnhancer.allowWindowResizeFor(profile);
            if (shouldEnableNativeEvents()) {
                firefoxProfileEnhancer.enableNativeEventsFor(profile);
            }
            if (shouldActivateProxy()) {
                activateProxyFor(profile, firefoxProfileEnhancer);
            }
            if (firefoxProfileEnhancer.shouldActivateFirebugs()) {
                LOGGER.info("Adding Firebugs to Firefox profile");
                firefoxProfileEnhancer.addFirebugsTo(profile);
            }
            if (dontAssumeUntrustedCertificateIssuer()) {
                profile.setAssumeUntrustedCertificateIssuer(false);
                profile.setAcceptUntrustedCertificates(true);
            }
        } catch (UnableToCreateProfileException e) {

        }
        return profile;
    }

    private boolean shouldEnableNativeEvents() {
        return Boolean.valueOf(ThucydidesSystemProperty.NATIVE_EVENTS.from(environmentVariables,"true"));
    }

    private void activateProxyFor(FirefoxProfile profile, FirefoxProfileEnhancer firefoxProfileEnhancer) {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        String proxyPort = getProxyPortFromEnvironmentVariables();
        firefoxProfileEnhancer.activateProxy(profile, proxyUrl, proxyPort);
    }

    private String getProxyPortFromEnvironmentVariables() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.PROXY_PORT.getPropertyName());
    }

    private boolean shouldActivateProxy() {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        return StringUtils.isNotEmpty(proxyUrl);
    }

    private String getProxyUrlFromEnvironmentVariables() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.PROXY_URL.getPropertyName());
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
