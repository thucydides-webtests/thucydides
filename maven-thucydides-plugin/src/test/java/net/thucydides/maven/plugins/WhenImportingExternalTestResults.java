package net.thucydides.maven.plugins;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


public class WhenImportingExternalTestResults {

    ThucydidesAdaptorMojo plugin;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;

    @Before
    public void setupPlugin() throws IOException {

        outputDirectory = temporaryFolder.newFolder("sample-output");

        plugin = new ThucydidesAdaptorMojo();
        plugin.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_data_from_specified_test_results() throws Exception {
        plugin.setFormat("xunit");
        plugin.setSource(getResourcesAt("/xunit-sample-output"));

        plugin.execute();

        assertThat(outputDirectory.list(xmlFiles())).hasSize(5);
    }

    private FilenameFilter xmlFiles() {
        return new FilenameFilter() {

            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(".xml");
            }
        };
    }

    private File getResourcesAt(String path) {
        return new File(getClass().getResource(path).getFile());
    }
}
