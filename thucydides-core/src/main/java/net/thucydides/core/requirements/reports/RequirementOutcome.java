package net.thucydides.core.requirements.reports;

import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestType;
import net.thucydides.core.model.formatters.TestCoverageFormatter;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

public class RequirementOutcome {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;
    private IssueTracking issueTracking;
    private final int requirementsWithoutTests;
    private final int estimatedUnimplementedTests;

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes,
                              int requirementsWithoutTests, int estimatedUnimplementedTests,
                              IssueTracking issueTracking) {
        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
        this.requirementsWithoutTests = requirementsWithoutTests;
        this.estimatedUnimplementedTests = estimatedUnimplementedTests;
        this.issueTracking = issueTracking;
    }

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes, IssueTracking issueTracking) {
        this(requirement, testOutcomes, 0, 0, issueTracking);
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public TestOutcomes getTestOutcomes() {
        return testOutcomes;
    }

    /**
     * Is this requirement complete?
     * A Requirement is considered complete if it has associated tests to all of the tests are successful.
     */
    public boolean isComplete() {
        return getTestOutcomes().getResult() == TestResult.SUCCESS && allChildRequirementsAreSuccessful();
    }

    public boolean isFailure() {
        return getTestOutcomes().getResult() == TestResult.FAILURE || anyChildRequirementsAreFailures();
    }

    public boolean isPending() {
        return getTestOutcomes().getResult() == TestResult.PENDING || anyChildRequirementsArePending();
    }

    public int getFlattenedRequirementCount() {
        return requirement.getNestedChildren().size() + 1;
    }

    public int getRequirementsWithoutTestsCount() {
        return requirementsWithoutTests;
    }


    private boolean allChildRequirementsAreSuccessful() {
        if (requirement.hasChildren()) {
            return allChildRequirementsAreSuccessfulFor(requirement.getChildren());
        } else {
            return true;
        }
    }

    private boolean anyChildRequirementsAreFailures() {
        return anyChildRequirementsAreFailuresFor(requirement.getChildren());
    }


    private boolean anyChildRequirementsArePending() {
        return anyChildRequirementsArePendingFor(requirement.getChildren());
    }

    private boolean allChildRequirementsAreSuccessfulFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                                                                      testOutcomes.forRequirement(requirement),
                                                                      issueTracking);
            if (!childOutcomes.isComplete()) {
                return false;
            } else if (!allChildRequirementsAreSuccessfulFor(childRequirement.getChildren())) {
                return false;
            }
        }
        return true;
    }

    private boolean anyChildRequirementsAreFailuresFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isFailure()) {
                return true;
            } else if (anyChildRequirementsAreFailuresFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }

    private boolean anyChildRequirementsArePendingFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isPending()) {
                return true;
            } else if (anyChildRequirementsArePendingFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }

    public String getCardNumberWithLinks() {
        if (requirement.getCardNumber() != null) {
            return getFormatter().addLinks(requirement.getCardNumber());
        } else {
            return "";
        }
    }

    private Formatter getFormatter() {
        return new Formatter(issueTracking);
    }

    @Override
    public String toString() {
        return "RequirementOutcome{" +
                "requirement=" + requirement +
                ", testOutcomes=" + testOutcomes +
                '}';
    }

    public int getTestCount() {
        return testOutcomes.getTotal();
    }

    public int getEstimatedUnimplementedTests() {
        return estimatedUnimplementedTests;
    }

    private int totalEstimatedAndImplementedTests() {
        int totalImplementedTests = testOutcomes.getTotal();
        return totalImplementedTests + estimatedUnimplementedTests;
    }

    public RequirementsPercentageFormatter getFormattedPercentage() {
        return getFormattedPercentage(TestType.ANY);
    }

    public RequirementsPercentageFormatter getFormattedPercentage(String testType) {
        return new RequirementsPercentageFormatter(percentage(testType));
    }

    public RequirementsPercentageFormatter getFormattedPercentage(TestType testType) {
        return new RequirementsPercentageFormatter(percentage(testType));
    }

    public boolean testsRequirement(Requirement requirement) {
        return requirement.equals(getRequirement()) || testOutcomes.containsTag(requirement.asTag());
    }

    public RequirementsPercentageCounter getPercent() {
        return percentage(TestType.ANY);
    }

    public RequirementsPercentageCounter percentage(String testType) {
        return percentage(TestType.valueOf(testType.toUpperCase()));
    }

    public RequirementsPercentageCounter percentage(TestType testType) {
        return new RequirementsPercentageCounter(testType, testOutcomes, totalEstimatedAndImplementedTests());
    }

    public OutcomeCounter getTotal() {
        return new OutcomeCounter(TestType.ANY);
    }

    public OutcomeCounter count(TestType testType) {
        return new OutcomeCounter(testType);
    }

    public OutcomeCounter count(String testType) {
        return new OutcomeCounter(TestType.valueOf(testType.toUpperCase()));
    }

    public class OutcomeCounter extends TestOutcomeCounter {

        public OutcomeCounter(TestType testType) {
            super(testType);
        }

        public int withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public int withResult(TestResult expectedResult) {
            return sum(testOutcomes.getOutcomes(), on(TestOutcome.class).countResults(expectedResult, testType));
        }

        public int withIndeterminateResult() {
            return testOutcomes.getTotal() - withResult(TestResult.SUCCESS)
                                           - withResult(TestResult.FAILURE)
                                           - withResult(TestResult.ERROR);
        }
    }

}
