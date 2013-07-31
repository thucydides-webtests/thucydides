package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.features.ApplicationFeature;
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
    
    @Test
    public void should_unescape_newline_in_the_title_and_qualifier_of_a_qualified_acceptance_test_report_from_json_file() throws Exception {       
        String storedReportJson =
        		"{\n" + 
               "  \"title\": \"Should do this [a qualifier with \\u0026#10; a new line]\",\n" + 
               "  \"name\": \"should_do_this\",\n" + 
               "  \"test-case\": {\n" + 
               "    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
               "  },\n" + 
               "  \"result\": \"SUCCESS\",\n" + 
               "  \"qualifier\": \"a qualifier with \\u0026#10; a new line\",\n" + 
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
               "      \"startTime\": 1373544008557,\n" + 
               "      \"screenshots\": [],\n" + 
               "      \"result\": \"SUCCESS\",\n" + 
               "      \"children\": []\n" + 
               "    }\n" + 
               "  ]\n" + 
               "}";
       
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getQualifier().get(), is("a qualifier with \n a new line"));
        assertThat(testOutcome.get().getTitle(), containsString("[a qualifier with \n a new line]"));
    }
    
    @Test
    public void should_load_tags_from_json_file() throws Exception {        
        String storedReportJson =
        		"{\n" + 
    			"  \"title\": \"Should do this\",\n" + 
    			"  \"name\": \"should_do_this\",\n" + 
    			"  \"test-case\": {\n" + 
    			"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenarioWithTags\"\n" + 
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
    			"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenarioWithTags\"\n" + 
    			"    },\n" + 
    			"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.SomeTestScenarioWithTags\",\n" + 
    			"    \"storyName\": \"Some test scenario with tags\",\n" + 
    			"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\"\n" + 
    			"  },\n" + 
    			"  \"issues\": [],\n" + 
    			"  \"tags\": [\n" + 
    			"    {\n" + 
    			"      \"name\": \"Some test scenario with tags\",\n" + 
    			"      \"type\": \"story\"\n" + 
    			"    },\n" + 
    			"    {\n" + 
    			"      \"name\": \"simple story\",\n" + 
    			"      \"type\": \"story\"\n" + 
    			"    },\n" + 
    			"    {\n" + 
    			"      \"name\": \"important feature\",\n" + 
    			"      \"type\": \"feature\"\n" + 
    			"    }\n" + 
    			"  ],\n" + 
    			"  \"test-steps\": [\n" + 
    			"    {\n" + 
    			"      \"description\": \"step 1\",\n" + 
    			"      \"duration\": 0,\n" + 
    			"      \"startTime\": 1373544217353,\n" + 
    			"      \"screenshots\": [],\n" + 
    			"      \"result\": \"SUCCESS\",\n" + 
    			"      \"children\": []\n" + 
    			"    }\n" + 
    			"  ]\n" + 
    			"}\n" + 
    			""; 

        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTags().size(), is(3));        
    }
    
    @Test
    public void should_load_example_data_from_json_file() throws Exception {
        /*String storedReportJson =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' />\n"
                        + "  <tags>\n"
                        + "    <tag name='important feature' type='feature' />\n"
                        + "    <tag name='simple story' type='story' />\n"
                        + "  </tags>\n"
                        + "  <examples>\n"
                        + "    <headers>\n"
                        + "      <header>firstName</header>\n"
                        + "      <header>lastName</header>\n"
                        + "      <header>age</header>\n"
                        + "    </headers>\n"
                        + "    <rows>\n"
                        + "      <row result='FAILURE'>\n"
                        + "        <value>Joe</value>\n"
                        + "        <value>Smith</value>\n"
                        + "        <value>20</value>\n"
                        + "      </row>\n"
                        + "      <row>\n"
                        + "        <value>Jack</value>\n"
                        + "        <value>Jones</value>\n"
                        + "        <value>21</value>\n"
                        + "      </row>\n"
                        + "    </rows>\n"
                        + "  </examples>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";*/
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
        		"      \"startTime\": 1373543300323,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ],\n" + 
        		"  \"examples\": {\n" + 
        		"    \"headers\": [\n" + 
        		"      \"firstName\",\n" + 
        		"      \"lastName\",\n" + 
        		"      \"age\"\n" + 
        		"    ],\n" + 
        		"    \"rows\": [\n" + 
        		"      {\n" + 
        		"        \"cellValues\": [\n" + 
        		"          \"Joe\",\n" + 
        		"          \"Smith\",\n" + 
        		"          \"20\"\n" + 
        		"        ],\n" + 
        		"        \"result\": \"FAILURE\"\n" + 
        		"      },\n" + 
        		"      {\n" + 
        		"        \"cellValues\": [\n" + 
        		"          \"Jack\",\n" + 
        		"          \"Jones\",\n" + 
        		"          \"21\"\n" + 
        		"        ],\n" + 
        		"        \"result\": \"SUCCESS\"\n" + 
        		"      }\n" + 
        		"    ],\n" + 
        		"    \"predefinedRows\": true,\n" + 
        		"    \"currentRow\": {\n" + 
        		"      \"value\": 0\n" + 
        		"    }\n" + 
        		"  }\n" + 
        		"}";
        
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        
        DataTable table = testOutcome.get().getDataTable();
        assertThat(table.getHeaders(), hasItems("firstName","lastName","age"));
        assertThat(table.getRows().size(), is(2));
        assertThat(table.getRows().get(0).getStringValues(), hasItems("Joe","Smith","20"));
        assertThat(table.getRows().get(0).getResult(), is(TestResult.FAILURE));
        assertThat(table.getRows().get(1).getStringValues(), hasItems("Jack","Jones","21"));
        assertThat(table.getRows().get(1).getResult(), is(TestResult.SUCCESS));
    }
    
    @Test
    public void should_load_acceptance_test_report_including_issues() throws Exception {
        String storedReportJson =
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
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);        
        assertThat(testOutcome.get().getTitle(), is("Should do this"));
        assertThat(testOutcome.get().getIssues().size(), is(3));                
    }
    
    @Test
    public void should_load_feature_details_from_json_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
          + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
          + "  </user-story>"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";
        String storedReportJson = 
        		"{\n" + 
        		"  \"title\": \"Should do this\",\n" + 
        		"  \"name\": \"should_do_this\",\n" + 
        		"  \"test-case\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenarioInAFeature\"\n" + 
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
        		"      \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$AFeature$AUserStoryInAFeature\"\n" + 
        		"    },\n" + 
        		"    \"qualifiedStoryClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature.AUserStoryInAFeature\",\n" + 
        		"    \"storyName\": \"A user story in a feature\",\n" + 
        		"    \"path\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature\",\n" + 
        		"    \"qualifiedFeatureClassName\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature\",\n" + 
        		"    \"featureName\": \"A feature\"\n" + 
        		"  },\n" + 
        		"  \"issues\": [],\n" + 
        		"  \"tags\": [\n" + 
        		"    {\n" + 
        		"      \"name\": \"A feature\",\n" + 
        		"      \"type\": \"feature\"\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"name\": \"A user story in a feature\",\n" + 
        		"      \"type\": \"story\"\n" + 
        		"    }\n" + 
        		"  ],\n" + 
        		"  \"test-steps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373572931641,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}\n" + 
        		"";
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        

        assertThat(testOutcome.get().getFeature().getId(), is("net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature"));
        assertThat(testOutcome.get().getFeature().getName(), is("A feature"));
    }
    
    @Test
    public void should_load_the_session_id_from_xml_file() throws Exception {
        String storedReportXML =
                  "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' session-id='1234'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
                + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
                + "  </user-story>"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        String storedReportJson  = 
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
	        	"  \"session-id\": \"1234\",\n" + 
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
	        	"      \"startTime\": 1373571216867,\n" + 
	        	"      \"screenshots\": [],\n" + 
	        	"      \"result\": \"SUCCESS\",\n" + 
	        	"      \"children\": []\n" + 
	        	"    }\n" + 
	        	"  ]\n" + 
	        	"}";	
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        
        assertThat(testOutcome.get().getSessionId(), is("1234"));
    }
    
    @Test
    public void should_return_null_feature_if_no_feature_is_present() {
        TestOutcome testOutcome = new TestOutcome("aTestMethod");
        assertThat(testOutcome.getFeature(), is(nullValue()));
    }

    @Test
    public void should_load_acceptance_test_report_with_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
              "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
            + "        <test-step result='SUCCESS'>\n"
            + "          <description>step 1</description>\n"
            + "        </test-step>\n"
            + "      </test-group>\n" 
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";
        
        String storedReportJson = 
        		"{\n" + 
        		"  \"title\": \"A nested test case\",\n" + 
        		"  \"name\": \"a_nested_test_case\",\n" + 
        		"  \"test-case\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeNestedTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"result\": \"SUCCESS\",\n" + 
        		"  \"steps\": \"5\",\n" + 
        		"  \"successful\": \"5\",\n" + 
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
        		"      \"description\": \"Group 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373625031524,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"children\": [\n" + 
        		"        {\n" + 
        		"          \"description\": \"step 1\",\n" + 
        		"          \"duration\": 0,\n" + 
        		"          \"startTime\": 1373625031524,\n" + 
        		"          \"screenshots\": [],\n" + 
        		"          \"result\": \"SUCCESS\",\n" + 
        		"          \"children\": []\n" + 
        		"        },\n" + 
        		"        {\n" + 
        		"          \"description\": \"step 2\",\n" + 
        		"          \"duration\": 0,\n" + 
        		"          \"startTime\": 1373625031524,\n" + 
        		"          \"screenshots\": [],\n" + 
        		"          \"result\": \"SUCCESS\",\n" + 
        		"          \"children\": []\n" + 
        		"        },\n" + 
        		"        {\n" + 
        		"          \"description\": \"step 3\",\n" + 
        		"          \"duration\": 0,\n" + 
        		"          \"startTime\": 1373625031524,\n" + 
        		"          \"screenshots\": [],\n" + 
        		"          \"result\": \"SUCCESS\",\n" + 
        		"          \"children\": []\n" + 
        		"        },\n" + 
        		"        {\n" + 
        		"          \"description\": \"Group 1.1\",\n" + 
        		"          \"duration\": 0,\n" + 
        		"          \"startTime\": 1373625031524,\n" + 
        		"          \"screenshots\": [],\n" + 
        		"          \"children\": [\n" + 
        		"            {\n" + 
        		"              \"description\": \"step 4\",\n" + 
        		"              \"duration\": 0,\n" + 
        		"              \"startTime\": 1373625031530,\n" + 
        		"              \"screenshots\": [],\n" + 
        		"              \"result\": \"SUCCESS\",\n" + 
        		"              \"children\": []\n" + 
        		"            },\n" + 
        		"            {\n" + 
        		"              \"description\": \"step 5\",\n" + 
        		"              \"duration\": 0,\n" + 
        		"              \"startTime\": 1373625031530,\n" + 
        		"              \"screenshots\": [],\n" + 
        		"              \"result\": \"SUCCESS\",\n" + 
        		"              \"children\": []\n" + 
        		"            }\n" + 
        		"          ]\n" + 
        		"        }\n" + 
        		"      ]\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}";
	

        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
        assertThat(group1.getChildren().size(), is(4));
        assertThat(group1.getChildren().get(3).getChildren().size(), is(2));
        
    }

    @Test
    public void should_load_acceptance_test_report_with_simple_nested_groups_from_xml_file() throws Exception {        
    	
        String storedReportJson = 
        	    "{\n" + 
				"  \"title\": \"A nested test case\",\n" + 
				"  \"name\": \"a_nested_test_case\",\n" + 
				"  \"test-case\": {\n" + 
				"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeNestedTestScenario\"\n" + 
				"  },\n" + 
				"  \"result\": \"SUCCESS\",\n" + 
				"  \"steps\": \"3\",\n" + 
				"  \"successful\": \"3\",\n" + 
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
				"      \"description\": \"Group 1\",\n" + 
				"      \"duration\": 0,\n" + 
				"      \"startTime\": 1373624139680,\n" + 
				"      \"screenshots\": [],\n" + 
				"      \"children\": [\n" + 
				"        {\n" + 
				"          \"description\": \"step 1\",\n" + 
				"          \"duration\": 0,\n" + 
				"          \"startTime\": 1373624139681,\n" + 
				"          \"screenshots\": [],\n" + 
				"          \"result\": \"SUCCESS\",\n" + 
				"          \"children\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"description\": \"step 2\",\n" + 
				"          \"duration\": 0,\n" + 
				"          \"startTime\": 1373624139681,\n" + 
				"          \"screenshots\": [],\n" + 
				"          \"result\": \"SUCCESS\",\n" + 
				"          \"children\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"description\": \"step 3\",\n" + 
				"          \"duration\": 0,\n" + 
				"          \"startTime\": 1373624139681,\n" + 
				"          \"screenshots\": [],\n" + 
				"          \"result\": \"SUCCESS\",\n" + 
				"          \"children\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}";
        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
        assertThat(group1.getChildren().size(), is(3));
    }


    @Test
    public void should_load_acceptance_test_report_with_multiple_test_steps_from_xml_file() throws Exception {        
        
        String storedReportJson = 
        		"{\n" + 
        		"  \"title\": \"A simple test case\",\n" + 
        		"  \"name\": \"a_simple_test_case\",\n" + 
        		"  \"test-case\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"result\": \"FAILURE\",\n" + 
        		"  \"steps\": \"9\",\n" + 
        		"  \"successful\": \"2\",\n" + 
        		"  \"failures\": \"2\",\n" + 
        		"  \"errors\": \"1\",\n" + 
        		"  \"skipped\": \"1\",\n" + 
        		"  \"ignored\": \"2\",\n" + 
        		"  \"pending\": \"1\",\n" + 
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
        		"      \"startTime\": 1373601456591,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 2\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"IGNORED\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" +         		        		         		
        		"  ]\n" + 
        		"}";

        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A simple test case"));
        assertThat(testOutcome.get().getTestSteps().size(), is(2));
        assertThat(testOutcome.get().getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.get().getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testOutcome.get().getTestSteps().get(1).getResult(), is(TestResult.IGNORED));
        assertThat(testOutcome.get().getTestSteps().get(1).getDescription(), is("step 2"));
    }

    @Test
    public void should_load_qualified_acceptance_test_report_with_a_qualified_name() throws Exception {
        /*String storedReportXML =
                "<acceptance-test-run title='Search for news [euro]' name='searchForNews[0]' qualifier='euro' steps='0' successful='0' failures='0' skipped='0' ignored='0' pending='0' result='PENDING' duration='85'>\n"+
                        "  <user-story id='samples.ParametrizedTest' name='Parametrized test'/>\n"+
                        "  <tags>\n"+
                        "    <tag name='Parametrized test' type='story'/>\n"+
                        "  </tags>\n"+
                        "</acceptance-test-run>";*/
        String storedReportJson = 
            	"{\n" + 
    			"  \"title\": \"A simple test case [a_b]\",\n" + 
    			"  \"name\": \"a_simple_test_case\",\n" + 
    			"  \"test-case\": {\n" + 
    			"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
    			"  },\n" + 
    			"  \"result\": \"SUCCESS\",\n" + 
    			"  \"qualifier\": \"a_b\",\n" + 
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
    			"      \"startTime\": 1373601008887,\n" + 
    			"      \"screenshots\": [],\n" + 
    			"      \"result\": \"SUCCESS\",\n" + 
    			"      \"children\": []\n" + 
    			"    }\n" + 
    			"  ]\n" + 
    			"}";


        File report = temporaryDirectory.newFile("saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A simple test case [a_b]"));
        assertThat(testOutcome.get().getTitleWithLinks(), is("A simple test case [a_b]"));
    }
}
