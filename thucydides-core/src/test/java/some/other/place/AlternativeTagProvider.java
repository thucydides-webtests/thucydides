package some.other.place;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.statistics.service.TagProvider;

import java.util.List;
import java.util.Set;

public class AlternativeTagProvider implements TagProvider {
    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TestTag> getCapabilityTags() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
