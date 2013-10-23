package net.thucydides.core.requirements;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.statistics.service.FeatureStoryTagProvider;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequirementsServiceImplementation implements RequirementsService {

    private List<RequirementsTagProvider> requirementsTagProviders;
    private List<Requirement> requirements;
    private Map<Requirement, List<Requirement>> requirementAncestors;
    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final List<String> LOW_PRIORITY_PROVIDERS =
            ImmutableList.of(AnnotationBasedTagProvider.class.getCanonicalName(),
                    FeatureStoryTagProvider.class.getCanonicalName(),
                    FileSystemRequirementsTagProvider.class.getCanonicalName());

    public RequirementsServiceImplementation() {
        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = Lists.newArrayList();
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                LOGGER.info("Reading requirements from " + tagProvider);
                requirements = tagProvider.getRequirements();
                if (!requirements.isEmpty()) {
                    break;
                }
            }
            indexRequirements();
            LOGGER.info("Requirements found:" + requirements);
        }
        return requirements;
    }

    private void indexRequirements() {
        requirementAncestors = Maps.newHashMap();
        for (Requirement requirement : requirements) {
            List<Requirement> requirementPath = ImmutableList.of(requirement);
            requirementAncestors.put(requirement, ImmutableList.of(requirement));
            LOGGER.info("Requirement ancestors for:" + requirement + " = " + requirementPath);
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }

    ReleaseManager releaseManager;

    private ReleaseManager getReleaseManager() {
        if (releaseManager == null) {
            ReportNameProvider defaultNameProvider = new ReportNameProvider();
            releaseManager = new ReleaseManager(environmentVariables, defaultNameProvider);
        }
        return releaseManager;
    }


    private Map<Requirement, List<Requirement>> getRequirementAncestors() {
        if (requirementAncestors == null) {
            getRequirements();
        }
        return requirementAncestors;
    }

    private void indexChildRequirements(List<Requirement> ancestors, List<Requirement> children) {
        for (Requirement requirement : children) {
            List<Requirement> requirementPath = Lists.newArrayList(ancestors);
            requirementPath.add(requirement);
            requirementAncestors.put(requirement, ImmutableList.copyOf(requirementPath));
            LOGGER.info("Requirement ancestors for:" + requirement + " = " + requirementPath);
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }


    @Override
    public Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                Optional<Requirement> requirement = tagProvider.getParentRequirementOf(testOutcome);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return Optional.absent();
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag tag) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                Optional<Requirement> requirement = tagProvider.getRequirementFor(tag);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return Optional.absent();
    }


    @Override
    public List<Requirement> getAncestorRequirementsFor(TestOutcome testOutcome) {
        for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
            Optional<Requirement> requirement = tagProvider.getParentRequirementOf(testOutcome);
            if (requirement.isPresent()) {
                LOGGER.info("Requirement found for test outcome " + testOutcome.getTitle() + "-" + testOutcome.getIssueKeys() + ":");
                LOGGER.info("Requirement:" + requirement);
                if (getRequirementAncestors().containsKey(requirement.get())) {
                    return getRequirementAncestors().get(requirement.get());
                } else {
                    LOGGER.warn("Requirement without identified ancestors found:" + requirement.get().getCardNumber());
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getReleaseVersionsFor(TestOutcome testOutcome) {
        List<String> releases = Lists.newArrayList(testOutcome.getVersions());
        for (Requirement parentRequirement : getAncestorRequirementsFor(testOutcome)) {
            releases.addAll(parentRequirement.getReleaseVersions());
        }
        return releases;
    }

    @Override
    public List<Release> getReleasesFromRequirements() {
        List<List<String>> releaseVersions = getReleaseVersionsFrom(getRequirements());
        return getReleaseManager().extractReleasesFrom(releaseVersions);
    }

    private List<List<String>> getReleaseVersionsFrom(List<Requirement> requirements) {
        List<List<String>> releaseVersions = Lists.newArrayList();
        for (Requirement requirement : requirements) {
            releaseVersions.add(requirement.getReleaseVersions());
            releaseVersions.addAll(getReleaseVersionsFrom(requirement.getChildren()));
        }
        return releaseVersions;
    }

    private List<RequirementsTagProvider> getRequirementsTagProviders() {
        if (requirementsTagProviders == null) {
            RequirementsProviderService requirementsProviderService = Injectors.getInjector().getInstance(RequirementsProviderService.class);
            requirementsTagProviders = reprioritizeProviders(active(requirementsProviderService.getRequirementsProviders()));
        }
        return requirementsTagProviders;
    }

    private List<RequirementsTagProvider> active(List<RequirementsTagProvider> requirementsProviders) {
        boolean useDirectoryBasedRequirements =
                environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.USE_REQUIREMENTS_DIRECTORY, true);

        if (useDirectoryBasedRequirements) {
            return requirementsProviders;
        } else {
            List<RequirementsTagProvider> activeRequirementsProviders = Lists.newArrayList();
            for (RequirementsTagProvider provider : requirementsProviders) {
                if (!(provider instanceof FileSystemRequirementsTagProvider)) {
                    activeRequirementsProviders.add(provider);
                }
            }
            return activeRequirementsProviders;
        }
    }


    private List<RequirementsTagProvider> reprioritizeProviders(List<RequirementsTagProvider> requirementsTagProviders) {
        List<RequirementsTagProvider> lowPriorityProviders = Lists.newArrayList();
        List<RequirementsTagProvider> prioritizedProviders = Lists.newArrayList();

        for (RequirementsTagProvider provider : requirementsTagProviders) {
            if (LOW_PRIORITY_PROVIDERS.contains(provider.getClass().getCanonicalName())) {
                lowPriorityProviders.add(provider);
            } else {
                prioritizedProviders.add(provider);
            }
        }
        prioritizedProviders.addAll(lowPriorityProviders);
        return prioritizedProviders;
    }
}
