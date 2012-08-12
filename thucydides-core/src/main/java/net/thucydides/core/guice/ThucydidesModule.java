package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.jpa.JPAProvider;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.html.history.JPATestResultSnapshotDAO;
import net.thucydides.core.reports.html.history.TestResultSnapshotDAO;
import net.thucydides.core.reports.json.ColorScheme;
import net.thucydides.core.reports.json.RelativeSizeColorScheme;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.reports.saucelabs.SaucelabsLinkGenerator;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.screenshots.MultithreadScreenshotProcessor;
import net.thucydides.core.screenshots.ScreenshotProcessor;
import net.thucydides.core.statistics.HibernateTestStatisticsProvider;
import net.thucydides.core.statistics.Statistics;
import net.thucydides.core.statistics.StatisticsListener;
import net.thucydides.core.statistics.TestStatisticsProvider;
import net.thucydides.core.statistics.dao.JPATestOutcomeHistoryDAO;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
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
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebdriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.Properties;

public class ThucydidesModule extends AbstractModule {

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
            = new ThreadLocal<EntityManager>();

    private final Logger LOGGER = LoggerFactory.getLogger(ThucydidesModule.class);

    @Override
    protected void configure() {
        bind(ColorScheme.class).to(RelativeSizeColorScheme.class).in(Singleton.class);
        bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class).in(Singleton.class);
        bind(EnvironmentVariables.class).to(SystemEnvironmentVariables.class).in(Singleton.class);
        bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
        bind(IssueTracking.class).to(SystemPropertiesIssueTracking.class).in(Singleton.class);
        bind(WebdriverManager.class).to(ThucydidesWebdriverManager.class);
        bind(BatchManager.class).to(SystemVariableBasedBatchManager.class);
        bind(LinkGenerator.class).to(SaucelabsLinkGenerator.class);
        bind(LocalPreferences.class).to(PropertiesFileLocalPreferences.class).in(Singleton.class);
        bind(ScreenshotProcessor.class).to(MultithreadScreenshotProcessor.class).in(Singleton.class);
        bind(DatabaseConfig.class).to(EnvironmentVariablesDatabaseConfig.class).in(Singleton.class);
        bind(TestOutcomeHistoryDAO.class).to(JPATestOutcomeHistoryDAO.class).in(Singleton.class);
        bind(TestStatisticsProvider.class).to(HibernateTestStatisticsProvider.class).in(Singleton.class);
        bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
        bind(DependencyInjectorService.class).to(ClasspathDependencyInjectorService.class).in(Singleton.class);

        bind(StepListener.class).annotatedWith(Statistics.class).to(StatisticsListener.class).in(Singleton.class);
        bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class).in(Singleton.class);
        bind(TestResultSnapshotDAO.class).to(JPATestResultSnapshotDAO.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Inject
    public LocalDatabase provideLocalDatabase(EnvironmentVariables environmentVariables) {
        return new LocalH2ServerDatabase(environmentVariables);
    }

    @Provides
    @Singleton
    @Inject
    public EntityManagerFactory provideEntityManagerFactory(DatabaseConfig databaseConfig,
                                                            LocalDatabase localDatabase) {

        if (databaseConfig.isUsingLocalDatabase()) {
            startIfNotAlreadyRunning(localDatabase);
        }

        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = createEntityManagerFactory(databaseConfig);
        } catch (SQLException e) {
            LOGGER.error("Could not connect to statistics database - statistics will be disabled", e);
            databaseConfig.disable();
        }

        return entityManagerFactory;

    }

    private EntityManagerFactory createEntityManagerFactory(DatabaseConfig databaseConfig) throws SQLException {

        Properties databaseConfigProperties = databaseConfig.getProperties();
        String jaProviderName = databaseConfigProperties.getProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName());

        JPAProvider jpaProvider = JPAProvider.valueOf(jaProviderName);
        String persistenceUnitName = jpaProvider.getPersistenceUnitName();
        LOGGER.info("Persistence Unit used" + persistenceUnitName);

        return Persistence.createEntityManagerFactory(persistenceUnitName, databaseConfig.getProperties());
    }

    private void startIfNotAlreadyRunning(LocalDatabase localDatabase) {
        if (!localDatabase.isAvailable()) {
            localDatabase.start();
            addShutdownHookFor(localDatabase);
        }
    }

    private void addShutdownHookFor(final LocalDatabase localDatabase) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                localDatabase.stop();
            }
        });
    }

    /**
     * Used for testing
     */
    protected void clearEntityManagerCache() {
        ENTITY_MANAGER_CACHE.remove();
    }
}
