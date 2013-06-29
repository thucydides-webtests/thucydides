package net.thucydides.core.requirements

import com.google.common.base.Optional
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import spock.lang.Specification
import spock.lang.Unroll

class WhenOrderingRequirementsTagProviders extends Specification {


    static def LESS_THAN = -1
    static def GREATER_THAN = 1
    static def EQUALS = 0

    static final class OtherFileSystemRequirementsTagProvider extends FileSystemRequirementsTagProvider {}
    static class DifferentTagProvider implements RequirementsTagProvider {
        Set<TestTag> getTagsFor(TestOutcome testOutcome) { null}
        List<Requirement> getRequirements() { null }
        Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) { null }
        Optional<Requirement> getRequirementFor(TestTag testTag) { null }
    }
    static final class AnotherDifferentTagProvider extends DifferentTagProvider {}

    static def fileSystemProvider1 = new FileSystemRequirementsTagProvider()
    static def fileSystemProvider2 = new FileSystemRequirementsTagProvider()
    static def otherFileSystemProvider = new OtherFileSystemRequirementsTagProvider()
    static def differentTagProvider = new DifferentTagProvider();
    static def anotherDifferentTagProvider = new AnotherDifferentTagProvider();

    @Unroll
    def "should list file system requirements providers first"() {
        given:
            def comparator = new PlaceFileSystemRequirementsFirst()
        when:
            def comparison = comparator.compare(a,b)
        then:
            compare(comparison, expectedComparison)
        where:
            a                       | b                         | expectedComparison
            fileSystemProvider1     | fileSystemProvider1       | EQUALS
            fileSystemProvider1     | fileSystemProvider2       | EQUALS
            fileSystemProvider1     | otherFileSystemProvider   | LESS_THAN
            differentTagProvider    | fileSystemProvider2       | GREATER_THAN
            fileSystemProvider2     | differentTagProvider      | LESS_THAN
        anotherDifferentTagProvider | differentTagProvider      | LESS_THAN

    }

    def compare(value, expectedComparison) {
        switch (expectedComparison) {
            case LESS_THAN: return value < 0
            case EQUALS: return value == 0
            case GREATER_THAN: return value > 0
        }
    }


}