package net.thucydides.core.model

import net.thucydides.core.screenshots.ScreenshotProcessor
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.steps.StepFailure
import spock.lang.Specification

import static net.thucydides.core.model.TestResult.FAILURE
import static net.thucydides.core.model.TestResult.PENDING
import static net.thucydides.core.model.TestResult.SUCCESS
import static net.thucydides.core.model.TestResult.UNDEFINED

class WhenRecordingDataDrivenTestOutcomes extends Specification {

    def setup() {
        StepEventBus.eventBus.dropAllListeners()
    }

    def "Test outcomes should not have a data-driven table by default"()  {
        when:
            def testOutcome = new TestOutcome("someTest")
        then:
            !testOutcome.dataDriven
    }

    def "Test outcome with a data-driven table recorded should make this known"() {
        given:
            def testOutcome = new TestOutcome("someTest")
        when:
            def dataTable = DataTable.withHeaders(["firstName","lastName","age"]).build()
            testOutcome.useExamplesFrom(dataTable)
        then:
            testOutcome.dataDriven
    }

    def "Should be able to build a data table with headings"() {
        when:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).build()
        then:
            table.headers == ["firstName","lastName","age"]
            table.rows == []
    }

    def "Should be able to build a data table with headings and rows"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                              andRows([["Joe", "Smith",20],
                                       ["Jack", "Jones",21]]).build();
        then:
        table.headers == ["firstName","lastName","age"]
        table.rows.collect {it.values} ==[["Joe","Smith",20], ["Jack","Jones",21]]

    }

    def "Should be able to build a data table with headings and mapped rows"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                               ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        then:
        table.headers == ["firstName","lastName","age"]
        table.rows.collect {it.values} ==[["Joe","Smith",20], ["Jack","Jones",21]]
    }

    def "row results should be undefined by default"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                        ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        then:
        table.rows.collect {it.result} ==[UNDEFINED, UNDEFINED]
    }

    def "should be able to define the outcome by default"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).
                    andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                            ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        when:
            table.row(0).hasResult(FAILURE)
            table.row(1).hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def "should be able to define the outcome for successive rows"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).
                    andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                                   ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        when:
            table.currentRow().hasResult(FAILURE)
            table.nextRow()
            table.currentRow().hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def "should be able to add rows incrementally"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).build();
        when:
            table.addRow(["firstName":"Joe",  "lastName":"Smith","age":20])
            table.currentRow().hasResult(FAILURE)
            table.nextRow()
            table.addRow(["firstName":"Jack", "lastName":"Jones","age":21])
            table.currentRow().hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def screenshotProcessor = Mock(ScreenshotProcessor)
    def outputDirectory = Mock(File);

    def "Should be able to describe an example table via the event bus"() {
        given:
            def eventBus = new StepEventBus(screenshotProcessor)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                                               andRows([["Joe", "Smith",20],
                                                       ["Jack", "Jones",21]]).build())
        then:
            listener.testOutcomes[0].isDataDriven()
        and:
            listener.testOutcomes[0].dataTable
    }

    private static class SomeTest {
        public void step1() {}
        public void step2() {}
        public void step3() {}
        public void step4() {}
        public void step5() {}
    }

    def failure = Mock(StepFailure)

    def "Should be able to update the table results via the event bus"() {
        given:
            def eventBus = new StepEventBus(screenshotProcessor)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                    andRows([["Joe", "Smith",20],
                            ["Jack", "Smith",21],
                            ["Jack", "Smith",21]]).build())

            eventBus.exampleStarted(["firstName":"Joe","lastName":"Smith","age":20])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step1"));
            eventBus.stepFinished()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"Jack","lastName":"Smith","age":21])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step2"));
            eventBus.stepPending()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"Jack","lastName":"Smith","age":21])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step3"));
            eventBus.stepFailed(failure);

            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step4"));
            eventBus.stepIgnored()
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step5"));
            eventBus.stepIgnored()
            eventBus.exampleFinished()

            eventBus.testFinished()
        then:
            listener.testOutcomes[0].dataTable.rows.collect { it.result } == [SUCCESS, PENDING, FAILURE]
    }

    def "Should be able to update the table results incrementally via the event bus"() {
        given:
            def eventBus = new StepEventBus(screenshotProcessor)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).build())

            eventBus.exampleStarted(["firstName":"Joe","lastName":"Smith","age":20])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step1"));
            eventBus.stepFinished()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"Jack","lastName":"Smith","age":21])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step2"));
            eventBus.stepPending()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"John","lastName":"Smith","age":22])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step3"));
            eventBus.stepFailed(failure);

            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step4"));
            eventBus.stepIgnored()
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step5"));
            eventBus.stepIgnored()
            eventBus.exampleFinished()

            eventBus.testFinished()
        then:
            listener.testOutcomes[0].dataTable.rows.collect { it.result } == [SUCCESS, PENDING, FAILURE]
    }
}
