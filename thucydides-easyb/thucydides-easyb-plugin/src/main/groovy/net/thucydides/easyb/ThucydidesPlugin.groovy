package net.thucydides.easyb;


import net.thucydides.core.pages.Pages;


import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;

import org.easyb.plugin.BasePlugin;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;
  
    private boolean isBrowserOpen = false;
    
    /**
     * Retrieve the runner configuration from an external source.
     */
    
    private PluginConfiguration configuration = new PluginConfiguration();

    /**
     * Define the default URL to be used when opening web pages with Thucydides
     * @param defaultUrl
     */
    public void use_default_url(String defaultUrl) {
        LOGGER.info("Setting default URL to " + defaultUrl);
        
    }
    
    @Override
    public String getName() {
        return "thucydides";
    }

    protected WebdriverManager getWebdriverManager() {
        if (webdriverManager == null) {
            webdriverManager = new WebdriverManager(getDefaultWebDriverFactory());        
        }
        return webdriverManager;
    }
    
    protected WebDriverFactory getDefaultWebDriverFactory() {
        return new WebDriverFactory();
    }
    
    
    
    @Override
    public Object beforeStory(final Binding binding) {
        
        WebDriver driver = getWebdriverManager().getWebdriver();        
        binding.setVariable("driver", driver);
        binding.setVariable("thucydides", configuration);
        
        return super.beforeStory(binding);
    }
    
    private void initializePagesInFirstScenario(final Binding binding) {
        if (!isBrowserOpen) {
            Pages pages = new Pages(getWebdriverManager().getWebdriver());
            pages.setDefaultBaseUrl(configuration.getDefaultBaseUrl());
            binding.setVariable("pages", pages);      
            pages.start();
            isBrowserOpen = true;
        }
    }
    
    @Override
    public Object beforeScenario(final Binding binding) {
        LOGGER.debug("Before scenario");
        initializePagesInFirstScenario(binding);
        return super.beforeScenario(binding);
    }
    
    @Override
    public Object afterStory(final Binding binding) {
        WebDriver driver = (WebDriver) binding.getVariable("driver");
        driver.close();
        return super.afterStory(binding);
    }
    
    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected PluginConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Set the configuration for a test runner.
     * @param configuration
     */
//    public void setConfiguration(final Configuration configuration) {
//        this.configuration = configuration;
//    }
        
}
