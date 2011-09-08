package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;

import java.awt.*;

/**
 * Color scheme to be applied to a treemap.
 */
public interface ColorScheme {
    Color colorFor(FeatureResults feature);

    Color colorFor(StoryTestResults storyResult);

    Color colorFor(TestOutcome outcome);

    Color colorFor(TestStep step);
}
