package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;

import java.awt.*;


/**
 *  Determine what color boxes should be in the feature/story/scenario treemap.
 */
public class ProgressColorScheme implements ColorScheme {

    public static final Color PALE_BLUE = new Color(230,240,255);
    public static final Color DONE_BLUE = new Color(0,35,255);

    private final static float FIXED_HUE = 0.6F;
    private final static float FIXED_BRIGHTNESS = 1.0F;

    private static final float MINIMUM_SATURATION = 0.1f;
    private static final float MEDIUM_SATURATION  = 0.6f;
    private static final float MAXIMUM_SATURATION = 1.0f;

    /**
     * What color should a given feature be?
     */
    public Color colorFor(final FeatureResults feature) {

        return colorForResults(feature.getTotalSteps(), feature.countStepsInSuccessfulTests());
    }


    public Color colorFor(StoryTestResults storyResult) {
        return colorForResults(storyResult.getStepCount(), storyResult.countStepsInSuccessfulTests());
    }


    private Color colorForResults(final int totalTestSteps,
                                  final int totalStepsInPassingTests) {

        if (totalStepsInPassingTests == totalTestSteps) {
            return DONE_BLUE;
        } else {
            return calculateColorFromTestCount(totalTestSteps, totalStepsInPassingTests);
        }
    }

    private Color calculateColorFromTestCount(final int totalTestSteps,
                                              final int totalStepsInPassingTests) {
        float saturation = calculateValueBetween(MINIMUM_SATURATION, MAXIMUM_SATURATION, MEDIUM_SATURATION,
                                                totalStepsInPassingTests,
                                                totalTestSteps - totalStepsInPassingTests);

        return new Color(Color.HSBtoRGB(FIXED_HUE, saturation, FIXED_BRIGHTNESS));
    }

    public Color colorFor(final TestOutcome outcome) {
        switch(outcome.getResult()) {
            case SUCCESS:
                return DONE_BLUE;
            default:
                return PALE_BLUE;
        }
    }

    public Color colorFor(final TestStep step) {
        switch(step.getResult()) {
            case SUCCESS:
                return DONE_BLUE;
            default:
                return PALE_BLUE;
        }
    }

    private float calculateValueBetween(final float startColor, final float endColor, final float zeroColor,
                                        final int mainCount, final int complementaryCount) {
        if (mainCount + complementaryCount == 0) {
            return zeroColor;
        } else {
            float totalCount = mainCount + complementaryCount;
            float deltaColor = endColor - startColor;
            return startColor + (((float) mainCount) / totalCount) * deltaColor;
        }
    }

}
