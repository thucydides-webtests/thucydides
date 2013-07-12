package net.thucydides.core.reports.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

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
import net.thucydides.core.digest.Digest;
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

   
        String expectedReport = 
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
        		"      \"startTime\": 1373542551877,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}"; 
             
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(jsonReport);        
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, generatedReportText,jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }
              
    @Test
    public void should_include_issues_in_the_JSON_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);        
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
                             
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
        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
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
                
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void should_generate_an_JSON_report_for_an_acceptance_test_run_with_a_table()
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
        		"        \"result\": \"UNDEFINED\"\n" + 
        		"      }\n" + 
        		"    ],\n" + 
        		"    \"predefinedRows\": true,\n" + 
        		"    \"currentRow\": {\n" + 
        		"      \"value\": 0\n" + 
        		"    }\n" + 
        		"  }\n" + 
        		"}";
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));      
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);                   
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);              
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void should_generate_an_JSON_report_for_an_acceptance_test_run_with_a_qualifier()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        
        String expectedReport = 
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
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }
    
    @Test
    public void should_escape_new_lines_in_title_and_qualifier_attributes()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier with \n a new line");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        
        String expectedReport =
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
        
       
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));
        JSONCompareResult result = JSONCompare.compareJSON(expectedReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }


    @Test
    public void should_store_tags_in_the_XML_reports()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
           
        String expectedJSonReport = 
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

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);           
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJSonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void should_include_the_session_id_if_provided_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        
        String expectedJsonReport =  
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
        		

        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);
        
        assertTrue(result.getMessage(), result.passed());
    }

    

    @Test
    public void the_xml_report_should_contain_the_feature_if_provided()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        
        String expectedJsonReport =
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
        	
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        System.out.println(getStringFrom(jsonReport));
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }


    @Test
    public void the_xml_report_should_record_features_and_stories_as_tags()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
       
        String expectedJsonReport = 
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
        		"      \"startTime\": 1373573723398,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}\n" + 
        		"";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        System.out.println(getStringFrom(jsonReport));
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }

    
    @Test
    public void should_generate_a_qualified_XML_report_for_an_acceptance_test_run_if_the_qualifier_is_specified() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
       
        String expectedJsonReport = 
        		"{\n" + 
        		"  \"title\": \"A simple test case [qualifier]\",\n" + 
        		"  \"name\": \"a_simple_test_case\",\n" + 
        		"  \"test-case\": {\n" + 
        		"    \"classname\": \"net.thucydides.core.reports.integration.WhenGeneratingAJSONReport$SomeTestScenario\"\n" + 
        		"  },\n" + 
        		"  \"result\": \"SUCCESS\",\n" + 
        		"  \"qualifier\": \"qualifier\",\n" + 
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
        		"      \"startTime\": 1373574203456,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}\n" + 
        		"";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("qualifier");
        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        System.out.println(getStringFrom(jsonReport));
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());    }

    @Test
    public void should_generate_a_qualified_JSON_report_with_formatted_parameters_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [a_b]' name='a_simple_test_case' qualifier='a_b' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        
        String expectedJsonReport 
        	= "{\n" + 
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

        reporter.setQualifier("a_b");
        
        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        System.out.println(getStringFrom(jsonReport));
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("test-steps[0].startTime", comparator));       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }


    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        assertThat(xmlReport.getName(), is(Digest.ofTextValue("a_simple_test_case") + ".json"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='2' errors='1' skipped='1' ignored='2' pending='1' result='FAILURE' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 3</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 4</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 5</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 6</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='ERROR' duration='0'>\n"
                        + "    <description>step 7</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SKIPPED' duration='0'>\n"
                        + "    <description>step 8</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='PENDING' duration='0'>\n"
                        + "    <description>step 9</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";
        String expectedJsonReport = 
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
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 3\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"IGNORED\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 4\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SUCCESS\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 5\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"FAILURE\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 6\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"FAILURE\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 7\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"ERROR\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 8\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"SKIPPED\",\n" + 
        		"      \"children\": []\n" + 
        		"    },\n" + 
        		"    {\n" + 
        		"      \"description\": \"step 9\",\n" + 
        		"      \"duration\": 0,\n" + 
        		"      \"startTime\": 1373601456592,\n" + 
        		"      \"screenshots\": [],\n" + 
        		"      \"result\": \"PENDING\",\n" + 
        		"      \"children\": []\n" + 
        		"    }\n" + 
        		"  ]\n" + 
        		"}";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
        testOutcome.recordStep(TestStepFactory.errorTestStepCalled("step 7"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        System.out.println(getStringFrom(jsonReport));
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, 
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
        											  );       
        JSONCompareResult result = JSONCompare.compareJSON(expectedJsonReport, getStringFrom(jsonReport), jsonCmp);        
        assertTrue(result.getMessage(), result.passed());
    }


    @Story(AUserStory.class)
    class SomeNestedTestScenario {
        public void a_nested_test_case() {
        }

        ;

        public void should_do_this() {
        }

        ;

        public void should_do_that() {
        }

        ;
    }

    /*@Test
    public void should_record_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 4</description>\n"
                        + "      </test-step>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 5</description>\n"
                        + "      </test-step>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.startGroup("Group 1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_steps_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_the_name_of_any_screenshots_where_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <screenshots>\n"
                        + "      <screenshot image='step_1.png' source='step_1.html'/>\n"
                        + "    </screenshots>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "    <error>Unspecified failure</error>"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_have_a_qualified_filename_if_qualifier_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        assertThat(xmlReport.getName(), is(Digest.ofTextValue("a_user_story_a_simple_test_case_qualifier") + ".xml"));

    }
    
    /*@Test
    public void should_escape_new_lines_in_title_and_qualifier_attributes()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier with \n a new line");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this [a qualifier with &amp;#10;" +
                        " a new line]' name='should_do_this' qualifier='a qualifier with &amp;#10;" +
                        " a new line' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_store_tags_in_the_XML_reports()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='Some test scenario with tags' type='story'/>\n"
                        + "    <tag name='simple story' type='story' />\n"
                        + "    <tag name='important feature' type='feature' />\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_the_session_id_if_provided_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'   timestamp='2013-01-01T00:00:00.000-05:00' session-id='1234'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_issues_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'  timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <issues>\n"
                        + "    <issue>#456</issue>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "  </issues>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport, "timestamp"));
    }

    @Test
    public void the_xml_report_should_contain_the_feature_if_provided()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature'>\n"
                        + "    <feature id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature' name='A feature'/>\n"
                        + "  </user-story>\n"
                        + "  <tags>\n"
                        + "    <tag name='A feature' type='feature'/>\n"
                        + "    <tag name='A user story in a feature' type='story'/>\n"
                        + "  </tags>"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void the_xml_report_should_record_features_and_stories_as_tags()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature'>\n"
                        + "    <feature id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature' name='A feature'/>\n"
                        + "  </user-story>\n"
                        + "  <tags>\n"
                        + "    <tag name='A feature' type='feature' />\n"
                        + "    <tag name='A user story in a feature' type='story' />\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_generate_a_qualified_XML_report_for_an_acceptance_test_run_if_the_qualifier_is_specified() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [qualifier]' name='a_simple_test_case' qualifier='qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("qualifier");
        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_a_qualified_XML_report_with_formatted_parameters_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [a_b]' name='a_simple_test_case' qualifier='a_b' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("a_b");
        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        assertThat(xmlReport.getName(), is(Digest.ofTextValue("a_simple_test_case") + ".xml"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='2' errors='1' skipped='1' ignored='2' pending='1' result='FAILURE' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 3</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 4</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 5</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 6</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='ERROR' duration='0'>\n"
                        + "    <description>step 7</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SKIPPED' duration='0'>\n"
                        + "    <description>step 8</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='PENDING' duration='0'>\n"
                        + "    <description>step 9</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
        testOutcome.recordStep(TestStepFactory.errorTestStepCalled("step 7"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Story(AUserStory.class)
    class SomeNestedTestScenario {
        public void a_nested_test_case() {
        }

        ;

        public void should_do_this() {
        }

        ;

        public void should_do_that() {
        }

        ;
    }

    @Test
    public void should_record_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 4</description>\n"
                        + "      </test-step>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 5</description>\n"
                        + "      </test-step>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3"));
        testOutcome.startGroup("Group 1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_steps_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_the_name_of_any_screenshots_where_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <screenshots>\n"
                        + "      <screenshot image='step_1.png' source='step_1.html'/>\n"
                        + "    </screenshots>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "    <error>Unspecified failure</error>"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_have_a_qualified_filename_if_qualifier_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);

        reporter.setQualifier("qualifier");

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        assertThat(xmlReport.getName(), is(Digest.ofTextValue("a_user_story_a_simple_test_case_qualifier") + ".xml"));

    }

    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<error>Oh nose!</error>"));
    }

    @Test
    public void should_include_exception_stack_dump_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<exception>java.lang.IllegalArgumentException"));
    }


    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<error>Oh nose!</error>"));
    }

    @Test
    public void should_include_exception_stack_dump_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<exception>java.lang.IllegalArgumentException"));
    }*/



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
