package net.thucydides.maven.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class WhenGeneratingThucydidesReportsInMaven {

    Verifier verifier;
    
    @After
    public void cleanupVerifier() throws VerificationException {
        if (verifier != null) {
            verifier.resetStreams();
        }
    }
    
    @Test(expected=VerificationException.class)
    public void the_plugin_should_fail_if_wrong_goal_specified() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/simple-project");

        initVerifier(testDir);

        verifier.executeGoal("thucydides:justdoit");
    }

    @Test
    public void the_aggregate_goal_should_produce_aggregate_test_reports() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/simple-project");

        initVerifier(testDir);

        verifier.executeGoal("thucydides:aggregate");
        verifier.verifyErrorFreeLog();

        //verifier.assertFilePresent("target/thucydides/all_user_stories.xml");
    }
    
    @Test
    @Ignore
    public void the_user_can_override_the_default_output_directory() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/project-with-different-output-dir");

        initVerifier(testDir);

        verifier.executeGoal("thucydides:aggregate");
        verifier.verifyErrorFreeLog();

        File expectedOutputFile = new File(verifier.getBasedir(), "target/out");
        System.out.println("Files = " + expectedOutputFile.list());
        verifier.assertFilePresent("target/out/all_user_stories.xml");

        String report = getStringFrom(new File(verifier.getBasedir(), "target/out/all_user_stories.xml"));
        assertThat(report, is(not("")));
    }

    @Test
    @Ignore
    public void the_user_can_filter_the_tests_to_be_run() throws Exception {
        
    }
    
    private Verifier initVerifier(File testDir) throws VerificationException, IOException {
        verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples", "simple-project", "1.0", "pom");
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples", "simple-project", "1.0","jar");
        return verifier;
    }
    
    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }
}
