package net.thucydides.easyb

import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepListener
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.*
import org.easyb.BehaviorStep
import org.easyb.domain.Behavior
import org.easyb.listener.ExecutionListener
import org.easyb.util.BehaviorStepType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runners.model.InitializationError
import org.mockito.Mockito
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static org.hamcrest.Matchers.containsString
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.*

public class WhenUsingTheThucydidesEasybPlugin {

    ThucydidesPlugin plugin
    Binding binding
    WebDriverFacade mockWebDriver;
    File reportDirectory

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

    ExecutionListener listener;

    Behavior behavior;

    BehaviorStep scenarioStep;

    BehaviorStep givenStep;

    BehaviorStep whenStep;

    BehaviorStep thenStep;

    @Before
    public void initMocks() {
        reportDirectory = temporaryDir()
        System.setProperty("thucydides.outputDirectory", reportDirectory.getAbsolutePath())

        mockWebDriver =  new WebDriverFacade(MockWebDriver.class, new WebDriverFactory());
        plugin = new BrowserlessThucydidesPlugin();
        plugin.resetConfiguration();

        listener = new ThucydidesListenerBuilder().get();
        binding = new Binding();
        binding.setVariable("sourceFile", "TestStory.story")

        plugin.getConfiguration().use_mock_driver mockWebDriver

        behavior = [] as Behavior;

        scenarioStep = [] as BehaviorStep
        scenarioStep.id = 1
        scenarioStep.stepType = BehaviorStepType.SCENARIO
        scenarioStep.name = 'Some scenario'

        givenStep = [] as BehaviorStep
        givenStep.id = 2
        givenStep.stepType = BehaviorStepType.GIVEN
        givenStep.name = 'Some given'

        whenStep = [] as BehaviorStep
        whenStep.id = 3
        whenStep.stepType = BehaviorStepType.WHEN
        whenStep.name = 'Some when'

        thenStep = [] as BehaviorStep
        thenStep.id = 4
        thenStep.stepType = BehaviorStepType.THEN
        thenStep.name = 'Some name'
    }

    def temporaryDir() {
        def tempDir = new File(System.getProperty("java.io.tmpdir"))
        def reportDirName = "reports-${System.currentTimeMillis()}"
        new File(tempDir, reportDirName)

    }

    @After
    public void clearSystemProperties() {
        System.clearProperty("webdriver.base.url")
        System.clearProperty("thucydides.outputDirectory")
        plugin.getConfiguration().stop_using_mock_driver()
        reportDirectory.deleteOnExit()
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
    public void tagger_is_available_via_a_property_called_tests() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        Tagger tagger = config.tests
        assert tagger != null
    }


    @Test
    public void tagger_is_available_via_a_property_called_testing() {
        ThucydidesPlugin plugin = new BrowserlessThucydidesPlugin();

        runStories(plugin, binding);

        PluginConfiguration config = (PluginConfiguration) binding.getVariable("thucydides");
        Tagger tagger = config.testing

        assert tagger != null
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none()
        
    @Test
    public void the_plugin_should_fail_to_initialize_if_no_source_file_is_defined() {
        expectedException.expect IllegalArgumentException
        expectedException.expectMessage containsString("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")

        ThucydidesPlugin plugin = new MockedThucydidesPlugin();
        Binding binding = new Binding();
        plugin.stepListeners = [mock(StepListener)]
        
        runStories(plugin, binding);
    }
    
    def runTestsUsing(ThucydidesPlugin thucydidesPlugin, Binding binding) {
        thucydidesPlugin.baseStepListener.testStarted("A simple story")
        thucydidesPlugin.baseStepListener.stepStarted(ExecutedStepDescription.withTitle("A simple scenario"))
        thucydidesPlugin.baseStepListener.stepFinished();
    }


    public class ScenarioThread implements Callable<ThucydidesPlugin> {

        ThucydidesPlugin plugin;
        Binding binding;

        public ScenarioThread()  throws InitializationError {
            plugin = new MockedThucydidesPlugin();
            binding = new Binding();

            plugin.resetConfiguration();
            plugin.stepListeners = [mock(StepListener)]

            listener = new ThucydidesListenerBuilder().get();
            binding = new Binding();
            binding.setVariable("sourceFile", "TestStory.story")



        }

        @Override
        ThucydidesPlugin call() {
            new StoryRunner(plugin, binding).runStories();
            return plugin;
        }
    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError, InterruptedException {

        List<ScenarioThread> threads = new ArrayList<ScenarioThread>();
        List<Future<ThucydidesPlugin>> futures = new ArrayList<Future<ThucydidesPlugin>>();
        
        for(int i = 0; i < 10; i++) {
            threads.add(new ScenarioThread());
        }

        for(ScenarioThread thread : threads) {
            ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
            futures.add(threadExecutor.submit(thread));
        }

        for(Future<ThucydidesPlugin> future : futures) {
            future.get();
        }
    }

    private runStories(ThucydidesPlugin plugin, Binding binding)  {
        new StoryRunner(plugin, binding).runStories();
    }

    public class StoryRunner {

        private final ThucydidesPlugin plugin;
        private final Binding binding;

        StoryRunner(ThucydidesPlugin plugin, Binding binding) {
            this.plugin = plugin
            this.binding = binding
        }

        public void runStories() {
            plugin.beforeStory(binding);
            runScenarios();
            plugin.afterStory(binding);
        }

        public void runScenarios() {

            plugin.beforeScenario(binding);
            listener.startStep(scenarioStep);

            plugin.beforeGiven(binding);
            listener.startStep(givenStep);

            plugin.afterGiven(binding);
            listener.stopStep();

            plugin.beforeWhen(binding);
            listener.startStep(whenStep);

            plugin.afterWhen(binding);
            listener.stopStep();

            plugin.beforeThen(binding);
            listener.startStep(thenStep);

            runTestsUsing(plugin, binding);

            plugin.afterThen(binding);
            listener.stopStep();

            plugin.afterScenario(binding);
            listener.stopStep();
        }
    }

}
