package net.thucydides.easyb

import net.thucydides.core.model.TestTag
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.ThucydidesWebdriverManager
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverManager
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver

public class WhenAddingTagsDynamically {

    ThucydidesPlugin plugin
    def binding;

    class MockWebDriverFactory extends WebDriverFactory {
        protected WebDriver newFirefoxDriver() {
            return new MockWebDriver();
        }
    }


    class BrowserlessThucydidesPlugin extends ThucydidesPlugin {
        @Override
        protected WebdriverManager getWebdriverManager() {
            return new ThucydidesWebdriverManager(new MockWebDriverFactory(),
                                                  new SystemPropertiesConfiguration(new SystemEnvironmentVariables()));
        }
    }

    @Before
    public void initMocks() {
        plugin = new BrowserlessThucydidesPlugin();
        binding = new Binding();
        binding.setVariable("sourceFile", "TestStory.story")
    }

    @Test
    public void the_plugin_configuration_can_be_updated_from_the_story() {

        Tagger tagger = new Tagger(plugin.configuration)
        tagger.feature("some feature")
        tagger.epic("some epic")

        assert plugin.configuration.getTags().contains(TestTag.withName("some epic").andType("epic"))
        assert plugin.configuration.getTags().contains(TestTag.withName("some feature").andType("feature"))
    }


}
