package net.thucydides.core.reports.json

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import org.skyscreamer.jsonassert.JSONAssert
import spock.lang.Specification

class WhenStoringTestOutcomesInJSONForm extends Specification {

    JSONConverter converter = new JacksonJSONConverter()

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
    "qualifiedStoryClassName" : "someStory",
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

    // TODO
       /*
    def "should convert a simple JSON test outcome to the Java equivalent"() {
        given:
        def jsonString = """{
  "name" : "someTest",
  "testSteps" : [ ],
  "userStory" : {
    "userStoryClass" : null,
    "qualifiedStoryClassName" : "someStory",
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
        def testOutcome = converter.fromJson(jsonString)

    then:
    println testOutcome
    }
    */
}