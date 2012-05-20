package net.thucydides.jbehave;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.ParanamerConfiguration;
import org.jbehave.core.reporters.StoryReporterBuilder;

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
    public static Configuration defaultConfiguration() {
        return new ParanamerConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder().withDefaultFormats()
                        .withFormats(CONSOLE, HTML)
                        .withReporters(new ThucydidesReporter()));
    }
}
