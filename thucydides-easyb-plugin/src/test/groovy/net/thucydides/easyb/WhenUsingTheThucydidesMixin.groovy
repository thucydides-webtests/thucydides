package net.thucydides.easyb

import net.thucydides.core.model.TestTag
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.ThucydidesWebdriverManager
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverManager
import net.thucydides.easyb.samples.MoreSampleSteps
import net.thucydides.easyb.samples.SampleSteps
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver

public class WhenUsingTheThucydidesMixin {

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
    public void the_plugin_should_publish_accessors_to_the_thucydides_configuration() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_default_base_url "http://www.google.com"
    }

    @Test
    public void declared_step_libraries_should_be_recorded() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        assert plugin.configuration.registeredSteps.contains(SampleSteps)

    }

    @Test
    public void declared_step_libraries_should_be_available_in_the_story_context() {

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")

        assert SampleSteps.isAssignableFrom(stepLibrary.class)
    }

    @Test
    public void declared_step_libraries_with_custom_names_should_be_available_in_the_story_context() {

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps, "mysteps"

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("mysteps")

        assert SampleSteps.isAssignableFrom(stepLibrary.class)
    }

    @Test
    public void declared_step_libraries_with_custom_names_and_using_the_using_steps_named_notation_should_be_available_in_the_story_context() {

        def story = new Object()
        story.thucydides.uses_steps_named("mysteps").from SampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("mysteps")

        assert SampleSteps.isAssignableFrom(stepLibrary.class)
    }

    @Test
    public void declared_step_libraries_should_be_available_in_the_story_context_using_a_convention_based_name() {

        def story = new Object()
        story.thucydides.uses_steps_from MoreSampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("more_sample")

        assert MoreSampleSteps.isAssignableFrom(stepLibrary.class)
    }

    @Test
    public void a_story_can_have_several_step_libraries() {

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps
        story.thucydides.uses_steps_from MoreSampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")
        def anotherStepLibrary = binding.getProperty("more_sample")

        assert SampleSteps.isAssignableFrom(stepLibrary.class)
        assert MoreSampleSteps.isAssignableFrom(anotherStepLibrary.class)
    }

    @Test
    public void step_libraries_should_be_initialized_with_the_pages_object() {

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")

        assert stepLibrary.pages != null
    }


    @Test
    public void the_plugin_can_be_configured_to_reset_the_webdriver_before_each_scenario() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.use_new_broswer_for_each_scenario()

        assert plugin.configuration.isResetBrowserInEachScenario() == true
    }

    @Test
    public void the_plugin_configuration_can_be_updated_from_the_story() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_default_base_url "http://www.google.com"

        assert plugin.configuration.defaultBaseUrl == "http://www.google.com"
    }

    @Test
    public void "feature tags can be added dynamically"() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.tests.behavior "my behavior"

        assert story.thucydides.tags.contains( TestTag.withName("my behavior").andType("behavior") )
    }

    @Test
    public void "multiple feature tags can be added dynamically"() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.tests.behavior "my behavior", "my other behavior"

        assert story.thucydides.tags.contains( TestTag.withName("my behavior").andType("behavior") )
        assert story.thucydides.tags.contains( TestTag.withName("my other behavior").andType("behavior") )
    }

    @Test
    public void "multiple feature tags can be added dynamically in plural form"() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.tests.behaviors "my behavior", "my other behavior"

        assert story.thucydides.tags.contains( TestTag.withName("my behavior").andType("behavior") )
        assert story.thucydides.tags.contains( TestTag.withName("my other behavior").andType("behavior") )
    }


    @Test
    public void "issues can be added dynamically"() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.tests.issue "123"

        assert story.thucydides.scenarioIssues.contains("123")

    }

    @Test
    public void "multiple issues can be added dynamically"() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.tests.issues "123", "456"

        assert story.thucydides.scenarioIssues.contains("123")
        assert story.thucydides.scenarioIssues.contains("456")

    }

}
