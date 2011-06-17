package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;

import java.awt.*;


/**
 *  Determine what color boxes should be in the feature/story/scenario treemap.
 */
public class ColorScheme {

    private static final int MAX_COLOR_RANGE = 255;

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
            red = failingTests * MAX_COLOR_RANGE / totalTests;
            green = passingTests * MAX_COLOR_RANGE / totalTests;
            blue = pendingTests * MAX_COLOR_RANGE / totalTests;
        }

        return new Color(red, green, blue);
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
}
