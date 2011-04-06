package net.thucydides.easyb;


import groovy.lang.Binding;
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
    
    /**
     * Retrieve the runner configuration from an external source.
     */

    public ThucydidesPlugin() {
        println "instanciating plugin " + this

        Object.mixin ThucydidesExtensions;
    }


    @Override
    public String getName() {
        println "get name for plugin " + this
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
        println "before story"
        println "Configuration: " + configuration;
        println "Initializing pages with base URL " + getConfiguration().getDefaultBaseUrl()

        WebDriver driver = getWebdriverManager().getWebdriver();        
        binding.setVariable("driver", driver);
        binding.setVariable("thucydides", configuration);

        Pages pages = new Pages(getWebdriverManager().getWebdriver());
        pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl());
        binding.setVariable("pages", pages);
        pages.start();


        return super.beforeStory(binding);
    }
    

    @Override
    public Object beforeScenario(final Binding binding) {
        LOGGER.debug("Before scenario");
        return super.beforeScenario(binding);
    }
    
    @Override
    public Object afterStory(final Binding binding) {
        WebDriver driver = (WebDriver) binding.getVariable("driver");
        driver.quit();
        return super.afterStory(binding);
    }
    
    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    public PluginConfiguration getConfiguration() {
        return PluginConfiguration.getInstance();
    }

        
}
