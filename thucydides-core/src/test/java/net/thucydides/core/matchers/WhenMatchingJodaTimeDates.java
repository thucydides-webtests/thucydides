package net.thucydides.core.matchers;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static net.thucydides.core.matchers.DateMatchers.isAfter;
import static net.thucydides.core.matchers.DateMatchers.isBefore;
import static net.thucydides.core.matchers.DateMatchers.isSameAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class WhenMatchingJodaTimeDates {

    DateTime dateTime0;
    DateTime dateTime1;
    DateTime dateTime2;

    @Before
    public void setupDates() {
        dateTime0 = new DateTime(2000,01,01,12,0);
        dateTime1 = new DateTime(2000,01,01,12,0);
        dateTime2 = new DateTime(2001,01,01,12,0);
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_equal() {

        assertThat(dateTime0, isSameAs(dateTime1));
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_not_equal() {
        assertThat(dateTime1, not(isSameAs(dateTime2)));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_before_another() {
        assertThat(dateTime1, isBefore(dateTime2));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_before_another() {
        assertThat(dateTime2, not(isBefore(dateTime1)));
    }


    @Test
    public void should_be_able_to_check_whether_a_date_is_after_another() {
        assertThat(dateTime2, isAfter(dateTime1));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_after_another() {
        assertThat(dateTime1, not(isAfter(dateTime2)));
    }

    @Test
    public void should_be_able_to_work_with_date_manipulation() {
        assertThat(dateTime1, isBefore(dateTime1.plusDays(1)));
    }

}
