package net.thucydides.core.reports.integration

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import com.github.goldin.spock.extensions.tempdir.TempDir;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpock.ATestScenarioWithIssues;
import net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpock.SomeTestScenario;
import net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpock.SomeTestScenarioInAFeature;
import net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpock.SomeTestScenarioWithTags;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;

class WhenGeneratingAJSONReportSpecification extends spock.lang.Specification {

	def AcceptanceTestReporter reporter

	@TempDir File outputDirectory					
	
	TestOutcomes allTestOutcomes = Mock();
		
	public void setup() throws IOException {
		reporter = new JSONTestOutcomeReporter();		
		reporter.setOutputDirectory(outputDirectory);
	}
	
	class AUserStory {
	}

	@Story(AUserStory.class)
	class SomeTestScenario {
		public void a_simple_test_case() {
		}

		public void should_do_this() {
		}

		public void should_do_that() {
		}
	}
	
	@WithTag(name="important feature", type = "feature")
	class SomeTestScenarioWithTags {
		public void a_simple_test_case() {
		}

		@WithTag(name="simple story",type = "story")
		public void should_do_this() {
		}

		public void should_do_that() {
		}
	}

	@Feature
	class AFeature {
		class AUserStoryInAFeature {
		}
	}

	@Story(AFeature.AUserStoryInAFeature.class)
	class SomeTestScenarioInAFeature {
		public void should_do_this() {
		}

		public void should_do_that() {
		}
	}

	class ATestScenarioWithoutAStory {
		public void should_do_this() {
		}

		public void should_do_that() {
		}

		public void and_should_do_that() {
		}
	}

	@Story(AUserStory.class)
	@Issues(["#123", "#456"])
	class ATestScenarioWithIssues {
		public void a_simple_test_case() {
		}

		@Issue("#789")
		public void should_do_this() {
		}

		public void should_do_that() {
		}
	}


	
	def "should get tags from user story if present"() {
		
		def TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
		 			
		expect:					
			testOutcome.getTags().contains(TestTag.withName("A user story").andType("story"))			
	}
	
	
	def "should_get_tags_from_user_stories_and_features_if_present"() {
		def TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
		
		expect:						
			testOutcome.getTags().containsAll([
				TestTag.withName("A user story in a feature").andType("story"),
				TestTag.withName("A feature").andType("feature")
			]);
												
	}

	
	def "should get tags using tag annotations if present"() {		
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
		
		expect:
			testOutcome.getTags().containsAll([TestTag.withName("important feature").andType("feature"),
				TestTag.withName("simple story").andType("story")]);
	}

	
	def "should add a story tag based on the class name if nothing else is specified"() {
		def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithoutAStory.class);
		
		expect : 
			testOutcome.getTags().contains(TestTag.withName("A test scenario without a story").andType("story"));
	}

	
	def "should generate an JSON report for an acceptance test run"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		
		String expectedReport = """\
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
			      "startTime": 1373969264517,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
		""" 
			 
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"))
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def generatedReportText = getStringFrom(jsonReport)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))		
		
		expect : 
		   JSONCompare.compareJSON(expectedReport, generatedReportText,jsonCmp).passed();
	}
			
	
	def "should include issues in the JSON report"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		
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
			
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))		
		expect:
		   JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
 
	def "should generate an JSON report for a manual acceptance test run"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).asManualTest();
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
						
		String expectedReport = """
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
		
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))		
		expect:
		   JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
	@Test
	def "should generate an JSON report for an acceptance test run with a table"()
			throws Exception {

		def row1 = new ArrayList<Object>(); 
		row1.addAll(Lists.newArrayList("Joe", "Smith", "20"));
		def row2 = new ArrayList<Object>(); 
		row2.addAll(Lists.newArrayList("Jack", "Jones", "21"));

		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
		DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);

		def table = DataTable.withHeaders(ImmutableList.of("firstName","lastName","age")).
									andRows(ImmutableList.of(row1, row2)).build();
		testOutcome.useExamplesFrom(table);
		table.row(0).hasResult(TestResult.FAILURE);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		
		def expectedReport = """
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
			      "startTime": 1374906736965,
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
			        "result": "UNDEFINED"
			      }
			    ],
			    "predefinedRows": true,
			    "currentRow": {
			      "value": 0
			    }
			  }
			}
			"""				
		
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		   JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
	
	@Test
	def "should generate an JSON report for an acceptance test run with a qualifier"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier");
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		
		def expectedReport = """
			{
			  "title": "Should do this [a qualifier]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
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
			      "startTime": 1375025701593,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""					
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		   JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
	@Test
	def "should escape new lines in title and qualifier attributes"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier with \n a new line");
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));	
		def expectedReport = """
			{
			  "title": "Should do this [a qualifier with \u0026#10; a new line]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
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
			      "startTime": 1375026016537,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""
	    def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
	    def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
	    expect:
		  JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp).passed();								
	}
			

	@Test
	def "should store tags in the JSON reports"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		   
		def expectedJSonReport = """
		{
		  "title": "Should do this",
		  "name": "should_do_this",
		  "test-case": {
		    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenarioWithTags"
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
		      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenarioWithTags"
		    },
		    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.SomeTestScenarioWithTags",
		    "storyName": "Some test scenario with tags",
		    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification"
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
		      "startTime": 1375026277759,
		      "screenshots": [],
		      "result": "SUCCESS",
		      "children": []
		    }
		  ]
		}	
		"""
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		  JSONCompare.compareJSON(expectedJSonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
	@Test
	def "should include the session id if provided in the XML report"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.setSessionId("1234");
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		
		def expectedJSonReport = """
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
			  "session-id": "1234",
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
			      "startTime": 1375027063401,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
		"""
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		  JSONCompare.compareJSON(expectedJSonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
	@Test
	def "the xml report should contain the feature if provided"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		
		def expectedJSonReport = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenarioInAFeature"
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
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$AFeature\$AUserStoryInAFeature"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature.AUserStoryInAFeature",
			    "storyName": "A user story in a feature",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature",
			    "qualifiedFeatureClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature",
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
			      "startTime": 1375027443083,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}	
		"""
		
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))		
		expect:
		  JSONCompare.compareJSON(expectedJSonReport, getStringFrom(jsonReport),jsonCmp).passed();		
	}
			
			
	@Test
	def "the xml report should record features and stories as tags"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);		
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
	   
		def expectedJsonReport = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenarioInAFeature"
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
			      "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$AFeature\$AUserStoryInAFeature"
			    },
			    "qualifiedStoryClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature.AUserStoryInAFeature",
			    "storyName": "A user story in a feature",
			    "path": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature",
			    "qualifiedFeatureClassName": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification.AFeature",
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
			      "startTime": 1375068763901,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
		"""
					
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		  JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
	
	@Test
	def "should generate a qualified JSON report for an acceptance test run if the qualifier is specified"() throws Exception {
		def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		reporter.setQualifier("qualifier");
	   
		def expectedJsonReport = """
			{
			  "title": "A simple test case [qualifier]",
			  "name": "a_simple_test_case",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "qualifier": "qualifier",
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
			      "startTime": 1375069991988,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""				
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		  JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
	
	@Test
	def "should generate a qualified JSON report with formatted parameters if the qualifier is specified"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		reporter.setQualifier("a_b");
		
		def expectedJsonReport = """
			{
			  "title": "A simple test case [a_b]",
			  "name": "a_simple_test_case",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
			  },
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
			      "startTime": 1375070305312,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
		"""							
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator))
		expect:
		  JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}
			
	@Test
	def "should generate an JSON report with a name based on the test run title"()
			throws Exception {
		def testOutcome = new TestOutcome("a_simple_test_case");
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);		
		
		expect:		
		    jsonReport.getName().equals(Digest.ofTextValue("a_simple_test_case") + ".json");
	}

	@Test
	public void should_generate_an_JSON_report_in_the_target_directory() throws Exception {
		def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

		expect:		
		  jsonReport.getPath().startsWith(outputDirectory.getPath());
	}

	@Test
	def "should count the total number of steps with each outcome in acceptance test run"()
			throws Exception {
		def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
		def startTime = new DateTime(2013,1,1,0,0,0,0);
		testOutcome.setStartTime(startTime);
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
		testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
		testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
		testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
		testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
		testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
		testOutcome.recordStep(TestStepFactory.errorTestStepCalled("step 7"));
		testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
		testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

		def expectedJsonReport = """
			{
			  "title": "A simple test case",
			  "name": "a_simple_test_case",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.integration.WhenGeneratingAJSONReportSpecification\$SomeTestScenario"
			  },
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
			      "startTime": 1375070821042,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    },
			    {
			      "description": "step 2",
			      "duration": 0,
			      "startTime": 1375070821043,
			      "screenshots": [],
			      "result": "IGNORED",
			      "children": []
			    },
			    {
			      "description": "step 3",
			      "duration": 0,
			      "startTime": 1375070821043,
			      "screenshots": [],
			      "result": "IGNORED",
			      "children": []
			    },
			    {
			      "description": "step 4",
			      "duration": 0,
			      "startTime": 1375070821043,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    },
			    {
			      "description": "step 5",
			      "duration": 0,
			      "startTime": 1375070821045,
			      "screenshots": [],
			      "result": "FAILURE",
			      "children": []
			    },
			    {
			      "description": "step 6",
			      "duration": 0,
			      "startTime": 1375070821045,
			      "screenshots": [],
			      "result": "FAILURE",
			      "children": []
			    },
			    {
			      "description": "step 7",
			      "duration": 0,
			      "startTime": 1375070821045,
			      "screenshots": [],
			      "result": "ERROR",
			      "children": []
			    },
			    {
			      "description": "step 8",
			      "duration": 0,
			      "startTime": 1375070821046,
			      "screenshots": [],
			      "result": "SKIPPED",
			      "children": []
			    },
			    {
			      "description": "step 9",
			      "duration": 0,
			      "startTime": 1375070821046,
			      "screenshots": [],
			      "result": "PENDING",
			      "children": []
			    }
			  ]
			}
		"""
		def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
		System.out.println(getStringFrom(jsonReport));		
		def jsonCmp = new CustomComparator(JSONCompareMode.STRICT,
			new Customization("test-steps[0].startTime", comparator),
			new Customization("test-steps[1].startTime", comparator),
			new Customization("test-steps[2].startTime", comparator),
			new Customization("test-steps[3].startTime", comparator),
			new Customization("test-steps[4].startTime", comparator),
			new Customization("test-steps[5].startTime", comparator),
			new Customization("test-steps[6].startTime", comparator),
			new Customization("test-steps[7].startTime", comparator),
			new Customization("test-steps[8].startTime", comparator),
			new Customization("test-steps[9].startTime", comparator)
			)

		expect:
		  JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport),jsonCmp).passed();
	}

			
	def getStringFrom(File reportFile) throws IOException {
		return FileUtils.readFileToString(reportFile);
	}
	
	
	ValueMatcher<Object> comparator = new ValueMatcher<Object>() {
		@Override
		public boolean equal(Object o1, Object o2) {
			return true;
		}
	};

}
