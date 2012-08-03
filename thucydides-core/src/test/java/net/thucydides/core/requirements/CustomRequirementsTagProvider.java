package net.thucydides.core.requirements;

import com.google.common.collect.Lists;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.TestOutcome;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CustomRequirementsTagProvider implements RequirementsTagProvider {
    @Override
    public List<Requirement> getRequirements() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return Collections.EMPTY_SET;
    }
}
