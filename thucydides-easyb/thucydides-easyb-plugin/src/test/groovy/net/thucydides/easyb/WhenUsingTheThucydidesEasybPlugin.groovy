package net.thucydides.easyb

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Binding;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WhenUsingTheThucydidesEasybPlugin {

    @Mock
    WebDriverFactory mockWebDriverFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    class BrowserlessThucydidesPlugin extends ThucydidesPlugin {
        @Override
        protected WebDriverFactory getDefaultWebDriverFactory() {
            return mockWebDriverFactory;
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(mockWebDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX)).thenReturn(firefoxDriver);
    }

    @After
    public void clearSystemProperties() {
        System.setProperty("webdriver.base.url", "");
    }

    @Test
    public void the_plugin_should_answer_to_the_name_of_thucydides() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        assertThat(plugin.getName(), is("thucydides"));
    }

    @Test
    public void the_plugin_should_use_a_normal_WebDriverFactory_by_default() {
        ThucydidesPlugin plugin = new ThucydidesPlugin();
        WebDriverFactory factory = plugin.getDefaultWebDriverFactory();
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void the_plugin_should_inject_a_webdriver_instance_into_the_story_context() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");
        assertThat(driver, is(notNullValue()));
    }

    @Test
    public void the_plugin_should_inject_a_Pages_object_into_the_story_context() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        runStories(plugin, binding);

        Pages pages = (Pages) binding.getVariable("pages");
        assertThat(pages, is(notNullValue()));
    }

    @Test
    public void plugin_configuration_is_available_via_a_property_called_thucydides() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        assertThat(config, is(notNullValue()));
    }

    @Test
    public void the_plugin_should_let_the_user_define_the_default_base_url() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        plugin.getConfiguration().uses_default_base_url("http://www.google.co.nz");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");
        verify(driver).get("http://www.google.co.nz");
    }

    @Test
    public void the_plugin_should_open_the_browser_to_the_system_defined_default_url() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        System.setProperty("webdriver.base.url", "http://www.google.com");
        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");
        verify(driver).get("http://www.google.com");
    }

    @Test
    public void the_plugin_should_close_the_driver_at_the_end_of_the_story() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        runStories(plugin, binding);

        WebDriver driver = (WebDriver) binding.getVariable("driver");
        verify(driver).close();
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

    @Test
    public void the_plugin_should_close_the_webdriver_instance_at_the_end_of_the_story() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();
        Binding binding = new Binding();

        plugin.beforeStory(binding);

         WebDriver driver = (WebDriver) binding.getVariable("driver");
        assertThat(driver, is(notNullValue()));
    }

}
