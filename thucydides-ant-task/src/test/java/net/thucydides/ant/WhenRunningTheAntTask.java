package net.thucydides.ant;

import java.net.URISyntaxException;

import static java.lang.Thread.currentThread;

public class WhenRunningTheAntTask extends ThucydidesAntTaskTestBase {
    public void setUp() throws Exception {
        String antFile = currentThread().getContextClassLoader().getResource("build.xml").getFile();
        configureProject(antFile);
        cleanReportsIn("test-outcomes");
    }

    public void testShouldExecuteTaskWithDefaultValues() throws URISyntaxException {
        executeTarget("thucydides.reports");
        thucydidesReportsShouldAppearIn("test-outcomes");
    }
}
