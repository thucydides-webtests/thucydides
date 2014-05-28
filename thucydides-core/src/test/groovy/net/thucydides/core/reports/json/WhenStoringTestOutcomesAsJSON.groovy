package net.thucydides.core.reports.json

import com.github.goldin.spock.extensions.tempdir.TempDir
import com.sun.xml.internal.bind.v2.TODO
import net.thucydides.core.annotations.*
import net.thucydides.core.digest.Digest
import net.thucydides.core.model.DataTable
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import net.thucydides.core.model.TestTag
import net.thucydides.core.model.features.ApplicationFeature
import net.thucydides.core.reports.AcceptanceTestLoader
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import net.thucydides.core.util.MockEnvironmentVariables
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringTestOutcomesAsJSON extends Specification {

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()
    private static final DateTime SECOND_OF_JANUARY = new LocalDateTime(2013, 1, 2, 0, 0, 0, 0).toDateTime()

    def AcceptanceTestReporter reporter
    def AcceptanceTestLoader loader

    @TempDir
    File outputDirectory

    TestOutcomes allTestOutcomes = Mock();

    def setup() {
        reporter = new JSONTestOutcomeReporter();
        loader = new JSONTestOutcomeReporter();
        reporter.setOutputDirectory(outputDirectory);
        loader.setOutputDirectory(outputDirectory);
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

    @Issue("PROJ-123")
    @WithTag(name = "important feature", type = "feature")
    class SomeTestScenarioWithTags {
        public void a_simple_test_case() {
        }

        @WithTag(name = "simple story", type = "story")
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

    @Story(AUserStory.class)
    class SomeNestedTestScenario {
        public void a_nested_test_case() {
        };

        public void should_do_this() {
        };

        public void should_do_that() {
        };
    }

    def "should generate an JSON report for an acceptance test run"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        and:
        def expectedReport = """\
                {
  "name" : "should_do_this",
  "testCase" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario",
  "testSteps" : [ {
    "number" : 1,
    "description" : "step 1",
    "duration" : 0,
    "result" : "SUCCESS",
    "startTime" : ${FIRST_OF_JANUARY.millis}
  } ],
  "userStory" : {
    "id" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
  },
  "title" : "Should do this",
  "description" : "Some description",
  "tags" : [ {
    "name" : "A user story",
    "type" : "story"
  } ],
  "startTime" : ${FIRST_OF_JANUARY.millis},
  "duration" : 0,
  "result" : "SUCCESS",
  "manual" : false,
  }
            """
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }


    def "should generate a minimized JSON report by default"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        !jsonReport.text.contains("  ")
    }


    def "should generate pretty JSON report if configured"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))

        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("json.pretty.printing","true")
        when:
        reporter.jsonConverter = new JacksonJSONConverter(environmentVariables)
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        jsonReport.text.contains("  ")
    }

    def "should include the project and batch start time in the JSON report if specified."() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))

        testOutcome = testOutcome.forProject("Some Project").inTestRunTimestamped(SECOND_OF_JANUARY)

        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.startTime == testOutcome.startTime
        reloadedOutcome.description == testOutcome.description
        reloadedOutcome.testSteps.size() == 1
    }

    def "should record screenshot details"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY).addScreenshot(new ScreenshotAndHtmlSource(new File("screenshot1.png"))))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        !reloadedOutcome.testSteps.get(0).getScreenshots().isEmpty()
    }

    def "should generate an JSON report for an acceptance test run without a test class"() {
        given:
        def testOutcome = TestOutcome.forTestInStory("should_do_this",
                net.thucydides.core.model.Story.withId("id", "name"))
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.userStory.id == "id"
        reloadedOutcome.userStory.name == "name"

    }


    def "should include issues in the JSON report"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.issues as Set == ["#123", "#456", "#789"] as Set
    }


    def "should generate an JSON report for a manual acceptance test run"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).asManualTest();
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.isManual()
    }


    def "should store annotated tags and issues in the JSON reports"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.issues == ["PROJ-123"]
        and:
        reloadedOutcome.tags.containsAll([TestTag.withName("Some test scenario with tags").andType("story"),
                                          TestTag.withName("simple story").andType("story"),
                                          TestTag.withName("important feature").andType("feature")])
    }


    def "should include the session id if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.sessionId == "1234"
    }


    def "should include a data table if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.useExamplesFrom(DataTable.withHeaders(["a","b","c"]).build())
        testOutcome.addRow(["a":"1", "b":"2", "c":"3"]);
        testOutcome.addRow(["a":"2", "b":"3", "c":"4"]);
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.dataTable.headers == ["a","b","c"]
        reloadedOutcome.dataTable.rows[0].stringValues == ["1","2","3"]
        reloadedOutcome.dataTable.rows[1].stringValues == ["2","3","4"]
    }


    def "should contain the feature if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.feature.name == "A feature"
    }


    def "should record features and stories as tags"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.tags.contains(TestTag.withName("A user story in a feature").andType("story"))
        reloadedOutcome.tags.contains(TestTag.withName("A feature").andType("feature"))
    }

    def "should generate an JSON report with a name based on the test run title"() {
        when:
        def testOutcome = new TestOutcome("a_simple_test_case");
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        then:
        jsonReport.name == Digest.ofTextValue("a_simple_test_case") + ".json";
    }

    def "should generate a JSON report in the target directory"() {
        when:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);

        then:
        jsonReport.path.startsWith(outputDirectory.path);
    }

    def "should have a qualified filename if qualifier present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        def step = TestStepFactory.successfulTestStepCalled("step 1");
        testOutcome.recordStep(step);
        when:
        reporter.setQualifier("qualifier");
        def report = reporter.generateReportFor(testOutcome, allTestOutcomes);
        then:
        report.name == Digest.ofTextValue("net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON/a_user_story_a_simple_test_case_qualifier") + ".json";
    }

    def "should include error message for failing test"() {

        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        def step = TestStepFactory.failingTestStepCalled("step 1")
        and:
        step.failedWith(new IllegalArgumentException("Oh nose!"))
        testOutcome.recordStep(step)
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        def generatedReportText = jsonReport.text
        then:
        generatedReportText.contains "Oh nose!"
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.isError()
        reloadedOutcome.testSteps[0].errorMessage == "Oh nose!"
    }


    def "should include exception stack dump for failing test"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        def step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));
        testOutcome.recordStep(step);
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes);
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps[0].exception.stackTrace.size() > 0
    }

    def "should generate a qualified JSON report for an acceptance test run if the qualifier is specified"() {

        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        reporter.setQualifier("qualifier");
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.title == "A simple test case [qualifier]"
    }

    def "should generate a qualified JSON report with formatted parameters if the qualifier is specified"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        reporter.setQualifier("a_b");

        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.title == "A simple test case [a_b]"
    }


    def "should record test groups as nested structures"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3").startingAt(FIRST_OF_JANUARY))
        testOutcome.endGroup()
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps.size() == 1
        reloadedOutcome.testSteps[0].children.size() == 3

    }

    def "should record minimal nested test groups as nested structures"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1.1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1.1.1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps.size() == 1
        reloadedOutcome.testSteps[0].children.size() == 1
        reloadedOutcome.testSteps[0].children[0].children.size() == 1
    }


    def "should include the name of any screenshots where present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);
        def screenshot = new File(outputDirectory, "step_1.png");
        def source = new File(outputDirectory, "step_1.html");
        and:
        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY);
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY));
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps[0].screenshotCount == 1
        reloadedOutcome.testSteps[0].screenshots[0].screenshotFile.name.endsWith("step_1.png")
        reloadedOutcome.testSteps[0].screenshots[0].sourcecode.isPresent()
    }



    def "should include the name of any screenshots without html where present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        testOutcome.setStartTime(FIRST_OF_JANUARY);
        def screenshot = new File(outputDirectory, "step_1.png");
        and:
        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY);
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY));
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps[0].screenshotCount == 1
        reloadedOutcome.testSteps[0].screenshots[0].screenshotFile.name.endsWith("step_1.png")
        !reloadedOutcome.testSteps[0].screenshots[0].sourcecode.isPresent()
    }

}