package net.thucydides.core.util;

import java.util.Formatter;

public class StringConstants {
    public static final String NEWLINE;

    static {
        String newLine = null;

        try {
            newLine = new Formatter().format("%n").toString();
        } catch (Exception e) {
            newLine = "\n";
        }

        NEWLINE = newLine;
    }
}
