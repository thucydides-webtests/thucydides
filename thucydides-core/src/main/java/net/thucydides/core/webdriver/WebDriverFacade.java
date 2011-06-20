package net.thucydides.core.webdriver;

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

    private WebDriver proxiedWebDriver;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);

    public WebDriverFacade(final Class<? extends WebDriver> driverClass) {
        this.driverClass = driverClass;
    }

    public WebDriver getProxiedDriver() {
        if (proxiedWebDriver == null) {
            proxiedWebDriver = newProxyDriver();
            WebdriverProxyFactory.getFactory().notifyListenersOfWebdriverCreationIn(this);
        }
        return proxiedWebDriver;
    }

    protected WebDriver newProxyDriver() {
        WebDriver newDriver = null;
        if (usingAMockDriver()) {
            newDriver = WebdriverProxyFactory.getFactory().getMockDriver();
        } else {
            newDriver = newDriverInstance();
        }
        return newDriver;
    }

    private WebDriver newDriverInstance() {
        WebDriver newDriver = null;
        try {
            newDriver = driverClass.newInstance();
        } catch (Exception e) {
            throw new UnsupportedDriverException("Could not instantiate " + driverClass);
        }
        return newDriver;
    }

    private boolean usingAMockDriver() {
        return (WebdriverProxyFactory.getFactory().getMockDriver() != null);
    }

    public <X> X getScreenshotAs(final OutputType<X> target) {
        if (proxyInstanciated() && driverCanTakeScreenshots()) {
            try {
                return ((TakesScreenshot) getProxiedDriver()).getScreenshotAs(target);
            } catch (WebDriverException e) {
                LOGGER.error("Failed to take screenshot - driver closed already?", e);
            }
        }
        return null;
    }

    private boolean driverCanTakeScreenshots() {
        return (TakesScreenshot.class.isAssignableFrom(getProxiedDriver().getClass()));
    }

    public void get(final String url) {
        getProxiedDriver().get(url);
    }

    public String getCurrentUrl() {
        return getProxiedDriver().getCurrentUrl();
    }

    public String getTitle() {
        return getProxiedDriver().getTitle();
    }

    public List<WebElement> findElements(final By by) {
        return getProxiedDriver().findElements(by);
    }

    public WebElement findElement(final By by) {
        return getProxiedDriver().findElement(by);
    }

    public String getPageSource() {
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
            getDriverInstance().quit();
        }
    }

    protected boolean proxyInstanciated() {
        return (getDriverInstance() != null);
    }

    public Set<String> getWindowHandles() {
        return getProxiedDriver().getWindowHandles();
    }

    public String getWindowHandle() {
        return getProxiedDriver().getWindowHandle();
    }

    public TargetLocator switchTo() {
        return getProxiedDriver().switchTo();
    }

    public Navigation navigate() {
        return getProxiedDriver().navigate();
    }

    public Options manage() {
        return getProxiedDriver().manage();
    }
}
