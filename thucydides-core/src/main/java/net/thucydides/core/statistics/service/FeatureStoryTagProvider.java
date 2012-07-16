package net.thucydides.core.statistics.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.features.ApplicationFeature;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Legacy tag provider that builds tags based on the Feature/Story structure, if the @WithTag annotation is not used.
 * If the @WithTag annotation is used, @Feature classes will not be used.
 */
public class FeatureStoryTagProvider implements TagProvider {

    public FeatureStoryTagProvider() {
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        Set<TestTag> tags = Sets.newHashSet();
        addStoryTagIfPresent(testOutcome, tags);
        addFeatureTagIfPresent(testOutcome, tags);
        return ImmutableSet.copyOf(tags);
    }

    @Override
    public List<TestTag> getCapabilityTags() {
        return Collections.EMPTY_LIST;
    }

    private void addStoryTagIfPresent(TestOutcome testOutcome, Set<TestTag> tags) {
        Story story = testOutcome.getUserStory();
        if (story != null) {
            tags.add(TestTag.withName(story.getName()).andType("story"));
        }
    }

    private void addFeatureTagIfPresent(TestOutcome testOutcome, Set<TestTag> tags) {
        ApplicationFeature feature = testOutcome.getFeature();
        if (feature != null) {
            tags.add(TestTag.withName(feature.getName()).andType("feature"));
        }
    }
}
