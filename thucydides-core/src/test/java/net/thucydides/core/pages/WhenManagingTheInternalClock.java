package net.thucydides.core.pages;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class WhenManagingTheInternalClock {

    @Test
    public void should_pause_for_requested_delay() {
        InternalSystemClock clock = new InternalSystemClock();

        long startTime = System.currentTimeMillis();
        clock.pauseFor(50);
        long pauseLength = System.currentTimeMillis() - startTime;
        assertThat(pauseLength, greaterThanOrEqualTo(50L));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_something_goes_wrong() {
        InternalSystemClock clock = new InternalSystemClock() {
            @Override
            protected void sleepFor(long timeInMilliseconds) throws InterruptedException {
                throw new InterruptedException();
            }
        };

        clock.pauseFor(50);
    }

}
