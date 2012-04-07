package net.thucydides.core.statistics.service;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.statistics.model.TestRunTag;

import java.util.Set;

public interface TagProvider {
    Set<TestTag> getTagsFor(final TestOutcome testOutcome);
}
