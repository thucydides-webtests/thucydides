package net.thucydides.core.webdriver.integration;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        assertThat(driver, is(not(nullValue())));
    }

    @Test
    public void should_not_activate_firebugs_by_default() {
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);

        assertThat(firefoxProfileEnhancer.shouldActivateFirebugs(), is(false));
    }

    @Test
    public void should_activate_firebugs_if_requested() {
        environmentVariables.setProperty("thucydides.activate.firebugs", "true");
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);

        assertThat(firefoxProfileEnhancer.shouldActivateFirebugs(), is(true));
    }

    class MockFirefoxProfile extends FirefoxProfile {

        public List<String> extensions = new ArrayList<String>();

        @Override
        public void addExtension(Class<?> loadResourcesUsing, String loadFrom) throws IOException {
            extensions.add(loadFrom);
        }
    }

    @Test
    public void should_inject_firebugs_into_browser_if_requested () {
        environmentVariables.setProperty("thucydides.activate.firebugs", "true");
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
        MockFirefoxProfile profile = new MockFirefoxProfile();

        firefoxProfileEnhancer.addFirebugsTo(profile);

        List<String> registeredExtensions = profile.extensions;

        assertThat(registeredExtensions, containsExtention("firebug"));
    }

    private TypeSafeMatcher<List<String>> containsExtention(final String extensionName) {
        return new TypeSafeMatcher<List<String>>() {

            @Override
            public boolean matchesSafely(List<String> extensions) {
                for(String extension : extensions) {
                    if (extension.contains(extensionName) && (extension.endsWith(".xpi"))) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" a firefox plugin called " + extensionName);
            }
        };
    }

    @Test
    public void should_inject_firefinder_into_browser_if_firebugs_is_added () {
        environmentVariables.setProperty("thucydides.activate.firebugs", "true");
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
        MockFirefoxProfile profile = new MockFirefoxProfile();

        firefoxProfileEnhancer.addFirebugsTo(profile);

        List<String> registeredExtensions = profile.extensions;
        assertThat(registeredExtensions, containsExtention("firefinder"));
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

        environmentVariables.setProperty("thucydides.activate.firebugs", "false");
        FirefoxProfileEnhancer firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
        assertThat(firefoxProfileEnhancer.shouldActivateFirebugs(), is(false));
    }

    @Test
    public void should_enable_native_events() {
        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer).enableNativeEventsFor(any(FirefoxProfile.class));
    }

}
