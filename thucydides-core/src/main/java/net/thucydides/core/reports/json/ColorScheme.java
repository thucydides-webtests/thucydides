package net.thucydides.core.reports.json;

import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.TestOutcomes;

import java.awt.*;

/**
 * Color scheme to be applied to a treemap.
 */
public interface ColorScheme {

    Color colorFor(TestOutcomes outcome);

    Color colorFor(TestOutcome outcome);

    Color colorFor(TestStep step);
}
