package net.thucydides.core.webdriver.integration;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewFirefoxDriver {

    WebdriverInstanceFactory webdriverInstanceFactory;

    WebDriver driver;

    private MockEnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    FirefoxProfileEnhancer firefoxProfileEnhancer;

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

        TestableWebdriverFactory(WebdriverInstanceFactory mockWebdriverInstanceFactory,
                                 EnvironmentVariables environmentVariables) {
            super(mockWebdriverInstanceFactory, environmentVariables, firefoxProfileEnhancer);
        }

        TestableWebdriverFactory(EnvironmentVariables environmentVariables) {
            super(webdriverInstanceFactory, environmentVariables, firefoxProfileEnhancer);
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

    @Test
    public void should_allow_the_thucydides_activate_firebugs_to_deactivate_firebugs() {
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);

        environmentVariables.setProperty("thucydides.activate.firebugs", "false");

        assertThat(firefoxProfileEnhancer.shouldActivateFirebugs(), is(false));
    }

    @Test
    public void should_activate_firebugs_by_default() {
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);

        assertThat(firefoxProfileEnhancer.shouldActivateFirebugs(), is(true));
    }

    @Test
    public void should_include_the_firebugs_extension_by_default() throws Exception {

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        when(firefoxProfileEnhancer.shouldActivateFirebugs()).thenReturn(true);

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer).addFirebugsTo(any(FirefoxProfile.class));
    }

    @Test
    public void should_exclude_the_firebugs_extension_if_the_thucydides_activate_firebugs_property_is_set_to_false() throws Exception {

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        when(firefoxProfileEnhancer.shouldActivateFirebugs()).thenReturn(false);

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);
    }

    @Test
    public void should_enable_native_events() {
        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer).enableNativeEventsFor(any(FirefoxProfile.class));
    }

}
