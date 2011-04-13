package net.thucydides.easyb;


import net.thucydides.core.pages.Pages
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.mockito.Mock

public class WhenUsingTheThucydidesEasybPlugin {

    ThucydidesPlugin plugin
    Binding binding


    class BrowserlessThucydidesPlugin extends ThucydidesPlugin {

        @Override
        protected WebDriverFactory getDefaultWebDriverFactory() {
            return new WebDriverFactory() {
                protected WebDriver newFirefoxDriver() {
                    return new MockWebDriver();
                }

            }
        }

        public getCloseCount() {
            return closeCount;
        }
    }

    @Before
    public void initMocks() {
        plugin = new BrowserlessThucydidesPlugin();
        plugin.resetConfiguration();
        binding = new Binding();
    }

    @After
    public void clearSystemProperties() {
        System.setProperty("webdriver.base.url", "");
    }

    @Test
    public void the_plugin_should_answer_to_the_name_of_thucydides() {
        println plugin.getName()
        assert plugin.name == "thucydides"
    }

    @Test
    public void the_plugin_should_use_a_normal_WebDriverFactory_by_default() {
        WebDriverFactory factory = plugin.getDefaultWebDriverFactory();
        assert factory != null
    }

    @Test
    public void the_plugin_should_inject_a_webdriver_instance_into_the_story_context() {

        plugin.configuration.uses_default_base_url "http://www.google.com"

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver != null
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
        Binding binding = new Binding();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        assert config != null
    }

    @Test
    public void the_plugin_should_let_the_user_define_the_default_base_url() {

        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        driver.shouldHaveOpenedAt("http://www.google.co.nz")
    }

    @Test
    public void the_plugin_should_open_the_browser_to_the_system_defined_default_url() {

        System.setProperty("webdriver.base.url", "http://www.google.com");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        driver.shouldHaveOpenedAt("http://www.google.com")
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_story() {

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");

        assert driver.wasClosed();
        assert driver.closedCount == 1;
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_scenario_if_requested() {

        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();
        plugin.getConfiguration().use_new_broswer_for_each_scenario()

        plugin.beforeStory(binding);
        WebDriver driver = (WebDriver) binding.getVariable("driver");

        runScenarios(plugin, binding);

        plugin.afterStory(binding);

        WebDriver finalDriver = (WebDriver) binding.getVariable("driver");
        assert finalDriver != driver;
    }


    private void runStories(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeStory(binding);
        runScenarios(plugin, binding);
        plugin.afterStory(binding);
    }

    private void runScenarios(ThucydidesPlugin plugin, Binding binding) {
        plugin.beforeScenario(binding);
        plugin.beforeGiven(binding);
        plugin.beforeWhen(binding);
        plugin.beforeThen(binding);
        plugin.afterScenario(binding);
        plugin.beforeScenario(binding);
        plugin.beforeGiven(binding);
        plugin.beforeWhen(binding);
        plugin.beforeThen(binding);
        plugin.afterScenario(binding);
    }


}
