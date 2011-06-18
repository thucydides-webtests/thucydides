package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;

import java.awt.*;


/**
 *  Determine what color boxes should be in the feature/story/scenario treemap.
 */
public class ColorScheme {

    private static final int MAX_COLOR_RANGE = 255;
    private static final double FAILURE_EXTRA_WEIGHT = 0.5;
    private static final double SUCCESS_EXTRA_WEIGHT = 0.0;
    private static final double PENDING_EXTRA_WEIGHT = 0.0;

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
        int red = 0;
        int green = 0;
        int blue = 0;
        if (totalTests > 0) {
            red = weightedValue(failingTests * MAX_COLOR_RANGE / totalTests, FAILURE_EXTRA_WEIGHT);
            green = weightedValue(passingTests * MAX_COLOR_RANGE / totalTests, SUCCESS_EXTRA_WEIGHT);
            blue = weightedValue(pendingTests * MAX_COLOR_RANGE / totalTests, PENDING_EXTRA_WEIGHT);
        }

        return new Color(red, green, blue);
    }

    private int weightedValue(final int value, double extraWeight) {
        if ((value > 0) && (extraWeight > 0.0)) {
            int minimumValue = (int) (1 + MAX_COLOR_RANGE * (1 - extraWeight));
            int delta = (int) (value * extraWeight);
            return minimumValue + delta;
        } else {
            return value;
        }
    }

    /**
     * Utility method to format a color to HTML RGB color format (e.g. #FF0000 for Color.red).
     * @param color The color.
     * @return the HTML RGB color string.
     */
    public static String rgbFormatOf(final Color color) {
        String redByte = convertToByte(color.getRed());
        String greenByte = convertToByte(color.getGreen());
        String blueByte = convertToByte(color.getBlue());
        return "#" + redByte + greenByte + blueByte;
    }

    private static String convertToByte(final int colorByte) {
        if (colorByte < 16) {
            return "0" + Integer.toHexString(colorByte);
        } else {
            return Integer.toHexString(colorByte);
        }
    }

    public Color colorFor(final TestOutcome outcome) {
        switch(outcome.getResult()) {
            case SUCCESS:
                return Color.GREEN;
            case FAILURE:
                return Color.RED;
            case PENDING:
                return Color.BLUE;
            case SKIPPED:
                return Color.GRAY;
            case IGNORED:
                return Color.ORANGE;
        }
        throw new IllegalArgumentException("Unsupported test outcome: " + outcome.getResult());
    }
}
