package net.thucydides.core.capabilities;

import net.thucydides.core.capabilities.model.Capability;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;

import java.util.List;

public interface CapabilityProvider {
    List<Capability> getCapabilities();

    List<TestTag> getTagsFor(final TestOutcome testOutcome);
}
