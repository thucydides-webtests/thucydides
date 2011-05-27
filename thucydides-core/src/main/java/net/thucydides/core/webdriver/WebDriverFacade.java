package net.thucydides.core.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

/**
 * A proxy class for webdriver instances, designed to prevent the browser being opened unnecessarily.
 */
public class WebDriverFacade implements WebDriver, TakesScreenshot {

    private final Class<? extends WebDriver> driverClass;

    private WebDriver proxiedWebDriver;

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
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return newDriver;
    }

    private boolean usingAMockDriver() {
        return (WebdriverProxyFactory.getFactory().getMockDriver() != null);
    }

    public <X> X getScreenshotAs(final OutputType<X> target) {
        if (proxyInstanciated() && driverCanTakeScreenshots()) {
            return ((TakesScreenshot) getProxiedDriver()).getScreenshotAs(target);
        } else {
            return null;
        }
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
