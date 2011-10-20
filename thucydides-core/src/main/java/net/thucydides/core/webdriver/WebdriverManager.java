package net.thucydides.core.webdriver;

import net.thucydides.core.guice.Injectors;
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
     * A WebDriver instance is shared across all the tests executed by the runner in a given test run.
     */
    private final WebDriver webdriver;

    private final WebDriverFactory webDriverFactory;

    private final Configuration configuration;

    public WebdriverManager(final WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
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
        SupportedWebDriver supportedDriverType = configuration.getDriverType();
        Class<? extends WebDriver> webDriverType = WebDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType, webDriverFactory);
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
        return WebDriverFactory.getClassFor(configuration.getDriverType());
     }

}    