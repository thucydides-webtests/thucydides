package net.thucydides.core.matchers;

import org.joda.time.DateTime;

import java.util.Date;

class DateMatcherFormatter {

    public static String formatted(DateTime dateTime) {
        return dateTime.toString("d MMM yyyy HH:mm:ss");
    }

}
