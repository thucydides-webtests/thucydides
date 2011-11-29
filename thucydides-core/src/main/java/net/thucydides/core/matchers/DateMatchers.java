package net.thucydides.core.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Hamcrest matchers to be used with Dates.
 */
public class DateMatchers {

    @Factory
    public static Matcher<Date> isSameAs(Date expectedDate){
        return new DateIsSameAsMatcher(expectedDate);
    }

    @Factory
    public static Matcher<Date> isBefore(Date expectedDate){
        return new DateIsBeforeMatcher(expectedDate);
    }

    @Factory
    public static Matcher<Date> isAfter(Date expectedDate){
        return new DateIsAfterMatcher(expectedDate);
    }

    @Factory
    public static Matcher<DateTime> isSameAs(DateTime expectedDate){
        return new DateTimeIsSameAsMatcher(expectedDate);
    }

    @Factory
    public static Matcher<DateTime> isBefore(DateTime expectedDate){
        return new DateTimeIsBeforeMatcher(expectedDate);
    }

    @Factory
    public static Matcher<DateTime> isAfter(DateTime expectedDate){
        return new DateTimeIsAfterMatcher(expectedDate);
    }

}
