package net.thucydides.core.reports.json

import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource
import net.thucydides.core.util.MockEnvironmentVariables
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringScreenshotsAsJSON extends Specification {

    def environmentVars = new MockEnvironmentVariables();

    def "should generate JSON for a screenshot"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def htmlFile = new File("screenshot.html")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile, htmlFile)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, screenshot);

        then:
        def expectedJson = """
{
  "sourcecode" : "$htmlFile.absolutePath",
  "screenshotFile" : "$screenshotFile.absolutePath"
}
"""
        def serializedStory = writer.toString()
        println serializedStory
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }


    def "should generate JSON for a screenshot with no source code"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def screenshot = new ScreenshotAndHtmlSource(screenshotFile)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, screenshot);

        then:
        def expectedJson = """
{
  "screenshotFile" : "$screenshotFile.absolutePath"
}
"""
        def serializedStory = writer.toString()
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }

    def "should read a screenshot from JSON"() {
        given:
        def screenshotFile = new File("screenshot.png")
        def htmlFile = new File("screenshot.html")

        def serializedScreenshot = """
{
  "sourcecode" : "$htmlFile.absolutePath",
  "screenshotFile" : "$screenshotFile.absolutePath"
}
"""
        def reader = new StringReader(serializedScreenshot)

        when:
        def converter = new JacksonJSONConverter(environmentVars)
        def screenshot = converter.mapper.readValue(reader,ScreenshotAndHtmlSource)

        then:
        screenshot.screenshotFile.absolutePath == screenshotFile.absolutePath
        screenshot.sourcecode.isPresent()
        screenshot.sourcecode.get().absolutePath == htmlFile.absolutePath
    }


    def "should read a screenshot with no source code from JSON"() {
        given:
        def screenshotFile = new File("screenshot.png")

        def serializedScreenshot = """
{
  "screenshotFile" : "$screenshotFile.absolutePath"
}
"""
        def reader = new StringReader(serializedScreenshot)

        when:
        def converter = new JacksonJSONConverter(environmentVars)
        def screenshot = converter.mapper.readValue(reader,ScreenshotAndHtmlSource)

        then:
        screenshot.screenshotFile.absolutePath == screenshotFile.absolutePath
        !screenshot.sourcecode.isPresent()
    }
}