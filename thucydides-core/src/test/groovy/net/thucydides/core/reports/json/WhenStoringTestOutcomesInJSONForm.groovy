package net.thucydides.core.reports.json

import com.sun.xml.internal.bind.v2.TODO
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import org.fest.util.Files
import org.skyscreamer.jsonassert.JSONAssert
import spock.lang.Ignore
import spock.lang.Specification

class WhenStoringTestOutcomesInJSONForm extends Specification {

    JSONConverter converter = new JacksonJSONConverter()
     /*
    def "should convert a simple test outcome to JSON"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.called("someStory"))
        when:
            def json = converter.toJson(outcome)
        then:
            def expectedJson = """{
  "name" : "someTest",
  "testSteps" : [ ],
  "userStory" : {
    "userStoryClass" : null,
    "id" : "someStory",
    "storyName" : "someStory",
    "path" : null,
    "qualifiedFeatureClassName" : null,
    "featureName" : null
  },
  "title" : "Some test",
  "issues" : [ ],
  "versions" : [ ],
  "tags" : [ {
    "name" : "someStory",
    "type" : "story"
  } ],
  "startTime" : ${outcome.startTime.millis},
  "duration" : 0,
  "error" : 0,
  "storyTitle" : "someStory",
  "pathId" : "someStory",
  "completeName" : "someStory:someTest",
  "manual" : false,
  "pending" : 0,
  "success" : 0,
  "failure" : 0,
  "skipped" : 0,
  "result" : "SUCCESS",
  "steps" : 0,
  "ignored" : 0,
  "skippedOrIgnored" : 0
}"""
            JSONAssert.assertEquals(expectedJson, json.toString(),false)
    }
       */
    // TODO

    @Ignore
    def "should convert a simple JSON test outcome to the Java equivalent"() {
        given:
        File jsonFile = Files.newTemporaryFile()
        jsonFile << """{
  "name" : "someTest",
  "testSteps" : [ ],
  "userStory" : {
    "userStoryClass" : null,
    "id" : "someStory",
    "storyName" : "someStory",
    "path" : null,
    "qualifiedFeatureClassName" : null,
    "featureName" : null
  },
  "title" : "Some test",
  "issues" : [ ],
  "versions" : [ ],
  "tags" : [ {
    "name" : "someStory",
    "type" : "story"
  } ],
  "startTime" : 0,
  "duration" : 0,
  "error" : 0,
  "storyTitle" : "someStory",
  "pathId" : "someStory",
  "completeName" : "someStory:someTest",
  "manual" : false,
  "pending" : 0,
  "success" : 0,
  "failure" : 0,
  "skipped" : 0,
  "result" : "SUCCESS",
  "steps" : 0,
  "ignored" : 0,
  "skippedOrIgnored" : 0
}"""
    when:
        def testOutcome = converter.fromJson(jsonFile)

    then:
    println testOutcome
    }

}