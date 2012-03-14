package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.json.ColorScheme;
import net.thucydides.core.reports.json.RelativeSizeColorScheme;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.reports.saucelabs.SaucelabsLinkGenerator;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.statistics.Statistics;
import net.thucydides.core.statistics.StatisticsListener;
import net.thucydides.core.statistics.dao.HibernateTestOutcomeHistoryDAO;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.steps.ConsoleLoggingListener;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.LocalPreferences;
import net.thucydides.core.util.PropertiesFileLocalPreferences;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebdriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ThucydidesModule extends AbstractModule {

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
            = new ThreadLocal<EntityManager>();

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
        bind(TestOutcomeHistoryDAO.class).to(HibernateTestOutcomeHistoryDAO.class);

        bind(StepListener.class).annotatedWith(Statistics.class).to(StatisticsListener.class);
        bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class);
    }

    @Provides
    @Singleton
    @Inject
    public EntityManagerFactory provideEntityManagerFactory(EnvironmentVariables environmentVariables) {
        Map<String, String> properties = new HashMap<String, String>();

        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String defaultDatabase = defaultThucydidesDirectory + "/stats";
        String driver = environmentVariables.getProperty("thucydides.statistics.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        String url = environmentVariables.getProperty("thucydides.statistics.url", "jdbc:hsqldb:file:" + defaultDatabase);
        String username = environmentVariables.getProperty("thucydides.statistics.username", "sa");
        String password = environmentVariables.getProperty("thucydides.statistics.password", "");
        String dialect = environmentVariables.getProperty("thucydides.statistics.dialect", "org.hibernate.dialect.HSQLDialect");

        properties.put("hibernate.connection.driver_class", driver);
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.connection.pool_size", "1");
        properties.put("hibernate.hbm2ddl.auto", "create");
        return Persistence.createEntityManagerFactory("db-manager", properties);
    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) {
            ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
        }
        return entityManager;
    }

    /**
     * Used for testing
     */
    protected void clearEntityManagerCache() {
        ENTITY_MANAGER_CACHE.remove();
    }
}
