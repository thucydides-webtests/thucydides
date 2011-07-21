package net.thucydides.core.pages.integration;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class WhenUsingLotsOfWebElements {

    static WebDriver driver;

    static StaticSitePage page;

    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class);
        page = new StaticSitePage(driver, 1);
        page.setWaitForTimeout(100);
        page.open();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    @DefaultUrl("classpath:static-site/index.html")
    public static final class StaticSitePage extends PageObject {

        protected WebElement data;

        public StaticSitePage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }

        public List<String> getData() {
            List<String> results = new ArrayList<String>();
            List<WebElement> rows = data.findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                for(WebElement cell : cells) {
                    results.add(cell.getText());
                }
            }
            return results;
        }

    }

    @Ignore("This test reproduces a Selenium error - it is not for general use.")
    @Test
    public void should_return_all_of_the_table_cells() {
        page.getData();
    }

}
