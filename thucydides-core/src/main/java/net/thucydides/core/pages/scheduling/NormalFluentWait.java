package net.thucydides.core.pages.scheduling;

import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Sleeper;

public class NormalFluentWait<T> extends ThucydidesFluentWait<T> {


    public NormalFluentWait(T input, Clock clock, Sleeper sleeper) {
        super(input, clock, sleeper);
    }

    @Override
    public void doWait() throws InterruptedException {
        getSleeper().sleep(interval);
    }
}