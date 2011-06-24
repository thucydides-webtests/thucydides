package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 23/06/11
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ColorScheme {
    Color colorFor(FeatureResults feature);

    Color colorFor(StoryTestResults storyResult);

    Color colorFor(TestOutcome outcome);

    Color colorFor(TestStep step);
}
