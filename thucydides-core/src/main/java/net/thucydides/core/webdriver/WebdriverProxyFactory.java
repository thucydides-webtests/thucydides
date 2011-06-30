package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a proxy for a WebDriver instance.
 * The proxy lets you delay opening the browser until you really know you are going to use it.
 */
public class WebdriverProxyFactory implements Serializable {


    private static ThreadLocal<WebdriverProxyFactory> factory = new ThreadLocal<WebdriverProxyFactory>();

    private static List<ThucydidesWebDriverEventListener> eventListeners
            = new ArrayList<ThucydidesWebDriverEventListener>();

    private WebDriver mockDriver;

    private WebdriverProxyFactory() {}

    public static WebdriverProxyFactory getFactory() {
        if (factory.get() == null) {
            factory.set(new WebdriverProxyFactory());
        }
        return factory.get();
    }

    public WebDriver proxyFor(final Class<? extends WebDriver> driverClass) {
        if (usingMockDriver()) {
            return getMockDriver();
        } else {
            return new WebDriverFacade(driverClass);
        }
    }

    private boolean usingMockDriver() {
        return (getMockDriver() != null);
    }

    public void registerListener(final ThucydidesWebDriverEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void notifyListenersOfWebdriverCreationIn(final WebDriverFacade webDriverFacade) {
        for(ThucydidesWebDriverEventListener listener : eventListeners) {
            listener.driverCreatedIn(webDriverFacade);
        }
    }

    public void useMockDriver(final WebDriver driver) {
        this.mockDriver = driver;
    }

    public void clearMockDriver() {
        mockDriver = null;
    }

    protected WebDriver getMockDriver() {
        return mockDriver;
    }


    public WebDriver proxyDriver() {
        Class<? extends WebDriver> driverClass = WebDriverFactory.getClassFor(Configuration.getDriverType());
        return proxyFor(driverClass);
    }

    public static void resetDriver(WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            ((WebDriverFacade) driver).reset();
        }
    }
}
