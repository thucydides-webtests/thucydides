package net.thucydides.easyb

import com.google.common.collect.ImmutableList
import net.thucydides.core.batches.BatchManager
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.pages.Pages
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.reports.ReportService
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter
import net.thucydides.core.steps.*
import net.thucydides.core.util.Inflector
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.webdriver.WebdriverManager
import org.easyb.listener.ListenerFactory
import org.easyb.plugin.BasePlugin
import org.openqa.selenium.WebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static net.thucydides.core.Thucydides.initializeTestSession
import static net.thucydides.easyb.StepName.defaultNameOf

public class ThucydidesPlugin extends BasePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesPlugin.class);

    private WebdriverManager webdriverManager;

    private runningFirstScenario = true;

    private BatchManager batchManager;

    ReportService reportService;

    StepFactory stepFactory;

    BaseStepListener baseStepListener;
    List<StepListener> stepListeners;
    StepPublisher stepPublisher;

    Configuration systemConfiguration;

    boolean pluginInitialized = false;

    Pages pages;

    /**
     * Retrieve the runner configuration from an external source.
     */
    public ThucydidesPlugin() {
        Object.mixin ThucydidesExtensions;
    }

    public ThucydidesPlugin(WebdriverManager manager) {
        this()
        webdriverManager = manager;
    }


    @Override
    public String getName() {
        return "thucydides";
    }

    private boolean useUniqueBrowser() {
        getSystemConfiguration().useUniqueBrowser
    }

    private void initializePlugin(final Binding binding) {
        pages = newPagesInstanceIn(binding)
        initializeStepFactoryAndListeners()
        initializeReportService()
        pluginInitialized = true;
    }

    protected void resetWebdriverManagerIfRequired() {
        if (!useUniqueBrowser()) {
            webdriverManager?.closeDriver();
            webdriverManager = null;
        }
    }

    protected WebdriverManager getWebdriverManager() {
        if (!webdriverManager) {
            webdriverManager = Injectors.injector.getInstance WebdriverManager
        }
        return webdriverManager
    }

    protected BatchManager getBatchManager() {
        if (!batchManager) {
            batchManager = Injectors.injector.getInstance BatchManager
        }
        return batchManager
    }

    private WebDriver getWebDriver() {
        return getWebdriverManager().getWebdriver(configuration.requestedDriver)
    }

    @Override
    public Object beforeStory(final Binding binding) {
        if (pluginInitialized) {
            resetWebdriverManagerIfRequired()
        } else {
            initializePlugin(binding)
        }
        initializeTestSession();
        binding.setVariable("pages", pages)
        binding.setVariable("driver", getWebDriver());
        binding.setVariable("thucydides", configuration);


        initializeStepsLibraries(binding);

        testRunStarted(binding);

        return super.beforeStory(binding);
    }



    private def testRunStarted(def binding) {
        def storyClass = configuration.storyClass
        def story
        if (storyClass) {
           story = Story.from(storyClass)
        } else {
            def storyName = lookupStoryNameFrom(binding)
            def storyFile = lookupStoryFileFrom(binding)
            story = Story.withId(storyFile, storyName)
            configuration.tag(storyName, "story")
        }
        StepEventBus.eventBus.testSuiteStarted(story)
        getBatchManager().registerTestCase(story.name)
    }



    def lookupStoryNameFrom(def binding) {
        String sourceFile = binding.variables['sourceFile']
        if (sourceFile == null) {
            throw new IllegalArgumentException("No easyb source file name found - are you using a recent version of easyb (1.1 or greater)?")
        }
        def sourceFilename = new File(sourceFile).name
        def basicStoryName = sourceFilename.substring(0, sourceFilename.lastIndexOf("."))

        new Inflector().of(basicStoryName).inHumanReadableForm().asATitle().toString()
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
        def outputDirectory = getSystemConfiguration().outputDirectory

        baseStepListener = Listeners.baseStepListener.withPages(pages).and().withOutputDirectory(outputDirectory)
        stepListeners = [baseStepListener,
                         Listeners.loggingListener]

        stepPublisher = (StepPublisher) baseStepListener;

        StepEventBus.eventBus.dropAllListeners();
        stepListeners.each { listener ->
            if (listener) StepEventBus.eventBus.registerListener(listener)
        }

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
    public Object afterScenario(final Binding binding) {
        LOGGER.info("AFTER SCENARIO")
        runningFirstScenario = false;

        if (getLatestTestOutcome()) {
            if (configuration.scenarioIssues) {
                getLatestTestOutcome().addIssues(configuration.scenarioIssues)
            }

            LOGGER.info("STORING TAGS " + configuration.tags)
            if (configuration.tags) {
                getLatestTestOutcome().setTags(configuration.tags)
            }
        }

        StepEventBus.eventBus.testFinished(getLatestTestOutcome());
        return super.afterScenario(binding);
    }

    private TestOutcome getLatestTestOutcome() {
        (testOutcomes.size() > 0) ? testOutcomes.last() : null;
    }

    public List<TestOutcome> getTestOutcomes() {
        return stepPublisher.testOutcomes;
    }

    @Override
    public Object afterStory(final Binding binding) {
        StepEventBus.eventBus.testSuiteFinished()
        StepEventBus.eventBus.dropAllListeners()
        StepEventBus.eventBus.clear()

        generateReportsFor(stepPublisher.testOutcomes);

        closeDriver(binding);

        return super.afterStory(binding);
    }

    @Override
    Object beforeGiven(Binding binding) {
        return super.beforeGiven(binding)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    Object beforeWhen(Binding binding) {
        return super.beforeWhen(binding)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    Object beforeThen(Binding binding) {
        return super.beforeThen(binding)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    Object afterGiven(Binding binding) {
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            raiseError()
        }
        return super.afterGiven(binding)
    }

    @Override
    Object afterWhen(Binding binding) {
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            raiseError()
        }
        return super.afterWhen(binding)
    }


    @Override
    Object afterThen(Binding binding) {
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            raiseError()
        }
        return super.afterThen(binding)
    }

    // TODO: This doesn't work
    private def raiseError() {
        def error = stepPublisher.testFailureCause
        if (errorIsNew(error)) {
            throw error
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
        reportService = Injectors.getInjector().getInstance(ReportService.class);
    }

    def generateReportsFor(final List<TestOutcome> testRunResults) {
        reportService.generateReportsFor(testRunResults);
    }

    private Pages newPagesInstanceIn(Binding binding) {
        Pages pages = new Pages()
        if (getConfiguration().getDefaultBaseUrl()) {
            pages.setDefaultBaseUrl(getConfiguration().getDefaultBaseUrl())
        }
        pages.setDriver(getWebDriver());
        return pages
    }

    def initializeStepsLibraries(Binding binding) {

        configuration.registeredSteps.each { stepLibraryClass ->
            def stepLibrary = proxyFor(stepLibraryClass)
            def customStepLibraryName = configuration.stepLibraryNameFor(stepLibraryClass.name)
            String stepLibraryName = (customStepLibraryName) ? customStepLibraryName : defaultNameOf(stepLibraryClass)
            binding.setVariable(stepLibraryName, stepLibrary)
        }
    }

    private def proxyFor(def stepLibraryClass) {
        stepFactory.getStepLibraryFor(stepLibraryClass)
    }

    private void closeDriver(Binding binding) {
        getWebdriverManager().closeDriver()
    }

    private void resetDriver(Binding binding) {
        getWebdriverManager().resetDriver()
        Pages pages = (Pages) binding.getVariable("pages")
        if (pageUrlHasBeenDefinedFor(pages)) {
            pages.start()
        }
    }

    def pageUrlHasBeenDefinedFor(def pages) {
        pages?.configuration?.defaultBaseUrl != null
    }
    /**
     * The configuration manages output directories and firefoxDriver types.
     * They can be defined as system values, or have sensible defaults.
     */
    public PluginConfiguration getConfiguration() {
        return PluginConfiguration.getInstance();
    }

    public PluginConfiguration resetConfiguration() {
        return PluginConfiguration.reset();
    }

    /**
     * The configuration manages output directories and firefoxDriver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
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
