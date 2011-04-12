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
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.reports.ReportService
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter
import com.google.common.collect.ImmutableList
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.model.AcceptanceTestRun;

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;

    def reportService;

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

    def initializeReportService() {
        reportService = new ReportService(getConfiguration().getOutputDirectory(),
                                          getConfiguration().getDefaultReporters());
    }

    def generateReportsFor(final List<AcceptanceTestRun> testRunResults) {
        reportService.generateReportsFor(testRunResults);
    }

    def initializePagesObject(Binding binding) {
        Pages pages = new Pages(getWebdriverManager().getWebdriver());
        pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl());
        binding.setVariable("pages", pages);
        pages.start()
        return pages;
    }


    def initializeStepsLibraries(Pages pages, Binding binding) {

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

        closeDriver(binding);

        //generateReportsFor(getTestRunResults());

        return super.afterStory(binding);
    }

    private def closeDriver(Binding binding) {
        WebDriver driver = (WebDriver) binding.getVariable("driver");
        driver.close();
        driver.quit()
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

    /**
     * The default reporters applicable for standard test runs.
     */
    public Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLAcceptanceTestReporter(),
                                new HtmlAcceptanceTestReporter());
    }
}
