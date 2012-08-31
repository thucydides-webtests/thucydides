package net.thucydides.core.requirements.reports;

import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;

public class RequirementOutcome {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;
    private IssueTracking issueTracking;

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes, IssueTracking issueTracking) {
        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
        this.issueTracking = issueTracking;
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

}
