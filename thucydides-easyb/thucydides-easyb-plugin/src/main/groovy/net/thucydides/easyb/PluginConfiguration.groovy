package net.thucydides.easyb

import net.thucydides.core.steps.ScenarioSteps

public class PluginConfiguration {

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

    def defaultBaseUrl;

    def registeredSteps = [];

    /**
     * Define the base URL to be used for this story.
     */
    public void uses_default_base_url(String defaultBaseUrl) {
        println "uses default base url of " + defaultBaseUrl;
        setDefaultBaseUrl(defaultBaseUrl);
    }

    public void uses_steps_from(Class<ScenarioSteps> stepsClass) {
        println "Declaring step library $stepsClass"
        registeredSteps += stepsClass
        println "Registered steps: $registeredSteps"
    }


    public void setDefaultBaseUrl(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getDefaultBaseUrl() {
        return defaultBaseUrl;
    }

}
