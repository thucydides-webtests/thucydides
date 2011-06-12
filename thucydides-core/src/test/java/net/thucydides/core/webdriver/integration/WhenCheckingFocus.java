package net.thucydides.core.webdriver.integration;

import net.thucydides.core.pages.PageObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenCheckingFocus {

    public class IndexPage extends PageObject {

        public WebElement firstname;
        public WebElement lastname;

        public IndexPage(WebDriver driver) {
            super(driver);
        }

    }
    
    private static WebDriver driver;
    
    @BeforeClass
    public static void open_local_static_site() {
        driver = new FirefoxDriver();
        openStaticTestSite(driver);
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    private static void openStaticTestSite(WebDriver driver) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }


    @Test
    public void should_detect_if_a_web_element_has_focus() {
        IndexPage indexPage = new IndexPage(driver);

        assertThat(indexPage.hasFocus(indexPage.lastname), is(true));
    }

    @Test
    public void should_not_detect_focus_if_a_web_element_does_not_have_focus() {
        IndexPage indexPage = new IndexPage(driver);

        assertThat(indexPage.hasFocus(indexPage.firstname), is(false));
    }

}
