package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WhenGeneratingAJSONReport {

	public WhenGeneratingAJSONReport() {
		
	}
	
    private AcceptanceTestReporter reporter;

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private File outputDirectory;

    @Mock
    TestOutcomes allTestOutcomes;
    
    @Before
    public void setupTestReporter() throws IOException {
        
        MockitoAnnotations.initMocks(this);
        
        reporter = new JSONTestOutcomeReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
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
    @Issues({"#123", "#456"})
    class ATestScenarioWithIssues {
        public void a_simple_test_case() {
        }

        @Issue("#789")
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }


    @Test
    public void should_get_tags_from_user_story_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        assertThat(testOutcome.getTags(), hasItem(TestTag.withName("A user story").andType("story")));
    }


    @Test
    public void should_get_tags_from_user_stories_and_features_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        assertThat(testOutcome.getTags(), allOf(hasItem(TestTag.withName("A user story in a feature").andType("story")),
                                                hasItem(TestTag.withName("A feature").andType("feature"))));
    }

    @Test
    public void should_get_tags_using_tag_annotations_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        assertThat(testOutcome.getTags(), allOf(hasItem(TestTag.withName("important feature").andType("feature")),
                hasItem(TestTag.withName("simple story").andType("story"))));
    }

    @Test
    public void should_add_a_story_tag_based_on_the_class_name_if_nothing_else_is_specified() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithoutAStory.class);
        assertThat(testOutcome.getTags(), hasItem(TestTag.withName("A test scenario without a story").andType("story")));
    }


    @Test
    public void should_generate_an_JSON_report_for_an_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);             

        String expectedReport =  "{\n" + 
        		"  \"methodName\": \"should_do_this\",\n" + 
        		"  \"testCase\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"testSteps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373280828796,\n" + 
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
        		"  \"manualTest\": false,\n" + 
        		"  \"NO_HEADERS\": []\n" + 
        		"}\n" + 
        		"";
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(jsonReport);        
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("testSteps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, generatedReportText,jsonCmp);
        assertTrue(result.getMessage(), result.passed());
    }
              
    @Test
    public void should_include_issues_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);        
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
                     
        String expectedReport = "{\n" + 
        		"  \"methodName\": \"should_do_this\",\n" + 
        		"  \"testCase\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$ATestScenarioWithIssues\",\n" + 
        		"    \"issues\": [\n" + 
        		"      \"#123\",\n" + 
        		"      \"#456\",\n" + 
        		"      \"#789\"\n" + 
        		"    ]\n" + 
        		"  },\n" + 
        		"  \"testSteps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373280737831,\n" + 
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
        		"  \"manualTest\": false,\n" + 
        		"  \"NO_HEADERS\": []\n" + 
        		"}";
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("testSteps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport),jsonCmp);
        assertTrue(result.getMessage(), result.passed());
    }


    @Test
    public void should_generate_an_JSON_report_for_a_manual_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).asManualTest();
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
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

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("testSteps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }

     @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_table()
            throws Exception {

        List<Object> row1 = new ArrayList<Object>(); row1.addAll(Lists.newArrayList("Joe", "Smith", "20"));
        List<Object> row2 = new ArrayList<Object>(); row2.addAll(Lists.newArrayList("Jack", "Jones", "21"));

        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        DataTable table = DataTable.withHeaders(ImmutableList.of("firstName","lastName","age")).
                                    andRows(ImmutableList.of(row1, row2)).build();
        testOutcome.useExamplesFrom(table);
        table.row(0).hasResult(TestResult.FAILURE);
        
        String expectedReport = 
        		"{\n" + 
        		"  \"methodName\": \"should_do_this\",\n" + 
        		"  \"testCase\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"testSteps\": [\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 1\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373281688189,\n" + 
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
        		"  \"dataTable\": {\n" + 
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
        		"        \"result\": \"UNDEFINED\"\n" + 
        		"      }\n" + 
        		"    ],\n" + 
        		"    \"predefinedRows\": true,\n" + 
        		"    \"currentRow\": {\n" + 
        		"      \"value\": 0\n" + 
        		"    }\n" + 
        		"  },\n" + 
        		"  \"manualTest\": false,\n" + 
        		"  \"NO_HEADERS\": []\n" + 
        		"}";
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));      
        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);                   
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("testSteps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_qualifier()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        
        String expectedReport = 
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
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("testSteps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }


    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }
    
    
    ValueMatcher<Object> comparator = new ValueMatcher<Object>() {
        @Override
        public boolean equal(Object o1, Object o2) {
        	return true;
        }
    };



}
