package net.thucydides.easyb;


import groovy.lang.Binding;
import net.thucydides.core.pages.Pages;

import static net.thucydides.easyb.StepName.*;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;

import org.easyb.plugin.BasePlugin;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import net.thucydides.core.webdriver.Configuration;

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;

    /**
     * Retrieve the runner configuration from an external source.
     */

    public ThucydidesPlugin() {
        Object.mixin ThucydidesExtensions;
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

        Pages pages = initializePagesObject(binding);
        initializeStepsLibraries(pages, binding);

        return super.beforeStory(binding);
    }

    private def initializePagesObject(Binding binding) {
        Pages pages = new Pages(getWebdriverManager().getWebdriver());
        pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl());
        binding.setVariable("pages", pages);
        pages.start()
        return pages;
    }


    private def initializeStepsLibraries(Pages pages, Binding binding) {

        configuration.registeredSteps.each { stepLibraryClass ->
            def stepLibrary = stepLibraryClass.newInstance(pages)
            binding.setVariable(nameOf(stepLibraryClass), stepLibrary)
        }

    }

    @Override
    public Object beforeScenario(final Binding binding) {
        LOGGER.debug("Before scenario");
        return super.beforeScenario(binding);
    }
    
    @Override
    public Object afterStory(final Binding binding) {
        LOGGER.debug("After scenario");
        WebDriver driver = (WebDriver) binding.getVariable("driver");
        driver.close();
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

    public PluginConfiguration resetConfiguration() {
        return PluginConfiguration.reset();
    }

}
