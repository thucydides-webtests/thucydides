package net.thucydides.easyb;

public class PluginConfiguration {

    String defaultBaseUrl
    
    /**
     * Define the base URL to be used for this story.
     */
    public void uses_default_base_url(String defaultBaseUrl) {
        setDefaultBaseUrl(defaultBaseUrl);
    }

}
