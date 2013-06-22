package net.thucydides.core.webdriver;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.guice.Injectors;
import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.synchronizedList;

/**
 * Provides a proxy for a WebDriver instance.
 * The proxy lets you delay opening the browser until you really know you are going to use it.
 */
public class WebdriverProxyFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<WebdriverProxyFactory> factory = new ThreadLocal<WebdriverProxyFactory>();

    private static List<ThucydidesWebDriverEventListener> eventListeners
                                              = synchronizedList(new ArrayList<ThucydidesWebDriverEventListener>());

    private WebDriverFactory webDriverFactory;
    private WebDriverFacade mockDriver;
    private final Configuration configuration;

    private WebdriverProxyFactory() {
        webDriverFactory = new WebDriverFactory();
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
    }

    public static WebdriverProxyFactory getFactory() {
        if (factory.get() == null) {
            factory.set(new WebdriverProxyFactory());
        }
        return factory.get();
    }

    public static List<ThucydidesWebDriverEventListener> getEventListeners() {
        return ImmutableList.copyOf(eventListeners);
    }
    public WebDriverFacade proxyFor(final Class<? extends WebDriver> driverClass) {
       return proxyFor(driverClass, new WebDriverFactory());
    }

    public WebDriverFacade proxyFor(final Class<? extends WebDriver> driverClass,
                              final WebDriverFactory webDriverFactory) {
        if (mockDriver != null) {
            return mockDriver;
        } else {
            return new WebDriverFacade(driverClass, webDriverFactory);
        }
    }

    public void registerListener(final ThucydidesWebDriverEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void notifyListenersOfWebdriverCreationIn(final WebDriverFacade webDriverFacade) {
        for(ThucydidesWebDriverEventListener listener : getEventListeners()) {
            listener.driverCreatedIn(webDriverFacade);
        }
    }

    public WebDriver proxyDriver() {
        Class<? extends WebDriver> driverClass = webDriverFactory.getClassFor(configuration.getDriverType());
        return proxyFor(driverClass, webDriverFactory);
    }

    public static void resetDriver(final WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            ((WebDriverFacade) driver).reset();
        }
    }

    public void useMockDriver(final WebDriverFacade mockDriver) {
        this.mockDriver = mockDriver;
    }

    public void clearMockDriver() {
        this.mockDriver = null;
    }
}
