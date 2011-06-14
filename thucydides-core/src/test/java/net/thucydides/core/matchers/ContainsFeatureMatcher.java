package net.thucydides.core.matchers;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.features.ApplicationFeature;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class ContainsFeatureMatcher extends TypeSafeMatcher<List<FeatureResults>> {

    private ApplicationFeature feature;

    public ContainsFeatureMatcher(ApplicationFeature feature) {
        this.feature = feature;
    }

    public boolean matchesSafely(List<FeatureResults> results) {
        for(FeatureResults result : results) {
            if (result.getFeature().equals(feature)) {
                return true;
            }
        }
        return false;
    }


    public void describeTo(Description description) {
        description.appendText("a collection of feature results containing ").appendText(feature.toString());
    }
}
