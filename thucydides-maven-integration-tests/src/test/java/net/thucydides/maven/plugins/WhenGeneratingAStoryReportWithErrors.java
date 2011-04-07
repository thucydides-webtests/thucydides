package net.thucydides.maven.plugins;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class WhenGeneratingAStoryReportWithErrors extends MavenThucydidesPluginTestSupport {

    @Before
    public void generateTheHtmlReport() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/test-projects/project-with-errors");
        initVerifier(testDir);
        
        runTests();
    }

    @Test
    public void the_aggregate_goal_should_produce_a_story_and_home_report_even_with_test_errors() throws Exception {

        verifier.executeGoal(THUCYDIDES_AGGREGATE);

        verifier.assertFilePresent("target/thucydides/stories.html");
        verifier.assertFilePresent("target/thucydides/home.html");
   }

}
