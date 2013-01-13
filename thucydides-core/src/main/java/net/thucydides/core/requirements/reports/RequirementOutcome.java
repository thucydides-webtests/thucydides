package net.thucydides.core.requirements.reports;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.CoverageFormatter;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;

public class RequirementOutcome {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;
    private IssueTracking issueTracking;
    private final int requirementsWithoutTests;
    private final int estimatedUnimplementedTests;

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes,
                              int requirementsWithoutTests, int estimatedUnimplementedTests, IssueTracking issueTracking) {
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
                    testOutcomes.forRequirement(requirement),
                    issueTracking);
            if (childOutcomes.isFailure()) {
                return true;
            } else if (anyChildRequirementsAreFailuresFor(childRequirement.getChildren())) {
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

    public double getPercentagePassingTestCount() {
        return ((double) getPassingTestCount()) / ((double) totalEstimatedAndImplementedTests());
    }

    public int getTestCount() {
        return testOutcomes.getTotal();
    }

    public int getPassingTestCount() {
        return testOutcomes.getSuccessCount();
    }

    public double getPercentageFailingTestCount() {
        return ((double) getFailingTestCount()) / ((double) totalEstimatedAndImplementedTests());
    }

    public double getPercentagePendingStepCount() {
        return 1 - getPercentageFailingTestCount() - getPercentagePassingTestCount();
    }

    public int getEstimatedUnimplementedTests() {
        return estimatedUnimplementedTests;
    }

    private int totalEstimatedAndImplementedTests() {
        int totalImplementedTests = testOutcomes.getTotal();
        return totalImplementedTests + estimatedUnimplementedTests;
    }

    public int getFailingTestCount() {
        return testOutcomes.getFailureCount();
    }

    public int getPendingTestCount() {
        return testOutcomes.getPendingCount();
    }

    public double getPercentagePendingTestCount() {
        return 1 - getPercentageFailingTestCount() - getPercentagePassingTestCount();
    }

    /**
     * @return Formatted version of the test coverage metrics
     */
    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentagePassingTestCount(),
                                     getPercentagePendingTestCount(),
                                     getPercentageFailingTestCount());
    }

    public boolean testsRequirement(Requirement requirement) {
        return requirement.equals(getRequirement()) || testOutcomes.containsTag(requirement.asTag());
    }
}
