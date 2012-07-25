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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.ExtensionConnection;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.internal.Lock;
import org.openqa.selenium.remote.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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

    @Mock
    FirefoxProfile profile;

    @Before
    public void createFactory() {
        MockitoAnnotations.initMocks(this);
        webdriverInstanceFactory = new WebdriverInstanceFactory() {
            @Override
            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
                return super.newInstanceOf(MockedFirefoxDriver.class);
            }

            @Override
            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass, FirefoxProfile profile) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
                return super.newInstanceOf(MockedFirefoxDriver.class, profile);
            }
        };
        environmentVariables = new MockEnvironmentVariables();
    }

    @After
    public void closeFirefox() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static class MockedFirefoxDriver extends FirefoxDriver {
        public MockedFirefoxDriver() { }
        public MockedFirefoxDriver(FirefoxProfile profile) {}
        public MockedFirefoxDriver(Capabilities capabilities) {}

        @Override
        protected Response execute(String driverCommand, Map<String, ?> parameters) {return new Response();}


        @Override
        protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {}

        @Override
        protected void startClient() {}

        @Override
        protected void startSession(Capabilities desiredCapabilities) {}

        @Override
        public Object executeScript(String script, Object... args) {
            return null;
        }
    }


    @Test
    public void should_support_creating_a_firefox_driver() throws Exception {
         driver = webdriverInstanceFactory.newInstanceOf(MockedFirefoxDriver.class);
         assertThat(driver, instanceOf(MockedFirefoxDriver.class));
    }


    @Test
    public void should_support_creating_a_firefox_driver_with_a_profile() throws Exception {
        FirefoxProfile profile = new FirefoxProfile();
        driver = webdriverInstanceFactory.newInstanceOf(MockedFirefoxDriver.class, profile);
        assertThat(driver, instanceOf(MockedFirefoxDriver.class));
    }

    String chosenProfile;

    class TestableWebdriverFactory extends WebDriverFactory {

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
    public void should_enable_native_events_by_default() {
        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer).enableNativeEventsFor(any(FirefoxProfile.class));
    }

    @Test
    public void should_be_able_to_deactivate_native_events_if_required() {
        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);

        environmentVariables.setProperty("thucydides.native.events", "false");
        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer, never()).enableNativeEventsFor(any(FirefoxProfile.class));
    }

    @Test
    public void should_allow_a_proxy_to_be_configured_using_system_properites() {
        environmentVariables.setProperty("thucydides.proxy.http", "my.proxy");
        environmentVariables.setProperty("thucydides.proxy.http_port", "8080");

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);
        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer).activateProxy(any(FirefoxProfile.class), eq("my.proxy"), eq("8080"));
    }

    @Test
    public void should_not_activate_the_proxy_if_proxy_system_property_not_set() {
        environmentVariables.setProperty("thucydides.proxy.http", "");
        environmentVariables.setProperty("thucydides.proxy.http_port", "");

        WebDriverFactory factory = new TestableWebdriverFactory(environmentVariables);
        driver = factory.newInstanceOf(SupportedWebDriver.FIREFOX);

        verify(firefoxProfileEnhancer, never()).activateProxy(any(FirefoxProfile.class), anyString(), anyString());
    }

    @Test
    public void should_set_firefox_preferences_to_activate_proxy() {
        FirefoxProfileEnhancer enhancer = new FirefoxProfileEnhancer(environmentVariables);

        enhancer.activateProxy(profile, "my.proxy","8080");

        verify(profile).setPreference("network.proxy.http","my.proxy");
        verify(profile).setPreference("network.proxy.http_port","8080");
        verify(profile).setPreference("network.proxy.type","1");
    }

}
