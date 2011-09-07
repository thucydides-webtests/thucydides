package net.thucydides.core.guice;

import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.json.ColorScheme;
import net.thucydides.core.reports.json.RelativeSizeColorScheme;

import com.google.inject.AbstractModule;
import net.thucydides.core.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.core.reports.templates.TemplateManager;

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ColorScheme.class).to(RelativeSizeColorScheme.class);
        bind(SystemClock.class).to(InternalSystemClock.class);
        bind(TemplateManager.class).to(FreeMarkerTemplateManager.class);
    }
}
