package net.thucydides.maven.plugins;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.StringWriter;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
