package net.thucydides.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class WhenCleaningTheMavenReports {

    ThucydidesCleanMojo plugin;

    @Mock
    HtmlAggregateStoryReporter reporter;

    @Before
    public void setupPlugin() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_remove_project_history_files() throws Exception {

        plugin = new ThucydidesCleanMojo() {
            @Override
            protected HtmlAggregateStoryReporter getReporter() {
                return reporter;
            }
        };
        plugin.execute();

        verify(reporter).clearHistory();
    }


}
