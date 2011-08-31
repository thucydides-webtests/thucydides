package net.thucydides.core.reports;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import org.apache.http.annotation.Immutable;

import java.util.List;

/**
 * Returns the data used to generate a Thucydides report.
 * Used in the Maven reporting.
 */
public class ThucydidesReportData {

    private final List<FeatureResults> featureResults;
    private final List<StoryTestResults> storyResults;

    public ThucydidesReportData(final List<FeatureResults> featureResults,
                                final List<StoryTestResults> storyResults) {
        this.featureResults = ImmutableList.copyOf(featureResults);
        this.storyResults = ImmutableList.copyOf(storyResults);
    }

    public List<FeatureResults> getFeatureResults() {
        return featureResults;
    }

    public List<StoryTestResults> getStoryResults() {
        return storyResults;
    }
}
