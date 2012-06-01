package net.thucydides.jbehave.internals;

import net.thucydides.core.guice.Injectors;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.ParanamerConfiguration;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;

/**
 * A convenience class designed to make it easier to set up JBehave tests with Thucydides.
 */
public class ThucydidesJBehave {

    /**
     * Returns a default JBehave configuration object suitable for Thucydides tests.
     *
     * @return
     */
    public static Configuration defaultConfiguration(net.thucydides.core.webdriver.Configuration systemConfiguration) {
        return new ParanamerConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder().withDefaultFormats()
                        .withFormats(CONSOLE, HTML)
                        .withReporters(new ThucydidesReporter(systemConfiguration)));
    }

    public static Configuration defaultConfiguration() {
        return defaultConfiguration(Injectors.getInjector().getInstance(net.thucydides.core.webdriver.Configuration.class));
    }
}
