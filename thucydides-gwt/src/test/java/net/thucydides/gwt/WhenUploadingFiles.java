package net.thucydides.gwt;

import net.thucydides.gwt.pages.GwtShowcaseUploadPage;
import net.thucydides.gwt.widgets.GwtButton;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.ElementNotDisplayedException;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUploadingFiles {

    public static WebDriver driver;

    private GwtShowcaseUploadPage uploadPage;

    @BeforeClass
    public static void openBrowser() {
        driver = new FirefoxDriver();
        driver.get("http://gwt.google.com/samples/Showcase/Showcase.html#!CwFileUpload");
    }
    
    @Before
    public void setupPageObject() {
        uploadPage = new GwtShowcaseUploadPage(driver);
        uploadPage.waitForRenderedElements(By.xpath("//button"));
    }
    
    @Test
    public void should_upload_a_file_from_the_resources_directory() {

        uploadPage.uploadFile("uploads/readme.txt");
        uploadPage.clickOkInAlertWindow();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }
}
