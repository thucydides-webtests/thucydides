package net.thucydides.core.webdriver.javascript;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class JavascriptSupport {
    public static boolean javascriptIsSupportedIn(Class<? extends WebDriver> driverClass) {
        return (isNotAMockedDriver(driverClass) && supportsJavascript(driverClass));
    }

    public static boolean javascriptIsSupportedIn(WebDriver driver) {
        Class<? extends WebDriver> driverClass = getRealDriverClass(driver);
        return javascriptIsSupportedIn(driverClass);
    }

    private static Class<? extends WebDriver> getRealDriverClass(WebDriver driver) {
        if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
            WebDriverFacade driverFacade = (WebDriverFacade) driver;
            return driverFacade.getDriverClass();
        } else {
            return driver.getClass();
        }
    }

    private static boolean supportsJavascript(Class<? extends WebDriver> driverClass) {
        return JavascriptExecutor.class.isAssignableFrom(driverClass);
    }


    private static boolean isNotAMockedDriver(Class<? extends WebDriver> driverClass) {
        return !driverClass.getName().contains("Mock");
    }

    public static void activateJavascriptSupportFor(WebDriver driver) {
        if (HtmlUnitDriver.class.isAssignableFrom(driver.getClass())) {
            ((HtmlUnitDriver)driver).setJavascriptEnabled(true);
        }
    }

    public static boolean isHeadlessDriver(WebDriver driver) {
        return (HtmlUnitDriver.class.isAssignableFrom(getRealDriverClass(driver)));
    }
}
