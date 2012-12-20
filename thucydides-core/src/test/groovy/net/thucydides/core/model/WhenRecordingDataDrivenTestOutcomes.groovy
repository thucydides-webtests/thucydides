package net.thucydides.core.model

import spock.lang.Specification

class WhenRecordingDataDrivenTestOutcomes extends Specification {
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
            testOutcome.recordTestData(dataTable)
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
        table.rows ==[["Joe","Smith",20], ["Jack","Jones",21]]

    }

    def "Should be able to build a data table with headings and mapped rows"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                               ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        then:
        table.headers == ["firstName","lastName","age"]
        table.rows ==[["Joe","Smith",20], ["Jack","Jones",21]]

    }

}
