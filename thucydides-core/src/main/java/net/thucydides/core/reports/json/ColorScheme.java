package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;

import java.awt.*;

/**
 *
 */
public class ColorScheme {
    public Color colorFor(final FeatureResults feature) {


        int red = 0;
        int green = 0;
        int blue = 0;
        if (feature.getTotalTests() > 0) {
            red = feature.getFailingTests() * 255 / feature.getTotalTests();
            green = feature.getPassingTests() * 255 / feature.getTotalTests();
        }

        return new Color(red, green, blue);
    }


    /**
     * Utility method to format a color to HTML RGB color format (e.g. #FF0000 for Color.red).
     * @param color The color.
     * @return the HTML RGB color string.
     */
    public static final String rgbFormatOf(final Color color) {
        String r = (color.getRed() < 16) ? "0" + Integer.toHexString(color.getRed()) : Integer.toHexString(color.getRed());
        String g = (color.getGreen() < 16) ? "0" + Integer.toHexString(color.getGreen()) : Integer.toHexString(color.getGreen());
        String b = (color.getBlue() < 16) ? "0" + Integer.toHexString(color.getBlue()) : Integer.toHexString(color.getBlue());
        return "#" + r + g + b;
    }

}
