package net.thucydides.easyb;


import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.StepListener
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.ThucydidesWebdriverManager
import net.thucydides.core.webdriver.UnsupportedDriverException
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import static org.hamcrest.Matchers.containsString
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.atLeast
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class WhenUsingTheThucydidesEasybPlugin {

    ThucydidesPlugin plugin
    Binding binding
    WebDriverFacade mockWebDriver;

    class MockWebDriverFactory extends WebDriverFactory {
         protected WebDriver newFirefoxDriver() {
             return mockWebDriver;
         }
     }

    class MockFirefoxWebDriverFactory extends WebDriverFactory {
        protected FirefoxDriver newFirefoxDriver() {
            return Mockito.mock(FirefoxDriver.class);
        }
     }

     class BrowserlessThucydidesPlugin extends ThucydidesPlugin {

        @Override
        protected WebdriverManager getWebdriverManager() {
            return new ThucydidesWebdriverManager(new MockWebDriverFactory(),
                                                  new SystemPropertiesConfiguration(new SystemEnvironmentVariables()));
        }

        public getCloseCount() {
            return closeCount;
        }
    }

    class MockedThucydidesPlugin extends ThucydidesPlugin {

        @Override
        protected WebdriverManager getWebdriverManager() {
            return new ThucydidesWebdriverManager(new MockFirefoxWebDriverFactory(),
                                                  new SystemPropertiesConfiguration(new SystemEnvironmentVariables()));
        }

        public getCloseCount() {
            return closeCount;
        }
    }

    @Before
    public void initMocks() {
        mockWebDriver =  new WebDriverFacade(MockWebDriver.class, new WebDriverFactory());
        plugin = new BrowserlessThucydidesPlugin();
        plugin.resetConfiguration();
        binding = new Binding();
        binding.setVariable("sourceFile", "TestStory.story")

        plugin.getConfiguration().use_mock_driver mockWebDriver
    }

    @After
    public void clearSystemProperties() {
        System.setProperty("webdriver.base.url", "");
        plugin.getConfiguration().stop_using_mock_driver();
    }

    @Test
    public void the_plugin_should_answer_to_the_name_of_thucydides() {
        assert plugin.name == "thucydides"
    }

    @Test
    public void the_plugin_should_inject_a_webdriver_instance_into_the_story_context() {

        plugin.configuration.uses_default_base_url "http://www.google.com"

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver != null
    }

    @Test
    public void should_be_able_to_force_the_driver_for_a_story() {

        WebdriverManager webdriverManager = mock(WebdriverManager)
        WebDriver webDriver = mock(WebDriver)
        when(webdriverManager.getWebdriver()).thenReturn(webDriver);
        when(webdriverManager.getWebdriver(anyString())).thenReturn(webDriver);

        ThucydidesPlugin plugin = new ThucydidesPlugin(webdriverManager)

        plugin.configuration.uses_default_base_url "http://www.google.com"
        plugin.configuration.uses_driver "chrome"

        runStories(plugin, binding);

        verify(webdriverManager, atLeast(1)).getWebdriver("chrome")
    }

    @Test
    public void should_reject_an_illegal_driver_for_a_story() {

        def exceptionThrown = false;
        try {
            WebdriverManager webdriverManager = mock(WebdriverManager)
            WebDriver webDriver = mock(WebDriver)
            when(webdriverManager.getWebdriver()).thenReturn(webDriver);
            when(webdriverManager.getWebdriver(anyString())).thenReturn(webDriver);

            ThucydidesPlugin plugin = new ThucydidesPlugin(webdriverManager)

            plugin.configuration.uses_default_base_url "http://www.google.com"
            plugin.configuration.uses_driver "does-not-exist"

            runStories(plugin, binding);
        } catch (UnsupportedDriverException e) {
            exceptionThrown = true;
        }

        assert exceptionThrown == true
    }

    @Test
    public void the_plugin_should_inject_a_Pages_object_into_the_story_context() {

        plugin.configuration.uses_default_base_url "http://www.google.com"

        runStories(plugin, binding);

        Pages pages = (Pages) binding.getVariable("pages");
        assert pages != null
    }


    @Test
    public void plugin_configuration_is_available_via_a_property_called_thucydides() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        assert config != null
    }

    @Test
    public void the_plugin_should_let_the_user_define_the_default_base_url() {

        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");

        runStories(plugin, binding);

        mockWebDriver.proxiedDriver.shouldHaveOpenedAt("http://www.google.co.nz")

    }

    @Test
    public void the_user_should_be_able_to_override_the_default_base_url_using_a_system_property() {

        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");

        System.setProperty("webdriver.base.url","http://www.wikipedia.org")
        runStories(plugin, binding);

        mockWebDriver.proxiedDriver.shouldHaveOpenedAt("http://www.wikipedia.org")

    }


    @Test
    public void the_plugin_should_open_the_browser_to_the_system_defined_default_url() {

        System.setProperty("webdriver.base.url", "http://www.google.com");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        driver.proxiedDriver.shouldHaveOpenedAt("http://www.google.com")
    }
    /*

    @Test
    public void the_plugin_should_use_a_new_driver_for_each_story() {

        plugin.getConfiguration().stop_using_mock_driver()
        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");

        runStories(plugin, binding);
        WebDriver driver1 = (WebDriver) binding.getVariable("driver").proxiedWebDriver;

        runStories(plugin, binding);
        WebDriver driver2 = (WebDriver) binding.getVariable("driver").proxiedWebDriver;

        assert (driver1 != driver2)
    }


    @Ignore("Not really sure how to get this to work properly yet.")
    @Test
    public void the_plugin_should_be_configurable_to_use_the_same_driver_for_all_stories() {
        plugin.getConfiguration().stop_using_mock_driver()
        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz")
        System.setProperty("thucydides.use.unique.browser","true")

        runStories(plugin, binding)
        WebDriver driver1 = (WebDriver) binding.getVariable("driver")

        runStories(plugin, binding)
        WebDriver driver2 = (WebDriver) binding.getVariable("driver")

        System.setProperty("thucydides.use.unique.browser","false")

        assert (driver1 == driver2)
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_story() {

        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver.proxiedDriver.wasClosed();
        assert driver.proxiedDriver.closedCount == 1;
    }

    @Test
    public void the_plugin_should_not_close_the_driver_at_the_end_of_the_story_if_using_a_single_browser() {

        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver.proxiedDriver.wasClosed();
        assert driver.proxiedDriver.closedCount == 1;
    }
    */

    @Rule
    public ExpectedException expectedException = ExpectedException.none()
        
    @Test
    public void the_plugin_should_fail_to_initialize_if_no_source_file_is_defined() {
        expectedException.expect IllegalArgumentException
        expectedException.expectMessage containsString("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")

        ThucydidesPlugin plugin = new MockedThucydidesPlugin();
        Binding binding = new Binding();
        plugin.stepListener = mock(StepListener)
        
        runStories(plugin, binding);
    }
    
    private void runStories(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeStory(binding);
        runScenarios(plugin, binding);
        plugin.afterStory(binding);
    }

    private void runScenarios(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeScenario(binding);
        plugin.beforeGiven(binding);
        plugin.afterGiven(binding);

        plugin.beforeWhen(binding);
        plugin.afterWhen(binding);

        plugin.afterThen(binding);
        plugin.beforeThen(binding);

        plugin.afterScenario(binding);

        plugin.beforeScenario(binding);

        plugin.beforeGiven(binding);
        runWebTestsUsing(plugin);
        plugin.afterGiven(binding);

        plugin.beforeWhen(binding);
        plugin.afterWhen(binding);

        plugin.afterThen(binding);
        plugin.beforeThen(binding);

        plugin.afterScenario(binding);
    }

    def runWebTestsUsing(ThucydidesPlugin thucydidesPlugin) {
        WebDriver driver = binding.getVariable("driver");
        driver.get "http://www.google.com"
    }


}
