package net.thucydides.core.reports

import net.thucydides.core.model.TestOutcome
import spock.lang.Specification
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled
import static org.junit.matchers.JUnitMatchers.everyItem
import net.thucydides.core.model.TestResult
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.withResult
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagName
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagType

class WhenProcessingTestOutcomes extends Specification {

    def loader = new TestOutcomeLoader()

    def "should load test outcomes from a given directory"() {
        when:
            List<TestOutcome> testOutcomes = loader.loadFrom(directoryInClasspathCalled("/tagged-test-outcomes"));
        then:
            testOutcomes.size() == 3
    }

    def "should not load test outcome from an invalid directory"() {
        when:
            loader.loadFrom(new File("/does-not-exist"))
        then:
            thrown IOException
    }

    def "should list all the tag types for the test outcomes"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tagTypes = testOutcomes.getTagTypes()
        then:
            tagTypes == ["epic", "feature", "story"]
    }

    def "should list all the tags for the test outcomes"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tags = testOutcomes.getTags()
        then:
            tags == ["a feature", "a story", "an epic", "another different story", "another story"]
    }

    def "should list all the tags of a given type for the test outcomes"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tags = testOutcomes.getTagsOfType 'story'
        then:
            tags == ["a story", "another different story", "another story"]
    }

    def "should list all the tags of a single type for the test outcomes"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tags = testOutcomes.getTagsOfType 'feature'
        then:
            tags == ["a feature"]
    }

    def "should list all the tags for a given type"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            Set<String> tagTypes = testOutcomes.withTagType("feature").getTagTypes()
        then:
            tagTypes == ["feature","story"] as Set
    }

    def "should list all the tag types for a given name"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            Set<String> tagTypes = testOutcomes.withTag("an epic").getTagTypes()
        then:
            tagTypes == ["epic","story"] as Set
    }

    def "should list tests in alphabetical order"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tests = testOutcomes.getTests()
        then:
            tests.size() == 3
            tests[0].getTitle() <= tests[1].getTitle()
            tests[1].getTitle() <= tests[2].getTitle()
    }

    def "should list tests for a given tag type"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tests = testOutcomes.withTagType("feature").getTests()
        then:
            tests everyItem(havingTagType("feature"))
    }

    def "should list tests for a given tag"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tests = testOutcomes.withTag("a story").getTests()
        then:
            tests everyItem(havingTagName("a story"))
    }

    def "should list all passing tests"() {
        given:
        TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        when:
        def tests = testOutcomes.passingTests.getTests()
        then:
            tests.size() == 1
            tests everyItem(withResult(TestResult.SUCCESS))
    }

    def "should list all failing tests"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        when:
            def tests = testOutcomes.failingTests.getTests()
        then:
            tests.size() == 1
            tests everyItem(withResult(TestResult.FAILURE))
    }

    def "should list all pending tests"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/test-outcomes/containing-failure"));
        when:
            def tests = testOutcomes.pendingTests.getTests()
        then:
            tests.size() == 1
            tests everyItem(withResult(TestResult.PENDING))
    }

    def "should list tests for a given tag and tag type"() {
        given:
            TestOutcomes testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        when:
            def tests = testOutcomes.withTagType("feature").withTag("a feature").getTests()
        then:
            tests everyItem(havingTagName("a feature"))
    }

    def "should provide total test duration for a set of tests"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        then:
            testOutcomes.duration == 3000
    }

    def "should count tests in set"() {
        when:
            def testOutcomes = TestOutcomeLoader.testOutcomesIn(directoryInClasspathCalled("/tagged-test-outcomes"));
        then:
            testOutcomes.total == 3
    }

}
