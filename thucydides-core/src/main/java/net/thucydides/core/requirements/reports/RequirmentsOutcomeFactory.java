package net.thucydides.core.requirements.reports;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.on;

public class RequirmentsOutcomeFactory {

    private final List<RequirementsTagProvider> requirementsTagProviders;
    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;

    public RequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders, IssueTracking issueTracking) {
        this(requirementsTagProviders, issueTracking, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public RequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders,
                                     IssueTracking issueTracking,
                                     EnvironmentVariables environmentVariables) {
        this.requirementsTagProviders = ImmutableList.copyOf(requirementsTagProviders);
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {
        List<Requirement> allRequirements = flatten(extract(requirementsTagProviders,
                                                            on(RequirementsTagProvider.class).getRequirements()));
        return new RequirementsOutcomes(allRequirements, testOutcomes, issueTracking, environmentVariables);
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();
        return new RequirementsOutcomes(parentRequirement, childRequirements, testOutcomes, issueTracking, environmentVariables);
    }

}
