package net.thucydides.junit.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.guice.DatabaseConfig;
import net.thucydides.core.guice.EnvironmentVariablesDatabaseConfig;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.ReportService;
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
import net.thucydides.core.statistics.dao.HibernateTestOutcomeHistoryDAO;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.statistics.database.LocalH2ServerDatabase;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
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
import net.thucydides.junit.listeners.TestCountListener;
import net.thucydides.junit.listeners.TestCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

public class ThucydidesJUnitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StepListener.class).annotatedWith(TestCounter.class)
                                .toProvider(TestCountListenerProvider.class).in(Singleton.class);
    }

    public static class TestCountListenerProvider implements Provider<StepListener> {

        public StepListener get() {
            EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
            return new TestCountListener(environmentVariables);
        }
    }
}
