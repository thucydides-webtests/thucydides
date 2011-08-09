package net.thucydides.core.webdriver;

import net.thucydides.core.steps.StepEventBus;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * A proxy class for webdriver instances, designed to prevent the browser being opened unnecessarily.
 */
public class WebDriverFacade implements WebDriver, TakesScreenshot {

    private final Class<? extends WebDriver> driverClass;

    private final WebDriverFactory webDriverFactory;

    protected WebDriver proxiedWebDriver;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);

    public WebDriverFacade(final Class<? extends WebDriver> driverClass,
                           final WebDriverFactory webDriverFactory) {
        this.driverClass = driverClass;
        this.webDriverFactory = webDriverFactory;
    }

    public WebDriver getProxiedDriver() {
        if (proxiedWebDriver == null) {
            proxiedWebDriver = newProxyDriver();
            WebdriverProxyFactory.getFactory().notifyListenersOfWebdriverCreationIn(this);
        }
        ensureValidDriver();
        return proxiedWebDriver;
    }

    private boolean isEnabled() {
        return !StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }
    /**
     * Workaround for Webdriver issue 1438 (http://code.google.com/p/selenium/issues/detail?id=1438)
     */
    private void ensureValidDriver() {
        try {
            proxiedWebDriver.getCurrentUrl();
        } catch (WebDriverException e) {
            proxiedWebDriver.switchTo().defaultContent();
        }
    }

    public void reset() {
        if (proxiedWebDriver != null) {
            forcedQuit();
        }
        proxiedWebDriver = null;

    }

    private void forcedQuit() {
        try {
            getDriverInstance().quit();
            proxiedWebDriver = null;
        } catch (WebDriverException e) {
            LOGGER.warn("Closing a driver that was already closed",e);
        }
    }

    protected WebDriver newProxyDriver() {
        return newDriverInstance();
    }

    private WebDriver newDriverInstance() {
        try {
            return webDriverFactory.newWebdriverInstance(driverClass);
        } catch (Exception e) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass, e);
        }
    }

    public <X> X getScreenshotAs(final OutputType<X> target) {
        if (proxyInstanciated() && driverCanTakeScreenshots()) {
            try {
                return ((TakesScreenshot) getProxiedDriver()).getScreenshotAs(target);
            } catch (WebDriverException e) {
                LOGGER.warn("Failed to take screenshot - driver closed already?", e);
            }
        }
        return null;
    }

    private boolean driverCanTakeScreenshots() {
        return (TakesScreenshot.class.isAssignableFrom(getProxiedDriver().getClass()));
    }

    public void get(final String url) {
        if (!isEnabled()) return;

        getProxiedDriver().get(url);
    }

    public String getCurrentUrl() {
        if (!isEnabled()) return null;

        return getProxiedDriver().getCurrentUrl();
    }

    public String getTitle() {
        if (!isEnabled()) return null;

        return getProxiedDriver().getTitle();
    }

    public List<WebElement> findElements(final By by) {
        if (!isEnabled()) return null;

        return getProxiedDriver().findElements(by);
    }

    public WebElement findElement(final By by) {
        if (!isEnabled()) return null;

        return getProxiedDriver().findElement(by);
    }

    public String getPageSource() {
        if (!isEnabled()) return null;

        return getProxiedDriver().getPageSource();
    }

    protected WebDriver getDriverInstance() {
        return proxiedWebDriver;
    }

    public void close() {
        if (proxyInstanciated()) {
            getDriverInstance().close();
        }
    }

    public void quit() {
        if (proxyInstanciated()) {
            try {
                getDriverInstance().quit();
            } catch (WebDriverException e) {
                LOGGER.warn("Error while quitting the driver - is this IE?");
            }
            proxiedWebDriver = null;
        }
    }

    protected boolean proxyInstanciated() {
        return (getDriverInstance() != null);
    }

    public Set<String> getWindowHandles() {
        if (!isEnabled()) return null;

        return getProxiedDriver().getWindowHandles();
    }

    public String getWindowHandle() {
        if (!isEnabled()) return null;

        return getProxiedDriver().getWindowHandle();
    }

    public TargetLocator switchTo() {
        if (!isEnabled()) return null;

        return getProxiedDriver().switchTo();
    }

    public Navigation navigate() {
        if (!isEnabled()) return null;

        return getProxiedDriver().navigate();
    }

    public Options manage() {
        if (!isEnabled()) return null;

        return getProxiedDriver().manage();
    }
}
