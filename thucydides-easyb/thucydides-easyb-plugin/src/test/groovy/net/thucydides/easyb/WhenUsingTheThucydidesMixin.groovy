package net.thucydides.easyb;


import net.thucydides.core.pages.Pages
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver

public class WhenUsingTheThucydidesMixin {

    ThucydidesPlugin plugin
    def binding;

    class BrowserlessThucydidesPlugin extends ThucydidesPlugin {
        @Override
        protected WebDriverFactory getDefaultWebDriverFactory() {
            return new WebDriverFactory() {
                protected WebDriver newFirefoxDriver() {
                    return new MockWebDriver();
                }

            }
        }
    }

    @Before
    public void initMocks() {
        plugin = new BrowserlessThucydidesPlugin();
        binding = new Binding();
    }

    @Test
    public void the_plugin_should_publish_accessors_to_the_thucydides_configuration() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_default_base_url "http://www.google.com"
    }

    @Test
    public void the_plugin_configuration_can_be_updated_from_the_story() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_default_base_url "http://www.google.com"

        assert plugin.configuration.defaultBaseUrl == "http://www.google.com"
    }


}
