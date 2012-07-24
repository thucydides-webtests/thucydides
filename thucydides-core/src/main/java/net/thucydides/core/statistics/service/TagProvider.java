package net.thucydides.core.statistics.service;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.statistics.model.TestRunTag;

import java.util.List;
import java.util.Set;

public interface TagProvider {
    /**
     * Returns the tags associated with a given test outcome.
     */
    Set<TestTag> getTagsFor(final TestOutcome testOutcome);

    /**
     * Returns the list of <em>all</em> the tags for the highest level requirements.
     * These high-level requirements are often called capabilities, but not necessarily.
     * There may not always be tests associated with these high-level requirements.
     */
    List<TestTag> getCapabilityTags();
}
