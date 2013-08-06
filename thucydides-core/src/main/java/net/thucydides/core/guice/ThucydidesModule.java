package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.renderer.Asciidoc;
import net.thucydides.core.reports.renderer.AsciidocMarkupRenderer;
import net.thucydides.core.reports.renderer.MarkupRenderer;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.reports.saucelabs.SaucelabsLinkGenerator;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.requirements.ClasspathRequirementsProviderService;
import net.thucydides.core.requirements.RequirementsProviderService;
import net.thucydides.core.screenshots.ScreenshotProcessor;
import net.thucydides.core.screenshots.SingleThreadScreenshotProcessor;
import net.thucydides.core.statistics.AtomicTestCount;
import net.thucydides.core.statistics.TestCount;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.statistics.database.LocalH2ServerDatabase;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.ConsoleLoggingListener;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.LocalPreferences;
import net.thucydides.core.util.PropertiesFileLocalPreferences;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ElementProxyCreator;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebdriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ThucydidesModule extends AbstractModule {

//    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
//            = new ThreadLocal<EntityManager>();

    private final Logger LOGGER = LoggerFactory.getLogger(ThucydidesModule.class);

    @Override
    protected void configure() {
        bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class).in(Singleton.class);
        bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
        bind(IssueTracking.class).to(SystemPropertiesIssueTracking.class).in(Singleton.class);
        bind(WebdriverManager.class).to(ThucydidesWebdriverManager.class).in(Singleton.class);
        bind(BatchManager.class).toProvider(BatchManagerProvider.class).in(Singleton.class);
        bind(LinkGenerator.class).to(SaucelabsLinkGenerator.class);
        bind(ScreenshotProcessor.class).to(SingleThreadScreenshotProcessor.class).in(Singleton.class);

        //bind(DatabaseConfig.class).to(EnvironmentVariablesDatabaseConfig.class).in(Singleton.class);
        //bind(TestOutcomeHistoryDAO.class).to(HibernateTestOutcomeHistoryDAO.class).in(Singleton.class);
        //bind(TestStatisticsProvider.class).to(HibernateTestStatisticsProvider.class).in(Singleton.class);

        bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
        bind(RequirementsProviderService.class).to(ClasspathRequirementsProviderService.class).in(Singleton.class);
        bind(DependencyInjectorService.class).to(ClasspathDependencyInjectorService.class).in(Singleton.class);
        bind(FixtureProviderService.class).to(ClasspathFixtureProviderService.class).in(Singleton.class);

        //bind(StepListener.class).annotatedWith(Statistics.class).to(StatisticsListener.class).in(Singleton.class);
        bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class).in(Singleton.class);
        bind(ElementProxyCreator.class).to(SmartElementProxyCreator.class).in(Singleton.class);

        bind(TestCount.class).to(AtomicTestCount.class).in(Singleton.class);

        bind(MarkupRenderer.class).annotatedWith(Asciidoc.class).to(AsciidocMarkupRenderer.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public EnvironmentVariables provideEnvironmentVariables() {
        return createEnvironmentVariables();
    }

    protected EnvironmentVariables createEnvironmentVariables() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        LocalPreferences localPreferences = new PropertiesFileLocalPreferences(environmentVariables);
        try {
            localPreferences.loadPreferences();
        } catch (IOException e) {
            LOGGER.error("Could not load local preferences", e);
        }
        return environmentVariables;
    }

    @Provides
    @Singleton
    @Inject
    public LocalDatabase provideLocalDatabase(EnvironmentVariables environmentVariables) {
        return new LocalH2ServerDatabase(environmentVariables);
    }

//    @Provides
//    @Singleton
//    @Inject
//    public EntityManagerFactory provideEntityManagerFactory(DatabaseConfig databaseConfig,
//                                                            LocalDatabase localDatabase) {
//
//        if (databaseConfig.isUsingLocalDatabase()) {
//            startIfNotAlreadyRunning(localDatabase);
//        }
//
//        EntityManagerFactory entityManagerFactory = null;
//        try {
//            entityManagerFactory = createEntityManagerFactory(databaseConfig);
//        } catch (SQLException e) {
//            LOGGER.error("Could not connect to statistics database - statistics will be disabled", e);
//            databaseConfig.disable();
//        }
//
//        return entityManagerFactory;
//
//    }
//
//    private EntityManagerFactory createEntityManagerFactory(DatabaseConfig databaseConfig) throws SQLException {
//        return Persistence.createEntityManagerFactory("db-manager", databaseConfig.getProperties());
//    }

//    private void startIfNotAlreadyRunning(LocalDatabase localDatabase) {
//        if (!localDatabase.isAvailable()) {
//            localDatabase.start();
//            addShutdownHookFor(localDatabase);
//        }
//    }
//
//    private void addShutdownHookFor(final LocalDatabase localDatabase) {
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                localDatabase.stop();
//            }
//        });
//    }

    /**
     * Used for testing
     */
//    protected void clearEntityManagerCache() {
//        ENTITY_MANAGER_CACHE.remove();
//    }
}
