package net.thucydides.core.reports

import net.thucydides.core.ThucydidesReports
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import org.openqa.selenium.WebDriver
import spock.lang.Specification

import java.nio.file.Files

class WhenSettingUpReportServices extends Specification {

//    File outputDir;

    def environmentVariables = new MockEnvironmentVariables();
    def configuration = new SystemPropertiesConfiguration(environmentVariables);

//    def setup() {
//        outputDir = Files.createTempDirectory("reports").toFile()
//    }
//
//    def cleanup() {
//        outputDir.deleteDir()
//    }

    def "should be able to configure default report services"() {
        when:
            def reportService = ThucydidesReports.getReportService(configuration)
        then:
            reportService.getSubscribedReporters().size() != 0
    }

    def "should be able to configure default listeners"() {
        when:
            def listeners = ThucydidesReports.setupListeners(configuration)
        then:
            listeners.baseStepListener != null
    }

    def "should be able to configure default listeners with a webdriver instance"() {
        when:
            def driver = Mock(WebDriver)
            def listeners = ThucydidesReports.setupListeners(configuration).withDriver(driver)
        then:
            listeners.baseStepListener.driver == driver
    }

    def "should be able to obtain the results from the base step listener"() {
        when:
            def listeners = ThucydidesReports.setupListeners(configuration)
        then:
            listeners.getResults() == listeners.baseStepListener.testOutcomes
    }

    def "should generate reports using each of the subscribed reporters"() {
        given:
            def outputDir = Files.createTempDirectory("reports").toFile()
            configuration.setOutputDirectory(outputDir)
            ThucydidesReports.setupListeners(configuration)
            def testOutcomes = [TestOutcome.forTestInStory("some test", Story.called("some story"))]
        when:
            ThucydidesReports.getReportService(configuration).generateReportsFor(testOutcomes)
        then:
            println outputDir
            outputDir.list().findAll { it.endsWith(".html")}.size() == 1
            outputDir.list().findAll { it.endsWith(".xml")}.size() == 1
            outputDir.list().findAll { it.endsWith(".json")}.size() == 1
    }
}
