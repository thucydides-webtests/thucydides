package net.thucydides.core.statistics.service;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.model.TestRunTag;

import java.util.Set;

public interface TagProvider {
    Set<TestRunTag> getTagsFor(final TestOutcome testOutcome);
}
