package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.StaticTestSite;
import org.junit.AfterClass;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FluentElementAPITestsBaseClass {

    private ThreadLocal<StaticTestSite> testSiteThreadLocal = new ThreadLocal<StaticTestSite>();
    private ThreadLocal<StaticSitePage> firefoxPage = new ThreadLocal<StaticSitePage>();
    private ThreadLocal<StaticSitePage> chromePage = new ThreadLocal<StaticSitePage>();

    private static Set<WebDriver> openDrivers = Collections.synchronizedSet(new HashSet<WebDriver>());

    protected StaticTestSite getStaticTestSite() {
        if (testSiteThreadLocal.get() == null) {
            testSiteThreadLocal.set(new StaticTestSite());
        }
        return testSiteThreadLocal.get();
    }

    protected StaticSitePage getFirefoxPage() {
        StaticSitePage page;
        if (firefoxPage.get() == null) {
            WebDriver driver = getStaticTestSite().open();
            openDrivers.add(driver);
            page = new StaticSitePage(driver, 1000);
            page.open();
            page.addJQuerySupport();
        } else {
            page = firefoxPage.get();
            refresh(page);
        }
        return page;
    }

    protected StaticSitePage getChromePage() {
        StaticSitePage page;
        if (chromePage.get() == null) {
            WebDriver driver = getStaticTestSite().open("chrome");
            openDrivers.add(driver);
            page = new StaticSitePage(driver, 1000);
            page.open();
            page.addJQuerySupport();
        } else {
            page = chromePage.get();
            refresh(page);
        }
        return page;
    }

    @AfterClass
    public static void closeBrowsers() {
        for(WebDriver driver : openDrivers) {
            driver.quit();
        }
    }

    protected void refresh(StaticSitePage page) {
        page.getDriver().navigate().refresh();
    }

    protected boolean runningOnLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

}
