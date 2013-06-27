package net.thucydides.core.reports.adaptors.specflow

import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor
import org.jbehave.core.annotations.Given
import org.jbehave.core.annotations.Then
import spock.lang.Ignore
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.fileInClasspathCalled

/**
 * We want to convert Specflow log outputs (possibly with some extra optional data) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenLoadingSpecflowLogOutputAsTestOutcomes extends Specification {

    def simpleSpecflowOutput = """***** root.packages.MyCapability.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> done: bla bla bla (0.9s)
"""

    def "should find the scenario and story titles"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 1
        and:
            testOutcomes.get(0).title == "My scenario"
        and:
            testOutcomes.get(0).storyTitle == "My feature"
        and:
            testOutcomes.get(0).getTags()
            testOutcomes.get(0).path == "root.packages.MyCapability.SpecFlow.Features.MyFeature"
    }

    def "should find the scenario steps"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.description } == ["Given a precondition",
                                                                     "When something happens",
                                                                     "Then some outcome should occur"]
    }

    def "should record the scenario step results"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
    }

    def "should record the step times"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.duration } == [2300, 1000, 900]
    }

    def  failingSpecflowOutput = """***** my.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> error: bla bla bla
   more bla bla bla
"""

    def "should record step failures"() {
        given:
            def specflowOutput = fileFrom(failingSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.FAILURE]
    }

    def  multiScenarioSpecflowOutput = """***** my.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> done: bla bla bla (0.9s)
***** my.SpecFlow.Features.MyFeature.MyOtherScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> error: bla bla bla
   more bla bla bla
"""

    def "should record multiple steps"() {
        given:
            def specflowOutput = fileFrom(multiScenarioSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 2
        and:
            testOutcomes.collect{ it.title } == ["My scenario", "My other scenario"]
    }

    def "should record multiple scenarios"() {
        given:
        def specflowOutput =fileInClasspathCalled("/specflow-output/multiple-scenarios.txt")
        TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
        def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
        testOutcomes.size() == 8
    }

    def "should load outcomes from output directory"() {
        given:
            def outputReportsDir = fileInClasspathCalled("/specflow-output/samples")
            def specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(outputReportsDir)
        then:
            testOutcomes.size() == 3

    }

    def "should record multiple different scenarios in a single file"() {
        given:
        def specflowOutput =fileInClasspathCalled("/specflow-output/multiple-separate-scenarios.txt")
        TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
        def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
        testOutcomes.size() == 3
        and:
        testOutcomes.collect { it.title } == ["Populate business payment process drop down list",
                "Test to fail",
                "Debit account owner selection"]
        and:
        testOutcomes.collect { it.result } == [TestResult.SUCCESS, TestResult.FAILURE, TestResult.PENDING]
    }

    @Ignore("WIP - TODO")
    def "should record scenarios with rows in a table"() {
        given:
        def specflowOutput =fileInClasspathCalled("/specflow-output/passing-multiple-scenarios-in-a-table.txt")
        TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
        def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
        testOutcomes.size() == 1
        and:
        testOutcomes.dataTable != null
        and:
        testOutcomes.dataTable.rows.collect { it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
        and:
        testOutcomes.dataTable.rows.collect {it.cellValues} ==
                ["Inputter-DirectBanking","Funds Transfer between Own Accounts","N/A","Funds Transfer",""]
        ["Inputter-DirectBanking","Credit Card Repayment","N/A","Funds Transfer",""]
        ["Inputter","Credit Card Repayment","N/A","Funds Transfer",""]
    }

    @Ignore("WIP - TODO")
    def "for a table-based scenario we should use the first set of steps"() {
        given:
        def specflowOutput =fileInClasspathCalled("/specflow-output/passing-multiple-scenarios-in-a-table.txt")
        TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
        def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
        testOutcomes.testSteps.size() == 7
        and:
        testOutcomes.testSteps.collect{ it.description } ==
                ["Given ESD Epp.RegularPaymentCapture app is loaded with CommSee.v1 theme",
                        "And the Payment Group Details page is loaded",
                        "Given the BusinessPaymentProcess drop down list is populated",
                        "And I am of Inputter-DirectBanking role",
                        "When I select Funds Transfer between Own Accounts from BusinessPaymentProcess drop down list",
                        "Then the BusinessTransactionType drop down list is populated with N/A",
                        "And the PaymentType drop down list is populated with Funds Transfer"]
    }

    @Ignore("WIP - TODO")
    def "should record scenarios with rows in a table containing failures"() {
        given:
        def specflowOutput =fileInClasspathCalled("/specflow-output/multiple-scenarios-in-a-table-with-failures.txt")
        TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
        def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
        testOutcomes.size() == 1
        and:
        testOutcomes.dataTable != null
        and:
        testOutcomes.dataTable.rows.collect { it.result } ==
                [TestResult.SUCCESS, TestResult.FAILURE, TestResult.FAILURE, TestResult.FAILURE,
                 TestResult.FAILURE, TestResult.FAILURE,TestResult.FAILURE, TestResult.FAILURE]
    }

    @TempDir File tmp

    def fileFrom(def contents) {
        def outputFile = new File(tmp, "specflow-output-${System.currentTimeMillis()}.out")
        outputFile.write(contents)
        return outputFile
    }


}
