package net.thucydides.core.guice;

import com.google.inject.AbstractModule;
import net.thucydides.core.reports.json.ColorScheme;
import net.thucydides.core.reports.json.RelativeSizeColorScheme;

public class ThucydidesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ColorScheme.class).to(RelativeSizeColorScheme.class);
    }
}
