package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class UsingWebdriverActionsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    static WebDriver htmlUnitDriver;
    static StaticSitePage page;

    @BeforeClass
    public static void openStaticPage() {
        htmlUnitDriver = new WebDriverFacade(HtmlUnitDriver.class, new WebDriverFactory());
        page = new StaticSitePage(htmlUnitDriver, 1);
        page.setWaitForTimeout(750);
        page.open();
    }

    @Test
    public void should_report_if_element_is_visible() {
        page.withAction().moveToElement(page.firstName).perform();
//        Actions builder = new Actions(page.getDriver());
//        builder.moveToElement(page.firstName).perform();
    }
}
