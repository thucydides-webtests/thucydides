package net.thucydides.core.model.userstories;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.xml.NotAThucydidesReportException;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Loads a list of user stories from a given directory.
 * 
 */
public class UserStoryLoader {

    /**
     * Load the user stories from the XML test results in a specified directory.
     * Test results will be split across user stories if the user stories are specified in the 
     * test run XML files.
     */
    public List<StoryTestResults> loadFrom(final File reportDirectory) throws IOException {

        List<StoryTestResults> stories = new ArrayList<StoryTestResults>();
        
        XMLTestOutcomeReporter testOutcomeReporter = new XMLTestOutcomeReporter();

        List<TestOutcome> testOutcomes = testOutcomeReporter.loadReportsFrom(reportDirectory);

        for (TestOutcome testOutcome : testOutcomes) {
            if (testOutcome.getUserStory() != null) {
                StoryTestResults storyResults = userStoryResultsFor(testOutcome, stories);
                storyResults.recordTestRun(testOutcome);
            }
        }
        
        return stories;
    }

    private StoryTestResults userStoryResultsFor(final TestOutcome testOutcome,
                                                 final List<StoryTestResults> storyResults) {
        Story userStory = testOutcome.getUserStory();
        for (StoryTestResults storyResult : storyResults) {
            if (storyResult.containsResultsFor(userStory)) {
                return storyResult;
            }
        }
        StoryTestResults storyTestResults = new StoryTestResults(userStory);
        storyResults.add(storyTestResults);
        return storyTestResults;
    }
}
