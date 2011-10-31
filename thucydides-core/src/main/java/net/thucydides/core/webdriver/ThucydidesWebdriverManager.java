package net.thucydides.core.webdriver;

import com.google.inject.Inject;
import net.thucydides.core.guice.Injectors;
import org.openqa.selenium.WebDriver;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 *                                                  ˜
 * @author johnsmart
 *
 */
public class ThucydidesWebdriverManager implements WebdriverManager {

    private static final ThreadLocal<WebDriver> webdriverThreadLocal = new ThreadLocal<WebDriver>();

    private final WebDriverFactory webDriverFactory;

    private final Configuration configuration;

    @Inject
    public ThucydidesWebdriverManager(final WebDriverFactory webDriverFactory, Configuration configuration) {
        this.webDriverFactory = webDriverFactory;
        this.configuration = configuration;
    }

    public ThucydidesWebdriverManager(final WebDriverFactory webDriverFactory) {
        this(webDriverFactory, Injectors.getInjector().getInstance(Configuration.class));
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     *
     * @throws net.thucydides.core.webdriver.UnsupportedDriverException
     *             if the driver type is not supported.
     */
    private static WebDriver newDriver(Configuration configuration, WebDriverFactory webDriverFactory) {
        SupportedWebDriver supportedDriverType = configuration.getDriverType();
        Class<? extends WebDriver> webDriverType = WebDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType, webDriverFactory);
    }
    
    public void closeDriver() {
        if (getWebdriver() != null) {
            getWebdriver().close();
            getWebdriver().quit();
            webdriverThreadLocal.remove();
        }
    }

    public WebDriver getWebdriver() {
        return getThreadLocalWebDriver(configuration, webDriverFactory);
    }

    private static WebDriver getThreadLocalWebDriver(Configuration configuration, WebDriverFactory webDriverFactory) {
        if (webdriverThreadLocal.get() == null) {
            webdriverThreadLocal.set(newDriver(configuration, webDriverFactory));
        }
        return webdriverThreadLocal.get();
    }

}