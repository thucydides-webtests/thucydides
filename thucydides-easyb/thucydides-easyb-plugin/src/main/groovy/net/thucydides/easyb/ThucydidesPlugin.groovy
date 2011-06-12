package net.thucydides.easyb;


import com.google.common.collect.ImmutableList
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.pages.Pages
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.reports.ReportService
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepFactory
import net.thucydides.core.steps.StepListener
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverManager
import org.easyb.listener.ListenerFactory
import org.easyb.plugin.BasePlugin
import org.openqa.selenium.WebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static net.thucydides.easyb.StepName.nameOf

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;

    private runningFirstScenario = true;

    ReportService reportService;

    StepFactory stepFactory;

    StepListener stepListener;

    Configuration systemConfiguration;

    boolean pluginInitialized = false;

    Pages pages;

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

    private boolean useUniqueBrowser() {
        Configuration.useUniqueBrowser
    }

    private void initializePlugin(final Binding binding) {
        pages = newPagesInstanceIn(binding)
        initializeStepFactoryAndListeners()
        initializeReportService()

        pluginInitialized = true;
    }

    protected void resetWebdriverManagerIfRequired() {
        if (!useUniqueBrowser()) {
            webdriverManager = null;
        }
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

    private WebDriver getWebDriver() {
        return getWebdriverManager().getWebdriver()
    }

    @Override
    public Object beforeStory(final Binding binding) {
        if (pluginInitialized) {
            resetWebdriverManagerIfRequired()
        } else {
            initializePlugin(binding)
        }
        binding.setVariable("pages", pages)
        binding.setVariable("driver", getWebDriver());
        binding.setVariable("thucydides", configuration);

        initializeStepsLibraries(binding);

        testRunStarted(binding);

        return super.beforeStory(binding);
    }

    private def testRunStarted(def binding) {
        def storyName = lookupStoryNameFrom(binding)
        def storyFile = lookupStoryFileFrom(binding)
        Story story = Story.withId(storyFile, storyName);
        stepListener.testRunStartedFor(story);
    }



    def lookupStoryNameFrom(def binding) {
        String sourceFile = binding.variables['sourceFile']
        if (sourceFile == null) {
            throw new IllegalArgumentException("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")
        }
        String sourceFilename = new File(sourceFile).name
        sourceFilename.substring(0, sourceFilename.lastIndexOf("."))
    }

    def lookupStoryFileFrom(def binding) {
        String sourceFile = binding.variables['sourceFile']
        if (sourceFile == null) {
            throw new IllegalArgumentException("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")
        }
        return sourceFile;
    }

    private def initializeStepFactoryAndListeners() {

        stepFactory = new StepFactory(pages)
        stepListener = new BaseStepListener(getSystemConfiguration().outputDirectory, pages)
        stepFactory.addListener(stepListener)

        ThucydidesListenerBuilder.setCurrentStepListener(stepListener);
        ListenerFactory.registerBuilder(new ThucydidesListenerBuilder());
    }



    @Override
    public Object beforeScenario(final Binding binding) {

        if (!runningFirstScenario && getConfiguration().isResetBrowserInEachScenario()) {
            resetDriver(binding)
        }
        return super.beforeScenario(binding);
    }


    @Override
    Object beforeGiven(Binding binding) {
        return super.beforeGiven(binding)
    }

    @Override
    Object beforeWhen(Binding binding) {
        return super.beforeWhen(binding)
    }

    @Override
    Object beforeThen(Binding binding) {
        return super.beforeThen(binding)
    }


    @Override
    public Object afterScenario(final Binding binding) {
        runningFirstScenario = false;
        return super.afterScenario(binding);
    }


    @Override
    public Object afterStory(final Binding binding) {
        stepFactory.notifyStepFinished()

        generateReportsFor(stepListener.testRunResults);

        closeDriver(binding);

        return super.afterStory(binding);
    }

    @Override
    Object afterGiven(Binding binding) {
        if (stepListener.aStepHasFailed()) {
            raiseError()
        }
    }

    @Override
    Object afterWhen(Binding binding) {
        if (stepListener.aStepHasFailed()) {
            raiseError()
        }
    }


    @Override
    Object afterThen(Binding binding) {
        if (stepListener.aStepHasFailed()) {
            raiseError()
        }
    }

    private def raiseError() {
        def error = stepListener.stepError
        if (errorIsNew(error)) {
            throw error
        } else {
            throw new AssertionError("Step skipped due to previous step failure")
        }
    }

    def raisedErrors = []

    boolean errorIsNew(Throwable error) {
        if (raisedErrors.contains(error)) {
            false
        } else {
            raisedErrors.add(error)
            true
        }
    }


    def initializeReportService() {

        reportService = new ReportService(getSystemConfiguration().outputDirectory, getDefaultReporters());
    }

    def generateReportsFor(final List<TestOutcome> testRunResults) {

        reportService.generateReportsFor(testRunResults);
    }

    private Pages newPagesInstanceIn(Binding binding) {
        Pages pages = new Pages()
        pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl())
        WebDriver driver = getWebDriver();
        pages.setDriver(driver);
        pages.notifyWhenDriverOpens()
        return pages
    }

    def initializeStepsLibraries(Binding binding) {

        configuration.registeredSteps.each { stepLibraryClass ->
            def stepLibrary = proxyFor(stepLibraryClass)
            binding.setVariable(nameOf(stepLibraryClass), stepLibrary)
        }
    }

    private def proxyFor(def stepLibraryClass) {
        stepFactory.newSteps(stepLibraryClass)
    }

    private def closeDriver(Binding binding) {
        if (!useUniqueBrowser()) {
            getWebdriverManager().closeDriver()
        }
    }

    private def resetDriver(Binding binding) {
        Pages pages = binding.getVariable("pages")
        pages.start()
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
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = new Configuration();
        }
        return systemConfiguration;
    }

    /**
     * The default reporters applicable for standard test runs.
     */
    public Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLTestOutcomeReporter(),
        new HtmlAcceptanceTestReporter());
    }
}
