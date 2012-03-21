package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.StaticTestSite;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FluentElementAPITestsBaseClass {

    private static ThreadLocal<StaticTestSite> testSiteThreadLocal = new ThreadLocal<StaticTestSite>();
    private ThreadLocal<StaticSitePage> firefoxPage = new ThreadLocal<StaticSitePage>();
    private ThreadLocal<StaticSitePage> chromePage = new ThreadLocal<StaticSitePage>();

    protected static StaticTestSite getStaticTestSite() {
        if (testSiteThreadLocal.get() == null) {
            testSiteThreadLocal.set(new StaticTestSite());
        }
        return testSiteThreadLocal.get();
    }

    protected StaticSitePage getFirefoxPage() {
        if (firefoxPage.get() == null) {
            WebDriver driver = getStaticTestSite().open("firefox");
            firefoxPage.set(new StaticSitePage(driver, 5000));
            firefoxPage.get().addJQuerySupport();
        }
        return firefoxPage.get();
    }

    protected StaticSitePage getChromePage() {
        if (chromePage.get() == null) {
            WebDriver driver = getStaticTestSite().open("chrome");
            chromePage.set(new StaticSitePage(driver, 5000));
            chromePage.get().addJQuerySupport();
        }
        return chromePage.get();
    }

    @AfterClass
    public static void closeBrowsers() {
        getStaticTestSite().close();
    }

    protected void refresh(StaticSitePage page) {
        page.getDriver().navigate().refresh();
    }

    protected boolean runningOnLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

}
