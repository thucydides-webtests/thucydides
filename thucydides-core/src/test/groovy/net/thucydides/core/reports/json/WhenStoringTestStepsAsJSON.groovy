package net.thucydides.core.reports.json

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.util.MockEnvironmentVariables
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class WhenStoringTestStepsAsJSON extends Specification {

    def environmentVars = new MockEnvironmentVariables();

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()

    static TestStep simpleStep = TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS)
            .startingAt(FIRST_OF_JANUARY)

    static String simpleStepJson = """
{
  "number" : 0,
  "description" : "some step",
  "duration" : 0,
  "result" : "SUCCESS",
  "startTime" : ${FIRST_OF_JANUARY.millis}
}
"""

    @Shared
    def converter = new JacksonJSONConverter(environmentVars)

    @Unroll
    def "should generate an JSON report for a test step"() {
        expect:
        StringWriter writer = new StringWriter();
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, testStep);
        def renderedJson = writer.toString()
        JSONCompare.compareJSON(expectedJson, renderedJson, JSONCompareMode.LENIENT).passed();

        where:
        testStep    | expectedJson
        simpleStep  | simpleStepJson
    }


    def "should read a test step from a simple JSON string"() {
        expect:
        def reader = new StringReader(json)
        def step = converter.mapper.readValue(reader, net.thucydides.core.model.TestStep)
        step == expectedStep

        where:
        json            | expectedStep
        simpleStepJson  | simpleStep
    }

    def "should read and write a test step containing an error"() {
        given:
            TestStep failingStep = TestStep.forStepCalled("some step").withResult(TestResult.FAILURE)
                                           .startingAt(FIRST_OF_JANUARY);
            failingStep.failedWith(new Throwable("Oh crap!"))

        when:
            StringWriter writer = new StringWriter();
            converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, failingStep);
            def renderedJson = writer.toString()
        and:
            def reader = new StringReader(renderedJson)
            def step = converter.mapper.readValue(reader, net.thucydides.core.model.TestStep)
        then:
            step.equals(failingStep)
    }

    def "should read and write a test step with children"() {
        given:
        TestStep parentStep = TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY)
        parentStep.addChildStep(TestStep.forStepCalled("child step 1").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY))
        parentStep.addChildStep(TestStep.forStepCalled("child step 2").withResult(TestResult.SUCCESS)
                .startingAt(FIRST_OF_JANUARY))

        when:
        StringWriter writer = new StringWriter();
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, parentStep);
        def renderedJson = writer.toString()
        and:
        def reader = new StringReader(renderedJson)
        def step = converter.mapper.readValue(reader, net.thucydides.core.model.TestStep)
        then:
        step.equals(parentStep)
        parentStep.getChildren().size() == 2
    }
}