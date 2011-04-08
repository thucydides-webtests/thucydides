package net.thucydides.easyb;


import net.thucydides.core.pages.Pages
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import net.thucydides.easyb.samples.SampleSteps
import org.junit.Ignore
import net.thucydides.easyb.samples.MoreSampleSteps

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
    public void declared_step_libraries_should_be_recorded() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        assert plugin.configuration.registeredSteps.contains(SampleSteps)

    }

    @Test
    public void declared_step_libraries_should_be_available_in_the_story_context() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")

        assert stepLibrary.class == SampleSteps
    }

    @Test
    public void declared_step_libraries_should_be_available_in_the_story_context_using_a_convention_based_name() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from MoreSampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("more_sample")

        assert stepLibrary.class == MoreSampleSteps
    }

    @Test
    public void a_story_can_have_several_step_libraries() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps
        story.thucydides.uses_steps_from MoreSampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")
        def anotherStepLibrary = binding.getProperty("more_sample")

        assert stepLibrary.class == SampleSteps
        assert anotherStepLibrary.class == MoreSampleSteps
    }

    @Test
    public void step_libraries_should_be_initialized_with_the_pages_object() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_steps_from SampleSteps

        plugin.beforeStory(binding);
        plugin.beforeScenario(binding);

        def stepLibrary = binding.getProperty("sample")

        assert stepLibrary.pages != null
    }

    @Test
    public void the_plugin_configuration_can_be_updated_from_the_story() {

        plugin.beforeStory(binding);

        def story = new Object()
        story.thucydides.uses_default_base_url "http://www.google.com"

        assert plugin.configuration.defaultBaseUrl == "http://www.google.com"
    }


}
