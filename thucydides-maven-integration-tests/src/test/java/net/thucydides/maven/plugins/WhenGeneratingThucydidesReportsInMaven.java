package net.thucydides.maven.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class WhenGeneratingThucydidesReportsInMaven extends MavenThucydidesPluginTestSupport {


    @Test(expected=VerificationException.class)
    public void the_plugin_should_fail_if_wrong_goal_specified() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/simple-project");

        initVerifier(testDir);

        verifier.executeGoal("net.thucydides.maven.plugins:thucydides:justdoit");
    }

    @Test
    public void the_aggregate_goal_should_produce_aggregate_test_reports() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/simple-project");

        initVerifier(testDir);

        runTests();

        verifier.executeGoal(THUCYDIDES_AGGREGATE);
        verifier.verifyErrorFreeLog();

        verifier.assertFilePresent("target/thucydides/stories.html");
    }
}
