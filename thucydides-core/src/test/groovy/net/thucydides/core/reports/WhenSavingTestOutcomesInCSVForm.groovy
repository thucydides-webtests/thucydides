package net.thucydides.core.reports

import au.com.bytecode.opencsv.CSVReader
import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.csv.CSVReporter
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

/**
 * Test outcomes can be saved as CSV files, so they can be imported and manipulated in Excel.
 */
class WhenSavingTestOutcomesInCSVForm extends Specification {

    @TempDir File temporaryDirectory
    def loader = new TestOutcomeLoader()

    def "should store an empty set of test outcomes as an empty CSV file with only column titles"() {
        given: "no test results"
            def testOutcomeList = TestOutcomes.withNoResults()
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory)
            File csvResults = csvReporter.generateReportFor(testOutcomeList)
        then: "the CSV file contains a single line"
            csvResults.text.readLines().size() == 1
        and: "the first line should contain the test outcome headings"
            linesIn(csvResults)[0] == ["Story", "Title", "Result", "Date", "Stability", "Duration (s)"]
    }

    def "should store a row of data for each test result"() {
        given: "a set of test results"
            def testOutcomeList = loader.loadFrom(directoryInClasspathCalled("/tagged-test-outcomes"));
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory)
            File csvResults = csvReporter.generateReportFor(TestOutcomes.of(testOutcomeList))
        then: "there should be a row for each test result"
            def lines = linesIn(csvResults)
            lines.size() == 4
    }

    def linesIn(File csvResults) {
        def reader = new CSVReader(new FileReader(csvResults))
        reader.readAll()
    }
}
