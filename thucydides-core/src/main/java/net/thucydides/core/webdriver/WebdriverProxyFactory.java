package net.thucydides.core.webdriver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

/**
 * Provides a proxy for a WebDriver instance.
 * The proxy lets you delay opening the browser until you really know you are going to use it.
 */
public class WebdriverProxyFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<WebdriverProxyFactory> factory = new ThreadLocal<WebdriverProxyFactory>();

    private static List<ThucydidesWebDriverEventListener> eventListeners
            = new ArrayList<ThucydidesWebDriverEventListener>();

    private WebDriverFactory webDriverFactory;

    private WebdriverProxyFactory() {
        webDriverFactory = new WebDriverFactory();
    }


    protected void setWebDriverFactory(final WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public static WebdriverProxyFactory getFactory() {
        if (factory.get() == null) {
            factory.set(new WebdriverProxyFactory());
        }
        return factory.get();
    }

    public WebDriver proxyFor(final Class<? extends WebDriver> driverClass) {
       return proxyFor(driverClass, new WebDriverFactory());
    }

    public WebDriver proxyFor(final Class<? extends WebDriver> driverClass,
                              final WebDriverFactory webDriverFactory) {
        return new WebDriverFacade(driverClass, webDriverFactory);
    }

    public void registerListener(final ThucydidesWebDriverEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void notifyListenersOfWebdriverCreationIn(final WebDriverFacade webDriverFacade) {
        for(ThucydidesWebDriverEventListener listener : eventListeners) {
            listener.driverCreatedIn(webDriverFacade);
        }
    }

    public WebDriver proxyDriver() {
        Class<? extends WebDriver> driverClass = WebDriverFactory.getClassFor(Configuration.getDriverType());
        return proxyFor(driverClass, webDriverFactory);
    }

    public static void resetDriver(WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            ((WebDriverFacade) driver).reset();
        }
    }
}
