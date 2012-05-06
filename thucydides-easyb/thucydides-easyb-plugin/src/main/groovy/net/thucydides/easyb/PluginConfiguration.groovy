package net.thucydides.easyb

import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.webdriver.WebdriverProxyFactory
import org.openqa.selenium.WebDriver
import net.thucydides.core.model.TestTag

public class PluginConfiguration {

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    private static final String OUTPUT_DIRECTORY_PROPERTY = "thucydides.outputDirectory";

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    private static ThreadLocal<PluginConfiguration> configuration = new ThreadLocal<PluginConfiguration>();

    public static synchronized reset() {
        configuration.remove();
    }

    public static synchronized PluginConfiguration getInstance() {
        if (configuration.get() == null) {
            configuration.set(new PluginConfiguration());
        }
        return configuration.get();
    }

    def defaultBaseUrl

    def storyClass

    def registeredSteps = []

    def scenarioIssues = []

    def tags = [] as Set

    def resetBrowserInEachScenario = true
    
    def requestedDriver

    /**
     * Define the base URL to be used for this story.
     */
    public void uses_default_base_url(String defaultBaseUrl) {
        setDefaultBaseUrl(defaultBaseUrl);
    }

    /**
     * Override the normal web driver instance to be used for testing purposes.
     */
    public void use_mock_driver(WebDriver webDriver) {
        WebdriverProxyFactory.getFactory().useMockDriver(webDriver);
    }

    public void stop_using_mock_driver() {
        WebdriverProxyFactory.getFactory().clearMockDriver()
    }

    public void uses_steps_from(Class<ScenarioSteps> stepsClass) {
        registeredSteps += stepsClass
    }

    public void tests_issue(String issue) {
        scenarioIssues.add(issue)
    }

    public void tests_issues(String... issues) {
        scenarioIssues.addAll(issues)
    }

    public void tests_feature(String featureName) {
        tags += TestTag.withName(featureName).andType("feature")
    }

    public void tests_story(String storyName) {
        tags += TestTag.withName(storyName).andType("story")
    }

    public void tag(String name, type) {
        tags += TestTag.withName(name).andType(type)
    }

    public List<String> getScenarioIssues() {
        return scenarioIssues;
    }


    public void tests_story(Class<?> story) {
        storyClass = story;
    }

    public void setDefaultBaseUrl(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getDefaultBaseUrl() {
        return defaultBaseUrl;
    }


    public void use_new_broswer_for_each_scenario() {
        resetBrowserInEachScenario = true;
    }

    public void use_unique_browser_session() {
        resetBrowserInEachScenario = false;
    }

    public boolean isResetBrowserInEachScenario() {
        return resetBrowserInEachScenario;
    }

    public void uses_driver(String driver) {
        checkRequestedDriverType(driver)
        requestedDriver = driver;
    }

    private void checkRequestedDriverType(def driver) {
        if (driver) {
            SupportedWebDriver.getDriverTypeFor(driver);
        }
    }


}
