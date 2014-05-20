package net.thucydides.core.reports.json

import com.github.goldin.spock.extensions.tempdir.TempDir
import com.sun.xml.internal.bind.v2.TODO
import net.thucydides.core.annotations.*
import net.thucydides.core.digest.Digest
import net.thucydides.core.model.DataTable
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import net.thucydides.core.reports.AcceptanceTestReporter
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringTestOutcomesAsJSON extends Specification {

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()
    private static final DateTime SECOND_OF_JANUARY = new LocalDateTime(2013, 1, 2, 0, 0, 0, 0).toDateTime()

    def AcceptanceTestReporter reporter

    @TempDir File outputDirectory

    TestOutcomes allTestOutcomes = Mock();

    def setup() {
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
    "screenshots" : [ ],
    "result" : "SUCCESS",
    "children" : [ ],
    "exception" : null,
    "error" : false,
    "ignored" : false,
    "durationInSeconds" : 0.0,
    "skipped" : false,
    "agroup" : false,
    "flattenedSteps" : [ ],
    "leafTestSteps" : [ ],
    "pending" : false,
    "failure" : false,
    "errorMessage" : "",
    "successful" : true,
    "firstScreenshot" : null,
    "shortErrorMessage" : "",
    "screenshotCount" : 0
  } ],
  "userStory" : {
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
  },
  "title" : "Should do this",
  "description" : "Some description",
  "issues" : [ ],
  "versions" : [ ],
  "tags" : [ {
    "name" : "A user story",
    "type" : "story"
  } ],
  "startTime" : ${FIRST_OF_JANUARY.millis},
  "duration" : 0,
  "error" : 0,
  "path" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON",
  "result" : "SUCCESS",
  "skipped" : 0,
  "storyTitle" : "A user story",
  "pathId" : "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
  "manual" : false,
  "pending" : 0,
  "success" : 1,
  "failure" : 0,
  "steps" : 1,
  "ignored" : 0,
  "skippedOrIgnored" : 0
  }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            println jsonReport.getText()
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    /*
        TODO:

    def "should include the project and batch start time in the JSON report if specified."() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))

        testOutcome = testOutcome.forProject("Some Project").inTestRunTimestamped(SECOND_OF_JANUARY)

        and:
        def expectedReport = """\
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "project" : "Some Project",
                  "batchStartTime" : "${SECOND_OF_JANUARY}",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
                  },
                  "description":"Some description",
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "${FIRST_OF_JANUARY}",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [],
                  "versions": [],
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
                      "startTime": ${FIRST_OF_JANUARY.millis},
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }


    def "should record screenshot details"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY).addScreenshot(new ScreenshotAndHtmlSource(new File("screenshot1.png"))))
        and:
        def expectedReport = """\
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
                  },
                  "description":"Some description",
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "${FIRST_OF_JANUARY}",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [],
                  "versions": [],
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
                      "startTime": ${FIRST_OF_JANUARY.millis},
                      "screenshots":  [
                            {
                              "screenshot": {
                                "path": "screenshot1.png"
                              }
                            }
                          ],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """
        when:
        def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
        JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should generate an JSON report for an acceptance test run without a test class"() {
        given:
            def testOutcome = TestOutcome.forTestInStory("should_do_this",
                                                         net.thucydides.core.model.Story.withId("id","name"))
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """\
                {
                  "title": "Should do this",
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "$FIRST_OF_JANUARY",
                  "user-story": {
                    "id": "id",
                    "storyName": "name"
                  },
                  "issues": [],
                  "versions": [],
                  "tags": [
                    {
                      "name": "name",
                      "type": "story"
                    }
                  ],
                  "test-steps": [
                    {
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should include issues in the JSON report"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class)
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                    startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$ATestScenarioWithIssues",
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
                  "timestamp": "$FIRST_OF_JANUARY",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [
                    "#789",
                    "#123",
                    "#456"
                  ],
                  "versions": [],
                  "tags": [
                    {
                      "name": "A user story",
                      "type": "story"
                    }
                  ],
                  "test-steps": [
                    {
                      "number": 1,
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }


    def "should generate an JSON report for a manual acceptance test run"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).asManualTest();
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "$FIRST_OF_JANUARY",
			  "manual": "true",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
			    },
			    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
			  },
			  "issues": [],
			  "versions": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "number": 1,
			      "description": "step 1",
			      "duration": 0,
			      "startTime": $FIRST_OF_JANUARY.millis,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }

    def "should generate an JSON report for an acceptance test run with a table"() {

        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome.startTime = FIRST_OF_JANUARY

            def table = DataTable.withHeaders(["firstName", "lastName", "age"]).
                                  andRows([["Joe", "Smith", "20"],["Jack", "Jones", "21"]]).build()
            testOutcome.useExamplesFrom(table)
            table.row(0).hasResult(TestResult.FAILURE)
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
                  },
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "$FIRST_OF_JANUARY",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [],
                  "versions": [],
                  "tags": [
                    {
                      "name": "A user story",
                      "type": "story"
                    }
                  ],
                  "test-steps": [
                    {
                      "number": 1,
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
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
            when:
                def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
            then:
                JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }

    def "should generate an JSON report for an acceptance test run with a qualifier"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier")
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
			{
			  "title": "Should do this [a qualifier]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
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
			  "timestamp": "$FIRST_OF_JANUARY",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
			    },
			    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
			  },
			  "issues": [],
			  "versions": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
                  "number": 1,
			      "description": "step 1",
			      "duration": 0,
			      "startTime": $FIRST_OF_JANUARY.millis,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }

    def "should escape new lines in title and qualifier attributes"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier with \n a new line");
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
			{
			  "title": "Should do this [a qualifier with \u0026#10; a new line]",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
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
			  "timestamp": "$FIRST_OF_JANUARY",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
			    },
			    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
			    "storyName": "A user story",
			    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
			  },
			  "issues": [],
			  "versions": [],
			  "tags": [
			    {
			      "name": "A user story",
			      "type": "story"
			    }
			  ],
			  "test-steps": [
			    {
			      "number": 1,
			      "description": "step 1",
			      "duration": 0,
			      "startTime": $FIRST_OF_JANUARY.millis,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}
			"""
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }


    def "should store annotated tags and issues in the JSON reports"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        and:
            def expectedReport = """
            {
              "title": "Should do this",
              "name": "should_do_this",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenarioWithTags",
                "issues": [
                    "PROJ-123"
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
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenarioWithTags"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.SomeTestScenarioWithTags",
                "storyName": "Some test scenario with tags",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [
                "PROJ-123"
              ],
              "versions": [],
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
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "SUCCESS",
                  "children": []
                }
              ]
            }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should include the session id if provided"()  {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.setSessionId("1234");
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        and:
            def expectedReport = """
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
                  },
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "$FIRST_OF_JANUARY",
                  "session-id": "1234",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [],
                  "versions": [],
                  "tags": [
                    {
                      "name": "A user story",
                      "type": "story"
                    }
                  ],
                  "test-steps": [
                    {
                      "number": 1,
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.STRICT).passed();
    }

    def "should contain the feature if provided"() {
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setSessionId("1234");
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));

        def expectedReport = """
			{
			  "title": "Should do this",
			  "name": "should_do_this",
			  "test-case": {
			    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenarioInAFeature"
			  },
			  "result": "SUCCESS",
			  "steps": "1",
			  "successful": "1",
			  "failures": "0",
			  "skipped": "0",
			  "ignored": "0",
			  "pending": "0",
			  "duration": "0",
			  "timestamp": "$FIRST_OF_JANUARY",
			  "user-story": {
			    "userStoryClass": {
			      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AFeature\$AUserStoryInAFeature"
			    },
			    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature.AUserStoryInAFeature",
			    "storyName": "A user story in a feature",
			    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature",
			    "qualifiedFeatureClassName": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature",
			    "name": "A feature"
			  },
			  "issues": [],
			  "versions": [],
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
			      "startTime": $FIRST_OF_JANUARY.millis,
			      "screenshots": [],
			      "result": "SUCCESS",
			      "children": []
			    }
			  ]
			}	
		"""

        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should record features and stories as tags"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
        and:
            def expectedReport = """
                {
                  "title": "Should do this",
                  "name": "should_do_this",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenarioInAFeature"
                  },
                  "result": "SUCCESS",
                  "steps": "1",
                  "successful": "1",
                  "failures": "0",
                  "skipped": "0",
                  "ignored": "0",
                  "pending": "0",
                  "duration": "0",
                  "timestamp": "$FIRST_OF_JANUARY",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AFeature\$AUserStoryInAFeature"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature.AUserStoryInAFeature",
                    "storyName": "A user story in a feature",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature",
                    "qualifiedFeatureClassName": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AFeature",
                    "name": "A feature"
                  },
                  "issues": [],
                  "versions": [],
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
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
            """

        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
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
            jsonReport.text.contains("java.lang.IllegalArgumentException");
    }

    def "should generate a qualified JSON report for an acceptance test run if the qualifier is specified"() {
        given:
            def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
            testOutcome.setStartTime(FIRST_OF_JANUARY);
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
            reporter.setQualifier("qualifier");
        and:
            def expectedReport = """
                {
                  "title": "A simple test case [qualifier]",
                  "name": "a_simple_test_case",
                  "test-case": {
                    "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
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
                  "timestamp": "$FIRST_OF_JANUARY",
                  "user-story": {
                    "userStoryClass": {
                      "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                    },
                    "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                    "storyName": "A user story",
                    "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
                  },
                  "issues": [],
                  "versions": [],
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
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
                """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should generate a qualified JSON report with formatted parameters if the qualifier is specified"() {
        given:
            def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
            testOutcome.setStartTime(FIRST_OF_JANUARY);
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
            reporter.setQualifier("a_b");
        and:
            def expectedReport = """
            {
              "title": "A simple test case [a_b]",
              "name": "a_simple_test_case",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
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
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                "storyName": "A user story",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [],
              "versions": [],
              "tags": [
                {
                  "name": "A user story",
                  "type": "story"
                }
              ],
              "test-steps": [
                {
                  "number": 1,
                  "description": "step 1",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "SUCCESS",
                  "children": []
                }
              ]
            }
        """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }


    def "should count the total number of steps with each outcome in acceptance test run"() {
        given:
            def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
            testOutcome.setStartTime(FIRST_OF_JANUARY);
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.errorTestStepCalled("step 7").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9").startingAt(FIRST_OF_JANUARY));
        and:
        def expectedReport = """
            {
              "title": "A simple test case",
              "name": "a_simple_test_case",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
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
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                "storyName": "A user story",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [],
              "versions": [],
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
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "SUCCESS",
                  "children": []
                },
                {
                  "description": "step 2",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "IGNORED",
                  "children": []
                },
                {
                  "description": "step 3",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "IGNORED",
                  "children": []
                },
                {
                  "description": "step 4",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "SUCCESS",
                  "children": []
                },
                {
                  "description": "step 5",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "FAILURE",
                  "children": []
                },
                {
                  "description": "step 6",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "FAILURE",
                  "children": []
                },
                {
                  "description": "step 7",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "ERROR",
                  "children": []
                },
                {
                  "description": "step 8",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "SKIPPED",
                  "children": []
                },
                {
                  "description": "step 9",
                  "duration": 0,
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "result": "PENDING",
                  "children": []
                }
              ]
            }
        """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }


    def "should record test groups as nested structures"()  {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
            testOutcome.setStartTime(FIRST_OF_JANUARY);

            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
            testOutcome.startGroup()
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3").startingAt(FIRST_OF_JANUARY))
            testOutcome.endGroup()
        and:
            def expectedReport = """
            {
              "title": "A nested test case",
              "name": "a_nested_test_case",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeNestedTestScenario"
              },
              "result": "SUCCESS",
              "steps": "3",
              "successful": "3",
              "failures": "0",
              "skipped": "0",
              "ignored": "0",
              "pending": "0",
              "duration": "0",
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                "storyName": "A user story",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [],
              "versions": [],
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
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "children": [
                    {
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    },
                    {
                      "description": "step 2",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    },
                    {
                      "description": "step 3",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    }
                  ]
                }
              ]
            }
        """

        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should_record_nested_test_groups_as_nested_structures"() {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
            testOutcome.setStartTime(FIRST_OF_JANUARY);

            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
            testOutcome.startGroup()
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1.1").startingAt(FIRST_OF_JANUARY))
            testOutcome.startGroup()
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 4").startingAt(FIRST_OF_JANUARY));
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 5").startingAt(FIRST_OF_JANUARY));
            testOutcome.endGroup();
            testOutcome.endGroup();
        and:
            def expectedReport = """
            {
              "title": "A nested test case",
              "name": "a_nested_test_case",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeNestedTestScenario"
              },
              "result": "SUCCESS",
              "steps": "5",
              "successful": "5",
              "failures": "0",
              "skipped": "0",
              "ignored": "0",
              "pending": "0",
              "duration": "0",
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                "storyName": "A user story",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [],
              "versions": [],
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
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "children": [
                    {
                      "description": "step 1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    },
                    {
                      "description": "step 2",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    },
                    {
                      "description": "step 3",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "result": "SUCCESS",
                      "children": []
                    },
                    {
                      "description": "Group 1.1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "children": [
                        {
                          "description": "step 4",
                          "duration": 0,
                          "startTime": $FIRST_OF_JANUARY.millis,
                          "screenshots": [],
                          "result": "SUCCESS",
                          "children": []
                        },
                        {
                          "description": "step 5",
                          "duration": 0,
                          "startTime": $FIRST_OF_JANUARY.millis,
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
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();

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
        and:
            def expectedReport = """
            {
              "title": "A nested test case",
              "name": "a_nested_test_case",
              "test-case": {
                "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeNestedTestScenario"
              },
              "result": "SUCCESS",
              "steps": "1",
              "successful": "1",
              "failures": "0",
              "skipped": "0",
              "ignored": "0",
              "pending": "0",
              "duration": "0",
              "timestamp": "$FIRST_OF_JANUARY",
              "user-story": {
                "userStoryClass": {
                  "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
                },
                "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
                "storyName": "A user story",
                "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
              },
              "issues": [],
              "versions": [],
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
                  "startTime": $FIRST_OF_JANUARY.millis,
                  "screenshots": [],
                  "children": [
                    {
                      "description": "Group 1.1",
                      "duration": 0,
                      "startTime": $FIRST_OF_JANUARY.millis,
                      "screenshots": [],
                      "children": [
                        {
                          "description": "Group 1.1.1",
                          "duration": 0,
                          "startTime": $FIRST_OF_JANUARY.millis,
                          "screenshots": [],
                          "children": [
                            {
                              "description": "step 1",
                              "duration": 0,
                              "startTime": $FIRST_OF_JANUARY.millis,
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
              ]
            }
            """
        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

    def "should include the name of any screenshots where present"()  {
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
         and:
            def osNeutralScreenshot = osNeutralPath(screenshot.absolutePath)
            def osNeutralSource = osNeutralPath(source.absolutePath)
            def expectedReport = """
            {
             "title": "A simple test case",
             "name": "a_simple_test_case",
             "test-case": {
               "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$SomeTestScenario"
             },
             "result": "FAILURE",
             "steps": "2",
             "successful": "1",
             "failures": "1",
             "skipped": "0",
             "ignored": "0",
             "pending": "0",
             "duration": "0",
             "timestamp": "$FIRST_OF_JANUARY",
             "user-story": {
               "userStoryClass": {
                 "classname": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON\$AUserStory"
               },
               "id": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON.AUserStory",
               "storyName": "A user story",
               "path": "net.thucydides.core.reports.json.WhenStoringTestOutcomesAsJSON"
             },
             "issues": [],
             "versions": [],
             "tags": [
               {
                 "name": "A user story",
                 "type": "story"
               }
             ],
             "test-steps": [
               {
                 "number" : 1,
                 "description": "step 1",
                 "duration": 0,
                 "startTime": $FIRST_OF_JANUARY.millis,
                 "screenshots": [
                   {
                     "screenshot": {
                       "path": "$osNeutralScreenshot"
                     },
                     "sourcecode": {
                       "path": "$osNeutralSource"
                     }
                   }
                 ],
                 "result": "SUCCESS",
                 "children": []
               },
               {
                 "number" : 2,
                 "description": "step 2",
                 "duration": 0,
                 "startTime": $FIRST_OF_JANUARY.millis,
                 "screenshots": [],
                 "result": "FAILURE",
                 "children": []
               }
             ]
            }
            """

        when:
            def jsonReport = reporter.generateReportFor(testOutcome, allTestOutcomes)
        then:
            JSONCompare.compareJSON(expectedReport, jsonReport.text, JSONCompareMode.LENIENT).passed();
    }

*/
    def osNeutralPath(path){
        path.replace("\\","\\\\")
    }
}