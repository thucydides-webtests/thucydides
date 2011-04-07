package net.thucydides.core.model.loaders;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.reports.xml.NotAThucydidesReportException;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads a list of user stories from a given directory.
 * 
 */
public class UserStoryLoader {

    private static final class XmlFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(".xml");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserStoryLoader.class);

    /**
     * Load the user stories from the XML test results in a specified directory.
     * Test results will be split across user stories if the user stories are specified in the 
     * test run XML files.
     */
    public List<UserStoryTestResults> loadStoriesFrom(final File reportDirectory) throws IOException {

        List<UserStoryTestResults> userStories = new ArrayList<UserStoryTestResults>();
        
        XMLAcceptanceTestReporter acceptanceTestReporter = new XMLAcceptanceTestReporter();

        File[] reportFiles = getAllXMLFilesFrom(reportDirectory);

        for (File reportFile : reportFiles) {
            try {
                AcceptanceTestRun testRun = acceptanceTestReporter.loadReportFrom(reportFile);
                UserStoryTestResults userStoryResults = userStoryResultsFor(testRun, userStories);
                userStoryResults.recordTestRun(testRun);
            } catch (NotAThucydidesReportException e) {
                LOGGER.info("Skipping XML file - not a Thucydides report: " + reportFile);
            }
        }
        
        return userStories;
    }

    
    private UserStoryTestResults userStoryResultsFor(final AcceptanceTestRun testRun, 
                                                     final List<UserStoryTestResults> userStoryResults) {
        UserStory userStory = testRun.getUserStory();
        for (UserStoryTestResults userStoryResult : userStoryResults) {
            if (userStoryResult.containsResultsFor(userStory)) {
                return userStoryResult;
            }
        }
        UserStoryTestResults userStoryTestResults = new UserStoryTestResults(userStory);
        userStoryResults.add(userStoryTestResults);
        return userStoryTestResults;
    }




    private File[] getAllXMLFilesFrom(final File reportsDirectory) {
        return reportsDirectory.listFiles(new XmlFilenameFilter());
    }
}
