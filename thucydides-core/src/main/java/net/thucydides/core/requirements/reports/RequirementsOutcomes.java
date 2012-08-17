package net.thucydides.core.requirements.reports;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.model.Requirement;
import static ch.lambdaj.Lambda.extract;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of test results for a list of high-level requirements.
 */
public class RequirementsOutcomes {
    private final List<RequirementOutcome> requirementOutcomes;
    private final TestOutcomes testOutcomes;
    private final Optional<Requirement> parentRequirement;

    public RequirementsOutcomes(List<Requirement> requirements, TestOutcomes testOutcomes, IssueTracking issueTracking) {
        this(null, requirements, testOutcomes, issueTracking);
    }

    public RequirementsOutcomes(Requirement parentRequirement, List<Requirement> requirements, TestOutcomes testOutcomes, IssueTracking issueTracking) {
        this.testOutcomes = testOutcomes;
        this.parentRequirement = Optional.fromNullable(parentRequirement);

        List<RequirementOutcome> outcomes = Lists.newArrayList();
        for(Requirement requirement : requirements) {
            TestOutcomes outcomesForRequirement = testOutcomes.withTag(requirement.getName());
            outcomes.add(new RequirementOutcome(requirement,outcomesForRequirement, issueTracking));
        }
        this.requirementOutcomes = outcomes;
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
        for(RequirementOutcome outcome : requirementOutcomes) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("requirements(\n");
        for(RequirementOutcome requirement : getRequirementOutcomes()) {
            builder.append("  requirement(name=" +requirement.getRequirement().getName()
                           + ", card=" + requirement.getRequirement().getCardNumber()
                           + ", type=" + requirement.getRequirement().getType()
                           + ", narrative=" + requirement.getRequirement().getNarrativeText() + ")");
        }
        builder.append(")");
        return "RequirementsOutcomes{" +
                "requirementOutcomes=" + requirementOutcomes +
                ", parentRequirement=" + parentRequirement +
                '}';
    }
}
