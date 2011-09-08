package net.thucydides.core.webdriver.integration;

import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewFirefoxDriver {

    WebdriverInstanceFactory webdriverInstanceFactory;

    WebDriver driver;

    private String originalWebdriverProfile;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void createFactory() {
        webdriverInstanceFactory = new WebdriverInstanceFactory();
        originalWebdriverProfile = System.getProperty("webdriver.firefox.profile");
    }

    @After
    public void closeFirefox() {
        driver.quit();
        if (originalWebdriverProfile != null) {
            System.setProperty("webdriver.firefox.profile", originalWebdriverProfile);
        } else {
            System.clearProperty("webdriver.firefox.profile");
        }
    }

    @Test
    public void should_support_creating_a_firefox_driver() throws Exception {
         driver = webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class);
         assertThat(driver, instanceOf(FirefoxDriver.class));
    }

    @Test
    public void should_support_creating_a_firefox_driver_with_a_profile() throws Exception {
        FirefoxProfile profile = new FirefoxProfile();
        driver = webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class, profile);
        assertThat(driver, instanceOf(FirefoxDriver.class));
    }

    String chosenProfile;

    @Test
    public void should_support_creating_a_firefox_driver_with_an_existing_profile() throws Exception {

        WebDriverFactory factory = new WebDriverFactory() {

            @Override
            protected FirefoxProfile useExistingFirefoxProfile(File profileDirectory) {
                chosenProfile = profileDirectory.getAbsolutePath();
                return super.useExistingFirefoxProfile(profileDirectory);
            }
        };

        File customProfileDir = temporaryFolder.newFolder("myprofile");
        System.setProperty("webdriver.firefox.profile", customProfileDir.getAbsolutePath());

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);
        assertThat(chosenProfile, is(customProfileDir.getAbsolutePath()));
    }

}
