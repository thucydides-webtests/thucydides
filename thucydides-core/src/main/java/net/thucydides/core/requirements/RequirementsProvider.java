package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;

import java.util.List;

public interface RequirementsProvider {
    List<Requirement> getRequirements();

    List<TestTag> getTagsFor(final TestOutcome testOutcome);
}
