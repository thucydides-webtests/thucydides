package net.thucydides.core.reports.integration;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class WhenGeneratingAnXMLReport {

    private AcceptanceTestReporter reporter;

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        reporter = new XMLTestOutcomeReporter();
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
    public void should_generate_an_XML_report_for_an_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_qualifier()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier");
        String expectedReport =
                "<acceptance-test-run title='Should do this [a qualifier]' name='should_do_this' qualifier='a qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }
    @Test
    public void should_store_tags_in_the_XML_reports()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_include_the_session_id_if_provided_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' session-id='1234'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_include_issues_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void the_xml_report_should_contain_the_feature_if_provided()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration'>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void the_xml_report_should_record_features_and_stories_as_tags()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration'>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Test
    public void should_generate_a_qualified_XML_report_for_an_acceptance_test_run_if_the_qualifier_is_specified() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [qualifier]' name='a_simple_test_case' qualifier='qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("qualifier");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_generate_a_qualified_XML_report_with_formatted_parameters_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A simple test case [a_b]' name='a_simple_test_case' qualifier='a_b' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1"));

        reporter.setQualifier("a_b");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }


    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getName(), is(DigestUtils.md5Hex("a_simple_test_case") + ".xml"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='3' skipped='1' ignored='2' pending='1' result='FAILURE' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 6</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 7</description>\n"
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
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 7"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
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
        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_record_minimal_nested_test_steps_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
    }

    @Test
    public void should_include_the_name_of_any_screenshots_where_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration'/>\n"
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
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");

        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport));
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        assertThat(xmlReport.getName(), is(DigestUtils.md5Hex("a_user_story_a_simple_test_case_qualifier") + ".xml"));

    }

    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
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

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<exception>java.lang.IllegalArgumentException"));
    }

    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
