package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
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

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ColorScheme.class).to(RelativeSizeColorScheme.class);
        bind(SystemClock.class).to(InternalSystemClock.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class);
        bind(EnvironmentVariables.class).to(SystemEnvironmentVariables.class);
        bind(Configuration.class).to(SystemPropertiesConfiguration.class);
        bind(IssueTracking.class).to(SystemPropertiesIssueTracking.class);
    }
}
