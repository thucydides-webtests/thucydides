package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.activateJavascriptSupportFor;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

    private final Integer EXTRA_TIME_TO_TAKE_SCREENSHOTS = 180;

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
            if (isARemoteDriver(driverClass) || shouldUseARemoteDriver() || saucelabsUrlIsDefined()) {
                driver = newRemoteDriver();
            } else if (isAFirefoxDriver(driverClass)) {
                driver = firefoxDriver();
            } else if (isAnHtmlUnitDriver(driverClass)) {
                driver = htmlunitDriver();
            } else if (isAChromeDriver(driverClass)) {
                driver = chromeDriver();
            } else if (isASafariDriver(driverClass)) {
                driver = safariDriver();
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

    private WebDriver  newRemoteDriver() throws MalformedURLException {
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
        return webdriverInstanceFactory.newRemoteDriver(new URL(remoteUrl), buildRemoteCapabilities());
    }

    private boolean saucelabsUrlIsDefined() {
        return ThucydidesSystemProperty.SAUCELABS_URL.isDefinedIn(environmentVariables);
    }

    private WebDriver buildSaucelabsDriver() throws MalformedURLException {
        String saucelabsUrl = ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables);
        WebDriver driver = webdriverInstanceFactory.newRemoteDriver(new URL(saucelabsUrl), findSaucelabsCapabilities());

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

    private DesiredCapabilities buildRemoteCapabilities() {
        String driver = ThucydidesSystemProperty.REMOTE_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = ThucydidesSystemProperty.DRIVER.from(environmentVariables);
        }
        return capabilitiesForDriver(driver);
    }


    private DesiredCapabilities capabilitiesForDriver(String driver) {
        if (driver == null) {
            driver = DEFAULT_DRIVER;
        }
        SupportedWebDriver driverType = driverTypeFor(driver);
        if (driverType == null) {
            throw new IllegalArgumentException("Unsupported remote driver type: " + driver);
        }

        if (driverType == SupportedWebDriver.REMOTE) {
            return remoteCapabilities();
        } else {
            return realBrowserCapabilities(driverType);
        }
    }

    private SupportedWebDriver driverTypeFor(String driver) {
        String normalizedDriverName = driver.toUpperCase();
        if (!SupportedWebDriver.listOfSupportedDrivers().contains(normalizedDriverName)) {
            SupportedWebDriver closestDriver = SupportedWebDriver.getClosestDriverValueTo(normalizedDriverName);
            throw new AssertionError("Unsupported driver for webdriver.driver or webdriver.remote.driver: " + driver
                                     + ". Did you mean " + closestDriver.toString().toLowerCase() + "?");
        }
        return SupportedWebDriver.valueOf(normalizedDriverName);
    }

    private DesiredCapabilities realBrowserCapabilities(SupportedWebDriver driverType) {

        DesiredCapabilities capabilities = null;

        switch (driverType) {
            case CHROME:
                capabilities = DesiredCapabilities.chrome();
                break;

            case FIREFOX:
                capabilities = DesiredCapabilities.firefox();
                break;

            case HTMLUNIT:
                capabilities = DesiredCapabilities.htmlUnit();
                break;

            case OPERA:
                capabilities = DesiredCapabilities.opera();
                break;

            case IEXPLORER:
                capabilities = DesiredCapabilities.internetExplorer();
                break;

            default:
                capabilities = new DesiredCapabilities();
                capabilities.setJavascriptEnabled(true);
        }
        addExtraCatabilitiesTo(capabilities);
        return capabilities;
    }

    private DesiredCapabilities remoteCapabilities() {
        String remoteBrowser = ThucydidesSystemProperty.REMOTE_DRIVER.from(environmentVariables, "firefox");
        DesiredCapabilities capabilities = realBrowserCapabilities(driverTypeFor(remoteBrowser));
        capabilities.setCapability("idle-timeout",EXTRA_TIME_TO_TAKE_SCREENSHOTS);

        Boolean recordScreenshotsInSaucelabs
              = environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.SAUCELABS_RECORD_SCREENSHOTS, false);
        capabilities.setCapability("record-screenshots",recordScreenshotsInSaucelabs);

        addExtraCatabilitiesTo(capabilities);
        return capabilities;
    }

    private void addExtraCatabilitiesTo(DesiredCapabilities capabilities) {
        CapabilitySet capabilitySet = new CapabilitySet(environmentVariables);
        Map<String, Object> extraCapabilities = capabilitySet.getCapabilities();
        for(String capabilityName : extraCapabilities.keySet()) {
            capabilities.setCapability(capabilityName, extraCapabilities.get(capabilityName));
        }
    }

    private WebDriver htmlunitDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        caps.setJavascriptEnabled(true);
        return webdriverInstanceFactory.newHtmlUnitDriver(caps);
    }

    private WebDriver firefoxDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FirefoxProfile profile = buildFirefoxProfile();
        return webdriverInstanceFactory.newFirefoxDriver(profile);
    }

    private WebDriver chromeDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ChromeOptions options = new ChromeOptions();
        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);
        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments =  Lists.newArrayList(Splitter.on(",").trimResults().split(chromeSwitches));
            options.addArguments(arguments);
        }
        return webdriverInstanceFactory.newChromeDriver(options);

    }

    private WebDriver safariDriver() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return webdriverInstanceFactory.newSafariDriver();

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

    private boolean isAChromeDriver(Class<? extends WebDriver> driverClass) {
        return (ChromeDriver.class.isAssignableFrom(driverClass));
    }

    private boolean isASafariDriver(Class<? extends WebDriver> driverClass) {
        return (SafariDriver.class.isAssignableFrom(driverClass));
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

    protected FirefoxProfile buildFirefoxProfile() {
        FirefoxProfile profile;
        String profileName = environmentVariables.getProperty("webdriver.firefox.profile");
        if (profileName == null) {
            profile = createNewFirefoxProfile();
        } else {
            profile = getProfileFrom(profileName);
        }

        firefoxProfileEnhancer.allowWindowResizeFor(profile);
        firefoxProfileEnhancer.activateNativeEventsFor(profile, shouldEnableNativeEvents());
        if (shouldActivateProxy()) {
            activateProxyFor(profile, firefoxProfileEnhancer);
        }
        if (firefoxProfileEnhancer.shouldActivateFirebugs()) {
            firefoxProfileEnhancer.addFirebugsTo(profile);
        }
        if (refuseUntrustedCertificates()) {
            profile.setAssumeUntrustedCertificateIssuer(false);
            profile.setAcceptUntrustedCertificates(false);
        } else {
            profile.setAssumeUntrustedCertificateIssuer(true);
            profile.setAcceptUntrustedCertificates(true);
        }
        firefoxProfileEnhancer.configureJavaSupport(profile);
        firefoxProfileEnhancer.addPreferences(profile);
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

    private boolean refuseUntrustedCertificates() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.getPropertyName(), false);
    }

    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public void initElementsWithAjaxSupport(final PageObject pageObject, final WebDriver driver) {
        ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(driver);
        PageFactory.initElements(finder, pageObject);
        initWebElementFacades(new WebElementFacadeFieldDecorator(finder), pageObject, driver);
    }

    private ElementLocatorFactorySelector getElementLocatorFactorySelector() {
        Configuration configuration = Injectors.getInjector().getInstance(Configuration.class);
        return new ElementLocatorFactorySelector(configuration);
    }

    public void initElementsWithAjaxSupport(final PageObject pageObject, final WebDriver driver, int timeoutInSeconds) {
        ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(driver);
        PageFactory.initElements(finder, pageObject);
        initWebElementFacades(new WebElementFacadeFieldDecorator(finder), pageObject, driver);

    }

    private void initWebElementFacades(WebElementFacadeFieldDecorator decorator, PageObject page, final WebDriver driver) {
        Class<?> proxyIn = page.getClass();
        while (proxyIn != Object.class) {
            proxyFields(decorator, page, proxyIn, driver);
            proxyIn = proxyIn.getSuperclass();
        }
    }

    private void proxyFields(WebElementFacadeFieldDecorator decorator, PageObject page, Class<?> proxyIn, final WebDriver driver) {
        Field[] fields = proxyIn.getDeclaredFields();
        for (Field field : fields) {
            WebElement webElementValue = decorator.decorate(page.getClass().getClassLoader(), field);
            if (webElementValue != null) {
                try {
                    WebElementFacade facadeValue = new WebElementFacade(driver, webElementValue,page.waitForTimeoutInMilliseconds());
                    field.setAccessible(true);
                    field.set(page, facadeValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
