package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class FluentElementAPITestsBaseClass {

    static protected WebDriver driver;
    static protected WebDriver chromeDriver;
    static protected StaticSitePage page;


    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class, new WebDriverFactory());
        chromeDriver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
        chromeDriver.quit();
    }

    @Before
    public void openStaticPage() {
        page.setWaitForTimeout(5000);
        page.open();
    }

    protected boolean runningOnLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

}
