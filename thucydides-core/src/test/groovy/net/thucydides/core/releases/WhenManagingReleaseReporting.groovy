package net.thucydides.core.releases

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

/**
 * Release reporting is done using tags and naming conventions.
 */
class WhenManagingReleaseReporting extends Specification {

    def outcome1 = TestOutcome.forTestInStory("someTest 1", Story.withId("1","story"))
    def outcome2 = TestOutcome.forTestInStory("someTest 2", Story.withId("1","story"))
    def outcome3 = TestOutcome.forTestInStory("someTest 3", Story.withId("1","story"))
    def outcome4 = TestOutcome.forTestInStory("someTest 4", Story.withId("1","story"))

    def release1 = TestTag.withName("PROJ Release 1").andType("version")
    def release2 = TestTag.withName("PROJ Release 2").andType("version")
    def iteration1 = TestTag.withName("Iteration 1").andType("version")
    def iteration2 = TestTag.withName("Iteration 2").andType("version")
    def iteration3 = TestTag.withName("Iteration 3").andType("version")

    def setup() {
        outcome1.addTags([release1,iteration1])
        outcome2.addTags([release1, iteration1])
        outcome3.addTags([release1, iteration2])
        outcome4.addTags([release2, iteration3])
    }

    def environmentVariables = new MockEnvironmentVariables()
    def reportNameProvider = new ReportNameProvider()

    def "should be able to list the top-level releases"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
        then:
            releases.collect { it.label } == ["PROJ Release 1", "PROJ Release 2"]
    }


    def "should know the capabilities (or other requirements) for a given release"() {
        given:
    }

    def "Releases should be available in JSON format"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releasesData = releaseManager.getJSONReleasesFrom(testOutcomes)
        then:
            releasesData == """[
  {
    "releaseTag": {
      "name": "PROJ Release 1",
      "type": "version"
    },
    "children": [
      {
        "releaseTag": {
          "name": "Iteration 1",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 1",
        "reportName": "8b0333df2bf13ef0aa16a04c7f2010ec8e228a9dfd88e3d22d6f531c65e1a15b.html"
      },
      {
        "releaseTag": {
          "name": "Iteration 2",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 2",
        "reportName": "3bee246f0bb923756b350b90139e7e2fcd478c902867688dad8a8eb78cebae9f.html"
      }
    ],
    "label": "PROJ Release 1",
    "reportName": "9b1746741253c3942611ab46897e77f81cdbcf8d35ec19a6c14b2d3b23d473d4.html"
  },
  {
    "releaseTag": {
      "name": "PROJ Release 2",
      "type": "version"
    },
    "children": [
      {
        "releaseTag": {
          "name": "Iteration 3",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 3",
        "reportName": "1705a1b20aad7e2e97692dd55eb994fee6bebc82b3c856b94530969094851a72.html"
      }
    ],
    "label": "PROJ Release 2",
    "reportName": "177e154abed192ebb6082e0ff5227a289fa7f3f78f7a50e943672b9eba80f4f9.html"
  }
]"""

    }
}