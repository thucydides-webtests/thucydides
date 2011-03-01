package net.thucydides.maven.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

public class WhenGeneratingThucydidesReportsInMaven {

    @Test
    public void the_aggregate_goal_should_produce_aggregate_test_reports() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/simple-project");

        Verifier verifier = getVerifier(testDir);

        verifier.executeGoal("thucydides:aggregate");

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent("target/thucydides/all_user_stories.xml");
    }
    
    @Test
    public void the_user_can_override_the_default_output_directory() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/project-with-different-output-dir");

        Verifier verifier = getVerifier(testDir);

        verifier.executeGoal("thucydides:aggregate");

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent("target/out/all_user_stories.xml");
    }

    
    private Verifier getVerifier(File testDir) throws VerificationException, IOException {
        Verifier verifier;
        verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples", "simple-project", "1.0", "pom");
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples", "simple-project", "1.0","jar");
        return verifier;
    }
}
