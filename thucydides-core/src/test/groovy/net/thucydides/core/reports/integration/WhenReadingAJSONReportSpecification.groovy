package net.thucydides.core.reports.integration

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
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.goldin.spock.extensions.tempdir.TempDir;
import com.google.common.base.Optional;

class WhenReadingAJSONReportSpecification extends spock.lang.Specification {

	def AcceptanceTestReporter outcomeReporter

	@TempDir File outputDirectory		
    	
	public void setup() throws IOException {
		outcomeReporter = new JSONTestOutcomeReporter();
		outcomeReporter.setOutputDirectory(outputDirectory);
	}

    @Test
    public void should_load_acceptance_test_report_from_json_file() throws Exception {
    	    	    	
    	 def expectedReport = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$ATestScenarioWithIssues",
			    "issues": [
			      "#123",
			      "#456",
			      "#789"
			    ]
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification"
			  },
			  "issues": [
			    "#456",
			    "#789",
			    "#123"
			  ],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1374810594394,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
		"""
    	
        def report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, expectedReport);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);        
		expect :
			testOutcome.get().getIssues().containsAll(["#123","#789","#456"]);
			testOutcome.get().getStartTime() != null;
    }
    
    @Test
    def "should_load_manual_acceptance_test_report_from_json_file"() throws Exception {
        String expectedReport = 
        	"""
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "manual": "true",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1374811596702,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""		                                     		
		def report = new File(outputDirectory,"saved-report.json");
		FileUtils.writeStringToFile(report, expectedReport);
		Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
		expect :
			testOutcome.get().isManual()	
    }

    @Test
    def "should_load_a_qualified_acceptance_test_report_from_json_file"() throws Exception {
        
        def storedReportJson = """
			{
			  "title": "Should do this [a qualifier]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "qualifier": "a qualifier",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373543479702,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""        		        
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);		
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
		expect :
        	testOutcome.get().getQualifier().get().equals("a qualifier")
			testOutcome.get().getTitle().indexOf("[a qualifier]") > -1
    } 
    
    @Test
    def "should unescape newline in the title and qualifier of a qualified acceptance test report from json file"() throws Exception {       
        String storedReportJson = """
			{
			  "title": "Should do this [a qualifier with \u0026#10; a new line]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "qualifier": "a qualifier with \u0026#10; a new line",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373544008557,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""        		
       
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);		
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
		expect : 
        	testOutcome.get().getQualifier().get().equals("a qualifier with \n a new line");
			testOutcome.get().getTitle().indexOf("[a qualifier with \n a new line]") > -1;
    }
    
    @Test
    def "should load tags from json file"() throws Exception {        
        String storedReportJson = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenarioWithTags"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenarioWithTags"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.SomeTestScenarioWithTags",
			    "storyName": "Some test scenario with tags",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "Some test scenario with tags",
			      "type": "story"
			    },
			    {
			      "name": "simple story",
			      "type": "story"
			    },
			    {
			      "name": "important feature",
			      "type": "feature"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373544217353,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""        		

        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
		expect : 
        	testOutcome.get().getTags().size() == 3        
    }
    
    @Test
    def "should load example data from json file"() throws Exception {
    	def storedReportJson = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373543300323,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ],
			  "examples": {
			    "headers": [
			      "firstName",
			      "lastName",
			      "age"
			    ],
			    "rows": [
			      {
			        "cellValues": [
			          "Joe",
			          "Smith",
			          "20"
			        ],
			        "result": "FAILURE"
			      },
			      {
			        "cellValues": [
			          "Jack",
			          "Jones",
			          "21"
			        ],
			        "result": "SUCCESS"
			      }
			    ],
			    "predefinedRows": true,
			    "currentRow": {
			      "value": 0
			    }
			  }
			}
			"""           		
        
        File report = new File(outputDirectory,"saved-report.json");		
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);        
        DataTable table = testOutcome.get().getDataTable();
		
		expect: 
        	table.getHeaders().containsAll("firstName","lastName","age")
			table.getRows().size() == 2
			table.getRows().get(0).getStringValues().containsAll("Joe","Smith","20")			
			table.getRows().get(0).getResult().equals(TestResult.FAILURE)
			table.getRows().get(1).getStringValues().containsAll("Jack","Jones","21")
			table.getRows().get(1).getResult().equals(TestResult.SUCCESS)
    }
    
    @Test
    def "should load acceptance test report including issues"() throws Exception {
        def storedReportJson = """
		{
		  "title": "Should do this",
		  "name": "should_do_this",
		  "test-case": {
		    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$ATestScenarioWithIssues",
		    "issues": [
		      "#123",
		      "#456",
		      "#789"
		    ]
		  },
		  "result": "SUCCESS",
		  "steps": "1",
		  "successful": "1",
		  "failures": "0",
		  "skipped": "0",
		  "ignored": "0",
		  "pending": "0",
		  "duration": "0",
		  "timestamp": "2013-01-01T00:00:00.000+01:00",
		  "user-story": {
		    "userStoryClass": {
		      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AUserStory"
		    },
		    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory",
		    "storyName": "A user story",
		    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
		  },
		  "issues": [
		    "#456",
		    "#789",
		    "#123"
		  ],
		  "tags": [
		    {
		      "name": "A user story",
		      "type": "story"
		    }
		  ],
		  "test-steps": [
		    {
		      "description": "step 1",
		      "duration": 0,
		      "startTime": 1373542631993,
		      "screenshots": [],
		      "result": "SUCCESS",
		      "children": []
		    }
		  ]
		}
		"""
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);  
		expect:      
        	testOutcome.get().getTitle().equals("Should do this")
        	testOutcome.get().getIssues().size() == 3                
    }
    
    @Test
    def "should load feature details from json file"() throws Exception {        
        String storedReportJson = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenarioInAFeature"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AFeature\$AUserStoryInAFeature"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature.AUserStoryInAFeature",
			    "storyName": "A user story in a feature",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature",
			    "qualifiedFeatureClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature",
			    "featureName": "A feature"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A feature",
			      "type": "feature"
			    },
			    {
			      "name": "A user story in a feature",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373572931641,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""        	
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        expect: 
        	testOutcome.get().getFeature().getId().equals("net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AFeature")
			testOutcome.get().getFeature().getName().equals("A feature");
    }
    
    @Test
    def "should_load_the_session_id_from_xml_file"() throws Exception {      
        def storedReportJson  = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "session-id": "1234",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$AUserStory"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport"
			  },
			  "issues": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "description": "step 1",
			      "duration": 0,
			      "startTime": 1373571216867,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""         	
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
		System.out.println(storedReportJson)
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        
        expect: 
			testOutcome.get().getSessionId().equals("1234")
    }
    
    /*@Test
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
    }*/

    /*@Test
    public void should_load_qualified_acceptance_test_report_with_a_qualified_name() throws Exception {
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
    }*/

}
