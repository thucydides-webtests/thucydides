package net.thucydides.core.model;

import org.openqa.jetty.util.StringUtil;

import java.text.DecimalFormat;

public class NumericalFormatter {
    public String rounded(final Double value, final int precision) {
        DecimalFormat format = decimalFormatWithPrecision(precision);

        return format.format(value);
    }

    public String percentage(final Double value, final int precision) {
        DecimalFormat format = percentageFormatWithPrecision(precision);

        return format.format(value);
    }

    private DecimalFormat decimalFormatWithPrecision(final int precision) {
        StringBuffer format = new StringBuffer("#");
        if (precision > 0) {
            format.append(".");
            for(int i = 0; i < precision; i++) {
                format.append("#");
            }
        }
        return new DecimalFormat(format.toString());
    }

    private DecimalFormat percentageFormatWithPrecision(final int precision) {
        StringBuffer format = new StringBuffer("#");
        if (precision > 0) {
            format.append(".");
            for(int i = 0; i < precision; i++) {
                format.append("#");
            }
        }
        format.append("%");
        return new DecimalFormat(format.toString());
    }

}
