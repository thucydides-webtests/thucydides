package net.thucydides.maven.plugins;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.After;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 7/04/11
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class MavenThucydidesPluginTestSupport {
    protected static final String THUCYDIDES_AGGREGATE = "net.thucydides.maven.plugins:maven-thucydides-plugin:aggregate";

    Verifier verifier;

    @After
    public void cleanupVerifier() throws VerificationException {
        if (verifier != null) {
            verifier.resetStreams();
        }
    }

    protected void runTests() {
        try {
            verifier.executeGoal("clean");
            verifier.executeGoal("test");
        } catch (VerificationException e) {
            e.printStackTrace();
        }
    }

    protected Verifier initVerifier(File testDir) throws VerificationException, IOException {
        verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples1", "simple-project", "1.0", "pom");
        verifier.deleteArtifact("net.thucydides.maven.plugins.samples1", "simple-project", "1.0","jar");
        return verifier;
    }

    protected String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }
}
