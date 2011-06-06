package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 * 
 * @author johnsmart
 *
 */
public class WebdriverManager {

    /**
     * Creates new browser instances. The Browser Factory's job is to provide
     * new web driver instances. It is designed to isolate the test runner from
     * the business of creating and managing WebDriver drivers.
     */
    private final WebDriverFactory webDriverFactory;

    /**
     * A WebDriver instance is shared across all the tests executed by the runner in a given test run.
     */
    private final WebDriver webdriver;
    
    public WebdriverManager(final WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
        webdriver = newDriver();
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     * 
     * @throws UnsupportedDriverException
     *             if the driver type is not supported.
     */
    protected WebDriver newDriver() {
        SupportedWebDriver supportedDriverType = Configuration.getDriverType();
        Class webDriverType = webDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType);
    }
    
    public void closeDriver() {
        if (getWebdriver() != null) {
            getWebdriver().close();
            getWebdriver().quit();
        }
    }

    public WebDriver getWebdriver() {
        return webdriver;
    }

    public Class<? extends WebDriver> getWebDriverClass() {
        return webDriverFactory.getClassFor(Configuration.getDriverType());
     }

}    