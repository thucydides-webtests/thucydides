package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.json.ColorScheme;
import net.thucydides.core.reports.json.RelativeSizeColorScheme;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebdriverManager;

public class ThucydidesModule extends AbstractModule {

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
    }
}
