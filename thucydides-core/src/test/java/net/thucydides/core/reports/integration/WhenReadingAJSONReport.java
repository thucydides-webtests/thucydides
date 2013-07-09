package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.base.Optional;

public class WhenReadingAJSONReport {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private JSONTestOutcomeReporter outcomeReporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() throws IOException {
        outcomeReporter = new JSONTestOutcomeReporter();
        outputDirectory = temporaryDirectory.newFolder();
        outcomeReporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_acceptance_test_report_from_json_file() throws Exception {
    	    	    	
    	 String expectedReport = "{\"methodName\":\"should_do_this\"," +
					"\"testCase\":{\"classname\":\"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$ATestScenarioWithIssues\"," +
					"\"issues\":[\"#123\",\"#456\",\"#789\"]}," +
					"\"testSteps\":[{\"description\":\"step 1\",\"duration\":0," +
					"\"startTime\":1373169535861,\"screenshots\":[],\"result\":\"SUCCESS\",\"children\":[]}]," +
					"\"userStory\":{\"userStoryClass\":{\"classname\":\"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"}," +
					"\"qualifiedStoryClassName\":\"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\"," +
					"\"storyName\":\"A user story\",\"path\":\"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"}," +
					"\"additionalIssues\":[],\"duration\":0,\"startTime\":1356994800000," +
					"\"groupStack\":[],\"issueTracking\":{},\"linkGenerator\":{}," +
					"\"qualifier\":[null],\"manualTest\":false,\"NO_HEADERS\":[]}";
    	

        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, expectedReport);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getIssues(), hasItems("#123", "#456", "#789"));
        assertThat(testOutcome.get().getStartTime(), notNullValue());
    }
    
    @Test
    public void should_load_manual_acceptance_test_report_from_json_file() throws Exception {
        String storedReportJson =
                "{\n" + 
                "  \"methodName\": \"should_do_this\",\n" + 
                "  \"testCase\": {\n" + 
                "    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
                "  },\n" + 
                "  \"testSteps\": [\n" + 
                "    {\n" + 
                "      \"description\": \"step 1\",\n" + 
                "      \"duration\": 0,\n" + 
                "      \"startTime\": 1373281473442,\n" + 
                "      \"screenshots\": [],\n" + 
                "      \"result\": \"SUCCESS\",\n" + 
                "      \"children\": []\n" + 
                "    }\n" + 
                "  ],\n" + 
                "  \"userStory\": {\n" + 
                "    \"userStoryClass\": {\n" + 
                "      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"\n" + 
                "    },\n" + 
                "    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\",\n" + 
                "    \"storyName\": \"A user story\",\n" + 
                "    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
                "  },\n" + 
                "  \"storedTitle\": \"Should do this\",\n" + 
                "  \"additionalIssues\": [],\n" + 
                "  \"duration\": 0,\n" + 
                "  \"startTime\": 1356994800000,\n" + 
                "  \"groupStack\": [],\n" + 
                "  \"issueTracking\": {},\n" + 
                "  \"linkGenerator\": {},\n" + 
                "  \"qualifier\": [\n" + 
                "    null\n" + 
                "  ],\n" + 
                "  \"manualTest\": true,\n" + 
                "  \"NO_HEADERS\": []\n" + 
                "}";
         
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().isManual(), is(true));        
    }

    @Test
    public void should_load_a_qualified_acceptance_test_report_from_json_file() throws Exception {
        
        String storedReportJson  = 
        		"{\n" + 
        		"  \"methodName\": \"should_do_this\",\n" + 
        		"  \"testCase\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"testSteps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373281913986,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ],\n" + 
        		"  \"userStory\": {\n" + 
        		"    \"userStoryClass\": {\n" + 
        		"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"\n" + 
        		"    },\n" + 
        		"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\",\n" + 
        		"    \"storyName\": \"A user story\",\n" + 
        		"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
        		"  },\n" + 
        		"  \"storedTitle\": \"Should do this [a qualifier]\",\n" + 
        		"  \"additionalIssues\": [],\n" + 
        		"  \"duration\": 0,\n" + 
        		"  \"startTime\": 1356994800000,\n" + 
        		"  \"groupStack\": [],\n" + 
        		"  \"issueTracking\": {},\n" + 
        		"  \"linkGenerator\": {},\n" + 
        		"  \"qualifier\": [\n" + 
        		"    \"a qualifier\"\n" + 
        		"  ],\n" + 
        		"  \"manualTest\": false,\n" + 
        		"  \"NO_HEADERS\": []\n" + 
        		"}"; 
        
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getQualifier().get(), is("a qualifier"));
        assertThat(testOutcome.get().getTitle(), containsString("[a qualifier]"));
    }  
}
