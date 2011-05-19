package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a proxy for a WebDriver instance.
 * The proxy lets you delay opening the browser until you really know you are going to use it.
 */
public class WebdriverProxyFactory {

    private static ThreadLocal<WebDriver> mockDriverThreadLocal = new ThreadLocal<WebDriver>();

    private static List<ThucydidesWebDriverEventListener> eventListeners = new ArrayList<ThucydidesWebDriverEventListener>();

    public static WebDriver proxyFor(Class<? extends WebDriver> driverClass) {
        if (usingMockDriver()) {
            return getMockDriver();
        } else {
            return new WebDriverFacade(driverClass);
        }
    }

    private static boolean usingMockDriver() {
        return (getMockDriver() != null);
    }

    public static void registerListener(ThucydidesWebDriverEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public static void notifyListenersOfWebdriverCreationIn(WebDriverFacade webDriverFacade) {
        for(ThucydidesWebDriverEventListener listener : eventListeners) {
            listener.driverCreatedIn(webDriverFacade);
        }
    }

    public static void useMockDriver(WebDriver mockDriver) {
        mockDriverThreadLocal.set(mockDriver);
    }

    public static void clearMockDriver() {
        mockDriverThreadLocal.remove();
    }

    protected static WebDriver getMockDriver() {
        return mockDriverThreadLocal.get();
    }


}
