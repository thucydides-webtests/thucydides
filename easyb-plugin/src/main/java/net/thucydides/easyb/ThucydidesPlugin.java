package net.thucydides.easyb;

import groovy.lang.Binding;

import org.easyb.plugin.BasePlugin;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;

public class ThucydidesPlugin extends BasePlugin {

    /**
     * Creates new browser instances. The Browser Factory's job is to provide
     * new web driver instances. It is designed to isolate the test runner from
     * the business of creating and managing WebDriver drivers.
     */
    private WebDriverFactory webDriverFactory;

    WebdriverManager webdriverManager;
  
    /**
     * Retrieve the runner configuration from an external source.
     */
    private Configuration configuration;

    @Override
    public String getName() {
        return "thucydides";
    }
    
    @Override
    public Object beforeStory(Binding binding) {
        webDriverFactory = new WebDriverFactory();
        webdriverManager = new WebdriverManager(webDriverFactory, getConfiguration());
        
        return super.beforeStory(binding);
    }
    
    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    /**
     * Set the configuration for a test runner.
     * @param configuration
     */
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
        
}
