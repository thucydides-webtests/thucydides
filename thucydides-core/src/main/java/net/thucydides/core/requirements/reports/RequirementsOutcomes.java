package net.thucydides.core.requirements.reports;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.CoverageFormatter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of test results for a list of high-level requirements.
 */
public class RequirementsOutcomes {
    private final List<RequirementOutcome> requirementOutcomes;
    private final TestOutcomes testOutcomes;
    private final Optional<Requirement> parentRequirement;
    private final EnvironmentVariables environmentVariables;
    private final IssueTracking issueTracking;

    public final static Integer DEFAULT_TESTS_PER_REQUIREMENT = 4;

    public RequirementsOutcomes(List<Requirement> requirements,
                                TestOutcomes testOutcomes,
                                IssueTracking issueTracking,
                                EnvironmentVariables environmentVariables) {
        this(null, requirements, testOutcomes, issueTracking, environmentVariables);
    }

    public RequirementsOutcomes(Requirement parentRequirement, List<Requirement> requirements, TestOutcomes testOutcomes,
                                IssueTracking issueTracking, EnvironmentVariables environmentVariables) {
        this.testOutcomes = testOutcomes;
        this.parentRequirement = Optional.fromNullable(parentRequirement);
        this.environmentVariables = environmentVariables;
        this.issueTracking = issueTracking;
        this.requirementOutcomes = buildRequirementOutcomes(requirements);
    }

    private List<RequirementOutcome> buildRequirementOutcomes(List<Requirement> requirements) {
        List<RequirementOutcome> outcomes = Lists.newArrayList();
        for (Requirement requirement : requirements) {
            TestOutcomes outcomesForRequirement = testOutcomes.forRequirement(requirement);
            int requirementsWithoutTests = countRequirementsWithoutTestsIn(requirement);
            int estimatedUnimplementedTests = requirementsWithoutTests * estimatedTestsPerRequirement();
            outcomes.add(new RequirementOutcome(requirement, outcomesForRequirement, requirementsWithoutTests, estimatedUnimplementedTests, issueTracking));
        }
        return outcomes;
    }

    private int countRequirementsWithoutTestsIn(Requirement rootRequirement) {
        List<Requirement> flattenedRequirements = getFlattenedRequirements(rootRequirement);

        int requirementsWithoutTests = 0;
        for(Requirement requirement : flattenedRequirements) {
            TestOutcomes matchingOutcomes = testOutcomes.withTag(requirement.asTag());
            if (matchingOutcomes.getTotal() == 0) {
                requirementsWithoutTests++;
            }
        }
        return requirementsWithoutTests;
    }

    public int getFlattenedRequirementCount() {
        int requirementCount = 0;
        for (RequirementOutcome requirement : requirementOutcomes) {
            requirementCount += requirement.getFlattenedRequirementCount();

        }
        return requirementCount;
    }

    private List<Requirement> getFlattenedRequirements(Requirement rootRequirement) {
        List<Requirement> flattenedRequirements = Lists.newArrayList();
        flattenedRequirements.add(rootRequirement);
        flattenedRequirements.addAll(rootRequirement.getNestedChildren());
        return flattenedRequirements;
    }

    public Optional<Requirement> getParentRequirement() {
        return parentRequirement;
    }

    public int getRequirementCount() {
        return requirementOutcomes.size();
    }

    public List<RequirementOutcome> getRequirementOutcomes() {
        return ImmutableList.copyOf(requirementOutcomes);
    }

    public String getType() {
        if (requirementOutcomes.isEmpty()) {
            return "requirement";
        } else {
            return requirementOutcomes.get(0).getRequirement().getType();
        }
    }

    public String getChildrenType() {
        return typeOfFirstChildPresent();
    }

    private String typeOfFirstChildPresent() {
        for (RequirementOutcome outcome : requirementOutcomes) {
            if (!outcome.getRequirement().getChildren().isEmpty()) {
                Requirement firstChildRequirement = outcome.getRequirement().getChildren().get(0);
                return firstChildRequirement.getType();
            }
        }
        return null;
    }

    public TestOutcomes getTestOutcomes() {
        return testOutcomes;
    }

    @Override
    public String toString() {
        return "RequirementsOutcomes{" +
                "requirementOutcomes=" + requirementOutcomes +
                ", parentRequirement=" + parentRequirement +
                '}';
    }

    public int getCompletedRequirementsCount() {
        int completedRequirements = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isComplete()) {
                completedRequirements++;
            }
        }
        return completedRequirements;
    }

    public int getFailingRequirementsCount() {
        int failingRequirements = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isFailure()) {
                failingRequirements++;
            }
        }
        return failingRequirements;
    }

    public int getRequirementsWithoutTestsCount() {
        int requirementsWithNoTests = 0;
        List<RequirementOutcome> flattenedRequirementOutcomes = getFlattenedRequirementOutcomes();

        for (Requirement requirement : getAllRequirements()) {
            if (!testsRecordedFor(flattenedRequirementOutcomes, requirement)) {
                requirementsWithNoTests++;
            }
        }
        return requirementsWithNoTests;
    }

    private boolean testsRecordedFor(List<RequirementOutcome> outcomes, Requirement requirement) {
        for (RequirementOutcome outcome : outcomes) {

            if (outcome.testsRequirement(requirement) && outcome.getTestCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private List<Requirement> getAllRequirements() {
        List<Requirement> allRequirements = Lists.newArrayList();
        for (RequirementOutcome outcome : requirementOutcomes) {
            addFlattenedRequirements(outcome.getRequirement(), allRequirements);
        }
        return ImmutableList.copyOf(allRequirements);
    }

    private void addFlattenedRequirements(Requirement requirement, List<Requirement> allRequirements) {
        allRequirements.add(requirement);
        for (Requirement child : requirement.getChildren()) {
            addFlattenedRequirements(child, allRequirements);
        }
    }

    List<RequirementOutcome> flattenedRequirementOutcomes = null;

    public List<RequirementOutcome> getFlattenedRequirementOutcomes() {
        if (flattenedRequirementOutcomes == null) {
            flattenedRequirementOutcomes = getFlattenedRequirementOutcomes(requirementOutcomes);
        }
        return flattenedRequirementOutcomes;
    }

    public List<RequirementOutcome> getFlattenedRequirementOutcomes(List<RequirementOutcome> outcomes) {
        List<RequirementOutcome> flattenedOutcomes = new ArrayList<RequirementOutcome>();

        for (RequirementOutcome requirementOutcome : outcomes) {
            flattenedOutcomes.add(requirementOutcome);
            Requirement requirement = requirementOutcome.getRequirement();
            if (requirement.hasChildren()) {
                for (Requirement childRequirement : requirement.getChildren()) {
                    TestOutcomes testOutcomesForChildRequirement = requirementOutcome.getTestOutcomes().withTag(childRequirement.getName());
                    List<Requirement> childRequirements = childRequirement.getChildren();
                    RequirementsOutcomes childOutcomes = new RequirementsOutcomes(childRequirement, childRequirements, testOutcomesForChildRequirement, issueTracking, environmentVariables);
                    flattenedOutcomes.addAll(getFlattenedRequirementOutcomes(childOutcomes.getRequirementOutcomes()));
                }
            }
        }

        return ImmutableList.copyOf(flattenedOutcomes);
    }

    public int getFailingTestCount() {
        return testOutcomes.getFailureCount();
    }

    public int getErrorTestCount() {
        return testOutcomes.getErrorCount();
    }

    public int getPassingTestCount() {
        return testOutcomes.getSuccessCount();
    }

    public int getTotalTestCount() {
        return testOutcomes.getTotal();
    }

    public int getPendingTestCount() {
        return testOutcomes.getPendingCount();
    }

    public int getSkippedTestCount() {
        return testOutcomes.getSkipCount();
    }

    public double getPercentagePassingTestCount() {
        return ((double) getPassingTestCount()) / ((double) totalEstimatedAndImplementedTests());
    }

    public double getPercentageFailingTestCount() {
        return ((double) getFailingTestCount()) / ((double) totalEstimatedAndImplementedTests());
    }

    public double getPercentageErrorTestCount() {
        return ((double) getErrorTestCount()) / ((double) totalEstimatedAndImplementedTests());
    }

    public double getPercentagePendingTestCount() {
        return 1 - getPercentageFailingTestCount() - getPercentagePassingTestCount() - getPercentageErrorTestCount();
    }

    /**
     * @return Formatted version of the test coverage metrics
     */
    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentagePassingTestCount(),
                getPercentagePendingTestCount(),
                getPercentageFailingTestCount(),
                getPercentageErrorTestCount());
    }

    private int totalEstimatedAndImplementedTests() {
        int totalImplementedTests = getTotalTestCount();
        return totalImplementedTests + getEstimatedUnimplementedTests();
    }

    public int getEstimatedUnimplementedTests() {
        return getRequirementsWithoutTestsCount() * estimatedTestsPerRequirement();
    }

    private int estimatedTestsPerRequirement() {
        return environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.ESTIMATED_TESTS_PER_REQUIREMENT.toString(),
                DEFAULT_TESTS_PER_REQUIREMENT);
    }
}
