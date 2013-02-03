package net.thucydides.junit.runners;


import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.steps.StepFailureException
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.ThucydidesWebdriverManager
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverInstanceFactory
import org.junit.runner.notification.RunNotifier
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification
import net.thucydides.samples.*

import static net.thucydides.core.model.TestResult.FAILURE
import static net.thucydides.core.model.TestResult.IGNORED
import static net.thucydides.core.model.TestResult.PENDING
import static net.thucydides.core.model.TestResult.SKIPPED
import static net.thucydides.core.model.TestResult.SUCCESS
import static net.thucydides.junit.runners.TestOutcomeChecks.resultsFrom
import static net.thucydides.junit.util.FileFormating.digest

class WhenRunningTestScenarios extends Specification {

    def firefoxDriver = Mock(FirefoxDriver)
    def htmlUnitDriver = Mock(HtmlUnitDriver)
    def webdriverInstanceFactory = Mock(WebdriverInstanceFactory)
    def environmentVariables = new MockEnvironmentVariables()
    def webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)

    def setup() {
        webdriverInstanceFactory.newFirefoxDriver(_) >> firefoxDriver
        webdriverInstanceFactory.newHtmlUnitDriver(_) >> htmlUnitDriver
    }



    def "should be able to specify a different driver"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioUsingHtmlUnit, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }


    def "should be able to specify a different driver for an individual test"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioUsingHtmlUnitForOneTest, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should record the steps that are executed"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    def "should record state between steps"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithStateVariables, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        results["joes_test"].result == SUCCESS
        results["jills_test"].result == SUCCESS
        results["no_ones_test"].result == PENDING

    }

    def "an error in a nested non-step method should cause the test to fail"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithFailingNestedNonStepMethod, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].result == FAILURE
        results["happy_day_scenario"].testSteps[2].result == FAILURE
    }

    def "an error in a non-step method should be displayed as a failing step"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithFailingNonStepMethod, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].result == FAILURE
        results["happy_day_scenario"].testSteps.size() == 3
    }

    def "pending tests should be reported as pending"() {
        given:
        def runner = new ThucydidesRunner(SamplePendingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        then:
        outcomes[0].result == PENDING
        outcomes[0].testSteps == []
    }

    def "private annotated fields should be allowed"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioWithPrivateFields, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    def "annotated fields should be allowed in parent classes"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenarioWithFieldsInParent, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        outcomes.size() == 3
        and:
        results["happy_day_scenario"].title == "Happy day scenario"
        results["happy_day_scenario"].testSteps.size() == 4

        results["edge_case_1"].title == "Edge case 1"
        results["edge_case_1"].testSteps.size() == 3

        results["edge_case_2"].title == "Edge case 2"
        results["edge_case_2"].testSteps.size() == 2
    }

    def "annotated tests should have expected results"() {
        given:
        def runner = new ThucydidesRunner(testclass, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        def results = resultsFrom(outcomes)
        then:
        results["happy_day_scenario"].result == happy_day_result
        results["edge_case_1"].result == edge_case_1_result
        results["edge_case_2"].result == edge_case_2_result

        where:
        testclass                               | happy_day_result   | edge_case_1_result | edge_case_2_result
        SamplePassingScenarioWithPendingTests   | SUCCESS | PENDING | PENDING
        SamplePassingScenarioWithIgnoredTests   | SUCCESS | IGNORED | IGNORED
        SamplePassingScenarioWithEmptyTests     | SUCCESS | PENDING | PENDING
        MockOpenStaticDemoPageWithFailureSample | FAILURE | SUCCESS | SUCCESS
        MockOpenPageWithWebdriverErrorSample    | FAILURE | SUCCESS | SUCCESS
    }

    def "failing tests with no steps should still record the error"() {
        given:
        def runner = new ThucydidesRunner(SampleFailingScenarioWithEmptyTests, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcomes = runner.testOutcomes;
        then:
        outcomes.size() == 1
        outcomes[0].result == FAILURE
        outcomes[0].testFailureCause.message == "Failure without any steps."
    }

    def "should skip test steps after a failure"() {
        given:
        def runner = new ThucydidesRunner(SingleHtmlUnitTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        def steps = runner.testOutcomes[0].testSteps;
        def stepResults = steps.collect {it.result}
        stepResults == [SUCCESS, SUCCESS, IGNORED, SUCCESS, FAILURE, SKIPPED]
    }

    def "should skip any ignored tests"() {
        given:
        def runner = new ThucydidesRunner(TestIgnoredScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes[0].result == IGNORED;
        runner.testOutcomes[0].testSteps == [];

    }

    def "should skip test steps after a webdriver error"() {
        given:
        def runner = new ThucydidesRunner(SampleNoSuchElementExceptionScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def results = resultsFrom(runner.testOutcomes)
        then:
        def steps = results["failing_happy_day_scenario"].getTestSteps()
        def stepResults = steps.collect {it.result}
        stepResults == [SUCCESS, IGNORED, SUCCESS, FAILURE, SKIPPED]
    }

    def "should record error message in the failing test step"() {
        given:
        def runner = new ThucydidesRunner(SingleHtmlUnitTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def steps = runner.testOutcomes[0].getTestSteps()
        def failingStep = steps[4]
        then:
        failingStep.errorMessage.contains "Expected: is <2>"
        and:
        failingStep.exception.class == StepFailureException
    }

    def "when a test throws a webdrriver exception is should be recorded in the step"() {
        given:
        def runner = new ThucydidesRunner(SingleTestScenarioWithWebdriverException, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def steps = runner.testOutcomes[0].getTestSteps()
        def failingStep = steps[3]
        then:
        failingStep.exception.class == StepFailureException
    }

    def "when a test throws a runtime exception is should be recorded in the step"() {
        given:
        def runner = new ThucydidesRunner(SingleTestScenarioWithRuntimeException, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def steps = runner.testOutcomes[0].getTestSteps()
        def failingStep = steps[3]
        then:
        failingStep.exception.class == StepFailureException
    }

    def "should record the name of the test scenario"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes[0].title == "Happy day scenario"
    }

    def "should execute tests in groups"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithGroups, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 1
        and:
        runner.testOutcomes[0].testSteps.size() == 3
    }

    def "should record an acceptance test result for each test"() {
        given:
        def runner = new ThucydidesRunner(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should derive the user story from the test case class"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        then:
        outcome.userStory.name == "Successful single test scenario"
    }

    def "should record each step with a human-readable name"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[0]
        then:
        firstStep.description == "Step that succeeds"
    }

    def "should be able to override default step names using the @Step annotation"() {
        given:
        def runner = new ThucydidesRunner(SuccessfulSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def pendingStep = outcome.testSteps[2]
        then:
        pendingStep.description == "A pending step"
    }

    def "steps with a parameter should contain the parameter value in the description"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[0]
        then:
        firstStep.description == "Step with a parameter: {foo}"
    }

    def "steps with multiple parameters should contain the parameter values in the description"() {
        given:
        def runner = new ThucydidesRunner(TestScenarioWithParameterizedSteps, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        def firstStep = outcome.testSteps[1]
        then:
        firstStep.description == "Step with two parameters: {foo, 2}"
    }

    def "should be able to override scenario titles using the @Title annotation"() {
        given:
        def runner = new ThucydidesRunner(AnnotatedSingleTestScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def outcome = runner.testOutcomes[0]
        then:
        outcome.title == "Oh happy days!"
    }

    def "should not require a steps in a test"() {
        given:
        def runner = new ThucydidesRunner(SampleScenarioWithoutSteps)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 3
    }

    def "should not require a webdriver in a test"() {
        given:
        def runner = new ThucydidesRunner(SimpleNonWebScenario)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.size() == 2
    }

    def "should ignore close if the webdriver is not defined"() {
        when:
        def manager = new ThucydidesWebdriverManager(webDriverFactory, new SystemPropertiesConfiguration(environmentVariables));
        then:
        manager.closeDriver()
    }

    @TempDir File temporaryDirectory

    class ATestableThucydidesRunner extends ThucydidesRunner {
        ATestableThucydidesRunner(Class<?> klass, WebDriverFactory webDriverFactory) {
            super(klass, webDriverFactory)
        }

        @Override
        File getOutputDirectory() {
            return temporaryDirectory
        }
    }


    def "xml test results should be written to the output directory"() {
        given:
        def runner = new ATestableThucydidesRunner(SamplePassingScenario, webDriverFactory)
        when:
        runner.run(new RunNotifier())
        def xmlReports = temporaryDirectory.list().findAll {it.endsWith(".xml")}
        then:
        xmlReports.size() == 3
        xmlReports.contains digest("sample_passing_scenario_edge_case_1.xml")
        xmlReports.contains digest("sample_passing_scenario_edge_case_2.xml")
        xmlReports.contains digest("sample_passing_scenario_happy_day_scenario.xml")
    }

    def "tests for multiple stories should be written to the output directory"() {
        when:
        new ATestableThucydidesRunner(SamplePassingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
        new ATestableThucydidesRunner(SampleFailingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
        def xmlReports = temporaryDirectory.list().findAll {it.endsWith(".xml")}
        then:
        xmlReports.size() == 6
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_edge_case_1.xml")
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_edge_case_2.xml")
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_happy_day_scenario.xml")
        xmlReports.contains digest("sample_failing_scenario_using_html_unit_edge_case_1.xml")
        xmlReports.contains digest("sample_failing_scenario_using_html_unit_edge_case_2.xml")
        xmlReports.contains digest("sample_failing_scenario_using_html_unit_happy_day_scenario.xml")
    }

    def "HTML test results should be written to the output directory"() {
        when:
        new ATestableThucydidesRunner(SamplePassingScenarioUsingHtmlUnit, webDriverFactory).run(new RunNotifier())
        def xmlReports = temporaryDirectory.list().findAll {it.endsWith(".html")}
        then:
        xmlReports.size() == 3
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_edge_case_1.html")
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_edge_case_2.html")
        xmlReports.contains digest("sample_passing_scenario_using_html_unit_happy_day_scenario.html")
    }

}
