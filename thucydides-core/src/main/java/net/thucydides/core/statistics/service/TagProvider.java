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
}
