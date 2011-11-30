package net.thucydides.core.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import java.util.Date;

import static net.thucydides.core.matchers.DateMatcherFormatter.formatted;


public class DateIsBetweenMatcher extends TypeSafeMatcher<Date> {

    private final DateTime startDate;
    private final DateTime endDate;

    public DateIsBetweenMatcher(final Date startDate, final Date endDate) {
        this.startDate = new DateTime(startDate);
        this.endDate = new DateTime(endDate);
    }

    public boolean matchesSafely(Date date) {
        DateTime provided = new DateTime(date);
        return (provided.isEqual(startDate) || provided.isAfter(startDate))
                && (provided.isEqual(endDate) || provided.isBefore(endDate));
    }

    public void describeTo(Description description) {
        description.appendText("a date that is between ");
        description.appendText(formatted(startDate));
        description.appendText(" and ");
        description.appendText(formatted(endDate));
    }
}
