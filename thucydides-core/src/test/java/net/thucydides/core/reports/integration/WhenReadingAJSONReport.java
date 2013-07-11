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
    	    	    	
    	 String expectedReport = 
    			 "{\n" + 
    			        	"  \"title\": \"Should do this\",\n" + 
    			        	"  \"name\": \"should_do_this\",\n" + 
    			        	"  \"test-case\": {\n" + 
    			        	"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$ATestScenarioWithIssues\",\n" + 
    			        	"    \"issues\": [\n" + 
    			        	"      \"#123\",\n" + 
    			        	"      \"#456\",\n" + 
    			        	"      \"#789\"\n" + 
    			        	"    ]\n" + 
    			        	"  },\n" + 
    			        	"  \"result\": \"SUCCESS\",\n" + 
    			        	"  \"steps\": \"1\",\n" + 
    			        	"  \"successful\": \"1\",\n" + 
    			        	"  \"failures\": \"0\",\n" + 
    			        	"  \"skipped\": \"0\",\n" + 
    			        	"  \"ignored\": \"0\",\n" + 
    			        	"  \"pending\": \"0\",\n" + 
    			        	"  \"duration\": \"0\",\n" + 
    			        	"  \"timestamp\": \"2013-01-01T00:00:00.000+01:00\",\n" + 
    			        	"  \"user-story\": {\n" + 
    			        	"    \"userStoryClass\": {\n" + 
    			        	"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"\n" + 
    			        	"    },\n" + 
    			        	"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\",\n" + 
    			        	"    \"storyName\": \"A user story\",\n" + 
    			        	"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
    			        	"  },\n" + 
    			        	"  \"issues\": [\n" + 
    			        	"    \"#456\",\n" + 
    			        	"    \"#789\",\n" + 
    			        	"    \"#123\"\n" + 
    			        	"  ],\n" + 
    			        	"  \"tags\": [\n" + 
    			        	"    {\n" + 
    			        	"      \"name\": \"A user story\",\n" + 
    			        	"      \"type\": \"story\"\n" + 
    			        	"    }\n" + 
    			        	"  ],\n" + 
    			        	"  \"test-steps\": [\n" + 
    			        	"    {\n" + 
    			        	"      \"description\": \"step 1\",\n" + 
    			        	"      \"duration\": 0,\n" + 
    			        	"      \"startTime\": 1373542631993,\n" + 
    			        	"      \"screenshots\": [],\n" + 
    			        	"      \"result\": \"SUCCESS\",\n" + 
    			        	"      \"children\": []\n" + 
    			        	"    }\n" + 
    			        	"  ]\n" + 
    			        	"}"	;
    	

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
        		"  \"title\": \"Should do this\",\n" + 
        		"  \"name\": \"should_do_this\",\n" + 
        		"  \"test-case\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"result\": \"SUCCESS\",\n" + 
        		"  \"steps\": \"1\",\n" + 
        		"  \"successful\": \"1\",\n" + 
        		"  \"failures\": \"0\",\n" + 
        		"  \"skipped\": \"0\",\n" + 
        		"  \"ignored\": \"0\",\n" + 
        		"  \"pending\": \"0\",\n" + 
        		"  \"duration\": \"0\",\n" + 
        		"  \"timestamp\": \"2013-01-01T00:00:00.000+01:00\",\n" + 
        		"  \"manual\": \"true\",\n" + 
        		"  \"user-story\": {\n" + 
        		"    \"userStoryClass\": {\n" + 
        		"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"\n" + 
        		"    },\n" + 
        		"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\",\n" + 
        		"    \"storyName\": \"A user story\",\n" + 
        		"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
        		"  },\n" + 
        		"  \"issues\": [],\n" + 
        		"  \"tags\": [\n" + 
        		"    {\n" + 
        		"      \"name\": \"A user story\",\n" + 
        		"      \"type\": \"story\"\n" + 
        		"    }\n" + 
        		"  ],\n" + 
        		"  \"test-steps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373542918414,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
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
	        	"  \"title\": \"Should do this [a qualifier]\",\n" + 
	        	"  \"name\": \"should_do_this\",\n" + 
	        	"  \"test-case\": {\n" + 
	        	"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
	        	"  },\n" + 
	        	"  \"result\": \"SUCCESS\",\n" + 
	        	"  \"qualifier\": \"a qualifier\",\n" + 
	        	"  \"steps\": \"1\",\n" + 
	        	"  \"successful\": \"1\",\n" + 
	        	"  \"failures\": \"0\",\n" + 
	        	"  \"skipped\": \"0\",\n" + 
	        	"  \"ignored\": \"0\",\n" + 
	        	"  \"pending\": \"0\",\n" + 
	        	"  \"duration\": \"0\",\n" + 
	        	"  \"timestamp\": \"2013-01-01T00:00:00.000+01:00\",\n" + 
	        	"  \"user-story\": {\n" + 
	        	"    \"userStoryClass\": {\n" + 
	        	"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AUserStory\"\n" + 
	        	"    },\n" + 
	        	"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory\",\n" + 
	        	"    \"storyName\": \"A user story\",\n" + 
	        	"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
	        	"  },\n" + 
	        	"  \"issues\": [],\n" + 
	        	"  \"tags\": [\n" + 
	        	"    {\n" + 
	        	"      \"name\": \"A user story\",\n" + 
	        	"      \"type\": \"story\"\n" + 
	        	"    }\n" + 
	        	"  ],\n" + 
	        	"  \"test-steps\": [\n" + 
	        	"    {\n" + 
	        	"      \"description\": \"step 1\",\n" + 
	        	"      \"duration\": 0,\n" + 
	        	"      \"startTime\": 1373543479702,\n" + 
	        	"      \"screenshots\": [],\n" + 
	        	"      \"result\": \"SUCCESS\",\n" + 
	        	"      \"children\": []\n" + 
	        	"    }\n" + 
	        	"  ]\n" + 
	        	"}"; 
        
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getQualifier().get(), is("a qualifier"));
        assertThat(testOutcome.get().getTitle(), containsString("[a qualifier]"));
    }  
}
