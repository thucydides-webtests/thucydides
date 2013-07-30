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
    def "should load the session id from xml file"() throws Exception {      
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
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        
        expect: 
			testOutcome.get().getSessionId().equals("1234")
    }
    
    @Test
    def "should_return_null_feature_if_no_feature_is_present"() {
        TestOutcome testOutcome = new TestOutcome("aTestMethod")
		
        expect:
		 testOutcome.getFeature() == null
    }

    @Test
    def "should load acceptance test report with nested groups from xmlfile"() throws Exception {
        
        def storedReportJson = """
			{
			  "title": "A nested test case",
			  "name": "a_nested_test_case",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReport\$SomeNestedTestScenario"
			  },
			  "result": "SUCCESS",
			  "steps": "5",
			  "successful": "5",
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
			      "description": "Group 1",
			      "duration": 0,
			      "startTime": 1373625031524,
			      "screenshots": [],
			      "children": [
			        {
			          "description": "step 1",
			          "duration": 0,
			          "startTime": 1373625031524,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        },
			        {
			          "description": "step 2",
			          "duration": 0,
			          "startTime": 1373625031524,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        },
			        {
			          "description": "step 3",
			          "duration": 0,
			          "startTime": 1373625031524,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        },
			        {
			          "description": "Group 1.1",
			          "duration": 0,
			          "startTime": 1373625031524,
			          "screenshots": [],
			          "children": [
			            {
			              "description": "step 4",
			              "duration": 0,
			              "startTime": 1373625031530,
			              "screenshots": [],
			              "result": "SUCCESS",
			              "children": []
			            },
			            {
			              "description": "step 5",
			              "duration": 0,
			              "startTime": 1373625031530,
			              "screenshots": [],
			              "result": "SUCCESS",
			              "children": []
			            }
			          ]
			        }
			      ]
			    }
			  ]
			}
			""" 
        File report =  new File(outputDirectory,"saved-report.json");		
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
		
		expect : 
        	testOutcome.get().getTitle().equals("A nested test case")        
			testOutcome.get().getTestSteps().size() == 1
			group1.getChildren().size() == 4
			group1.getChildren().get(3).getChildren().size() == 2
        
    }

    @Test
    def "should load acceptance test report with simple nested groups from xml file"() throws Exception {        
    	
        def storedReportJson = """
			{
			  "title": "A nested test case",
			  "name": "a_nested_test_case",			  
			  "result": "SUCCESS",
			  "steps": "3",
			  "successful": "3",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {			    
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
			      "description": "Group 1",
			      "duration": 0,
			      "startTime": 1373624139680,
			      "screenshots": [],
			      "children": [
			        {
			          "description": "step 1",
			          "duration": 0,
			          "startTime": 1373624139681,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        },
			        {
			          "description": "step 2",
			          "duration": 0,
			          "startTime": 1373624139681,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        },
			        {
			          "description": "step 3",
			          "duration": 0,
			          "startTime": 1373624139681,
			          "screenshots": [],
			          "result": "SUCCESS",
			          "children": []
			        }
			      ]
			    }
			  ]
			}
		""" 
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
		expect : 
        	testOutcome.get().getTitle().equals("A nested test case")        
			testOutcome.get().getTestSteps().size() == 1
		    group1.getChildren().size() == 3
    }


    @Test
    def "should load acceptance test report with multiple test steps from xml file"() throws Exception {        
        
        def storedReportJson = """
		{
		  "title": "A simple test case",
		  "name": "a_simple_test_case",		  
		  "result": "FAILURE",
		  "steps": "9",
		  "successful": "2",
		  "failures": "2",
		  "errors": "1",
		  "skipped": "1",
		  "ignored": "2",
		  "pending": "1",
		  "duration": "0",
		  "timestamp": "2013-01-01T00:00:00.000+01:00",
		  "user-story": {		    
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
		      "startTime": 1373601456591,
		      "screenshots": [],
		      "result": "SUCCESS",
		      "children": []
		    },
		    {
		      "description": "step 2",
		      "duration": 0,
		      "startTime": 1373601456592,
		      "screenshots": [],
		      "result": "IGNORED",
		      "children": []
		    }
		  ]
		}
		""" 
        File report = new File(outputDirectory,"saved-report.json");
        FileUtils.writeStringToFile(report, storedReportJson);
        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
		
		expect :
        	testOutcome.get().getTitle().equals("A simple test case")
			testOutcome.get().getTestSteps().size() == 2
			testOutcome.get().getTestSteps().get(0).getResult().equals(TestResult.SUCCESS)
			testOutcome.get().getTestSteps().get(0).getDescription().equals("step 1")
			testOutcome.get().getTestSteps().get(1).getResult().equals(TestResult.IGNORED)
			testOutcome.get().getTestSteps().get(1).getDescription().equals("step 2")
    }

    @Test
    public void should_load_qualified_acceptance_test_report_with_a_qualified_name() throws Exception {
        def storedReportJson = """
			{
			  "title": "A simple test case [a_b]",
			  "name": "a_simple_test_case",
			  "result": "SUCCESS",
			  "qualifier": "a_b",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "2013-01-01T00:00:00.000+01:00",
			  "user-story": {			    
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
			      "startTime": 1373601008887,
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
        	testOutcome.get().getTitle().equals("A simple test case [a_b]")
        	testOutcome.get().getTitleWithLinks().equals("A simple test case [a_b]")
    }

}
