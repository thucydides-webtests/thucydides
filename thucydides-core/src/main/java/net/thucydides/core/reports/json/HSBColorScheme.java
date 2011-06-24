package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import org.apache.bcel.generic.FLOAD;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import java.awt.*;
import static java.awt.Color.BLACK;


/**
 *  Determine what color boxes should be in the feature/story/scenario treemap.
 */
public class HSBColorScheme implements ColorScheme {

    private static final int MAX_COLOR_RANGE = 255;
    private final static float RED_HUE = 0.0F;
    private final static float YELLOW_HUE = 0.166666666F;
    private final static float GREEN_HUE = 0.333333333F;
    private final static float proportionOfPassingTestsAsGreenHue =  0.333333333F;
    private final static float MAX_BRIGHTNESS = 1.0F;

    /**
     * What color should a given feature be?
     */
    public Color colorFor(final FeatureResults feature) {

        return colorForResults(feature.getTotalTests(),
                               feature.getFailingTests(),
                               feature.getPassingTests(),
                               feature.getPendingTests());
    }


    public Color colorFor(StoryTestResults storyResult) {
        return colorForResults(storyResult.getTotal(),
                storyResult.getFailureCount(),
                storyResult.getSuccessCount(),
                storyResult.getPendingCount());
    }


    private Color colorForResults(final int totalTests,
                                  final int failingTests,
                                  final int passingTests,
                                  final int pendingTests) {

        if (totalTests == 0) {
            return BLACK;
        } else {
            return calculateColorFromTestCount(totalTests, failingTests, passingTests, pendingTests);
        }
    }

    private Color calculateColorFromTestCount(final int totalTests,
                                              final int failingTests,
                                              final int passingTests,
                                              final int pendingTests) {
        float hue = 0.0f;

        if (failingTests > 0) {
            hue = weightedHue(calculateHueBetween(RED_HUE, GREEN_HUE, YELLOW_HUE, passingTests, failingTests));
        } else {
            hue = calculateHueBetween(YELLOW_HUE, GREEN_HUE, YELLOW_HUE, passingTests, pendingTests);
        }

        float saturation = inverseOf(pendingToTotalTests(pendingTests, totalTests), 0.10);
        float brightness = MAX_BRIGHTNESS;

        return new Color(Color.HSBtoRGB(hue, saturation, brightness));
    }

    private float calculateHueBetween(final float startColor, final float endColor, final float zeroColor,
                                      final int mainCount, final int complementaryCount) {
        if (mainCount + complementaryCount == 0) {
            return zeroColor;
        } else {
            float totalCount = mainCount + complementaryCount;
            float deltaColor = endColor - startColor;
            return startColor + (((float) mainCount) / totalCount) * deltaColor;
            // return (float) ((passingTests * 1.0) / (failingTests + passingTests)) * proportionOfPassingTestsAsGreenHue;
        }
    }

    private float inverseOf(final double pendingToTotalTests, final double minimumValue) {
            return (float) (minimumValue + ((1 - pendingToTotalTests) * (1 - minimumValue) ));
    }

    private double pendingToTotalTests(final int pendingTests, final int totalTests) {
        return (pendingTests * 1.0) / totalTests;
    }

    private float weightedHue(final float rawHue) {
        double rawValue = rawHue / proportionOfPassingTestsAsGreenHue;
        double weightedValue = 0.0f;
        if (rawValue < 0.5) {
            weightedValue = (rawValue * 2.0/3.0);
        } else {
            weightedValue = (1.0/3.0) + (rawValue - 0.5) * (4.0/3.0);
        }
        return (float) weightedValue * proportionOfPassingTestsAsGreenHue;
    }

    public Color colorFor(final TestOutcome outcome) {
        switch(outcome.getResult()) {
            case SUCCESS:
                return Color.GREEN;
            case FAILURE:
                return Color.RED;
            case PENDING:
                return Color.YELLOW;
            case SKIPPED:
                return Color.GRAY;
            case IGNORED:
                return Color.ORANGE;
        }
        throw new IllegalArgumentException("Unsupported test outcome: " + outcome.getResult());
    }

    public Color colorFor(final TestStep step) {
        switch(step.getResult()) {
            case SUCCESS:
                return Color.GREEN;
            case FAILURE:
                return Color.RED;
            case PENDING:
                return Color.YELLOW;
            case SKIPPED:
                return Color.GRAY;
            case IGNORED:
                return Color.ORANGE;
        }
        throw new IllegalArgumentException("Unsupported test step result: " + step.getResult());
    }
}
