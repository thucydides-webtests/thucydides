package net.thucydides.core.webdriver.integration;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class WhenInstanciatingANewFirefoxDriver {

    WebdriverInstanceFactory webdriverInstanceFactory;

    WebDriver driver;

    private String originalWebdriverProfile;

    private MockEnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void createFactory() {
        MockitoAnnotations.initMocks(this);
        webdriverInstanceFactory = new WebdriverInstanceFactory();
        environmentVariables = new MockEnvironmentVariables();
    }

    @After
    public void closeFirefox() {
        if (driver != null) {
            driver.quit();
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

    class TestableWebdriverFactory extends WebDriverFactory {

        TestableWebdriverFactory(EnvironmentVariables environmentVariables) {
            super(environmentVariables);
        }

        @Override
        protected FirefoxProfile useExistingFirefoxProfile(File profileDirectory) {
            chosenProfile = profileDirectory.getAbsolutePath();
            return super.useExistingFirefoxProfile(profileDirectory);
        }

    }

    @Test
    public void should_support_creating_a_firefox_driver_with_an_existing_profile() throws Exception {

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        File customProfileDir = temporaryFolder.newFolder("myprofile");
        environmentVariables.setProperty("webdriver.firefox.profile", customProfileDir.getAbsolutePath());

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);
        assertThat(chosenProfile, is(customProfileDir.getAbsolutePath()));
    }
    @Test
    public void should_support_creating_a_firefox_driver_with_a_named_profile() throws Exception {

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        environmentVariables.setProperty("webdriver.firefox.profile", "default");

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);
        assertThat(chosenProfile, is(nullValue()));
    }

}
