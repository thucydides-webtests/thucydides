package net.thucydides.maven.plugins;

import net.thucydides.core.reports.html.HtmlUserStoryTestReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.mockito.Mockito.verify;


public class WhenGeneratingAnAggregateReport {

    ThucydidesReporterMojo plugin;

    @Mock
    File outputDirectory;

    @Mock
    File sourceDirectory;

    @Mock
    HtmlUserStoryTestReporter reporter;

    @Before
    public void setupPlugin() {

        MockitoAnnotations.initMocks(this);

        plugin = new ThucydidesReporterMojo();
        plugin.setOutputDirectory(outputDirectory);
        plugin.setSourceDirectory(sourceDirectory);
        plugin.setReporter(reporter);
    }

    @Test
    public void the_output_directory_can_be_set_via_the_plugin_configuration() throws Exception {

        plugin.execute();

        verify(reporter).setOutputDirectory(outputDirectory);

    }


    @Test
    public void the_aggregate_report_should_be_generated_using_the_specified_source_directory() throws Exception {

        plugin.execute();

        verify(reporter).generateReportsForStoriesFrom(sourceDirectory);

    }
}
