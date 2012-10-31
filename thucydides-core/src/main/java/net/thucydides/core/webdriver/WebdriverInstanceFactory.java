package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Centralize instantiation of WebDriver drivers.
 */
public class WebdriverInstanceFactory {

    public WebdriverInstanceFactory() {
    }

    public WebDriver newInstanceOf(final Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
        return webdriverClass.newInstance();
    }

    public WebDriver newRemoteDriver(URL remoteUrl, DesiredCapabilities capabilities) {
        return new RemoteWebDriver(remoteUrl, capabilities);
    }

    public WebDriver newFirefoxDriver(FirefoxProfile profile) {
        return new FirefoxDriver(profile);
    }

    public WebDriver newChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

    public WebDriver newHtmlUnitDriver(DesiredCapabilities caps) {
        return new HtmlUnitDriver(caps);
    }
}