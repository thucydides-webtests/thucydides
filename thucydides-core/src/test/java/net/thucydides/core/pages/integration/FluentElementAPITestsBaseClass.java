package net.thucydides.core.pages.integration;


import org.openqa.selenium.WebDriver;

public class FluentElementAPITestsBaseClass {

    protected static WebDriver driver;
    protected static WebDriver chromeDriver;
    protected static WebDriver htmlUnitDriver;
    protected static StaticSitePage page;

    protected void refresh(StaticSitePage page) {
        page.getDriver().navigate().refresh();
    }

    protected boolean runningOnLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

}
