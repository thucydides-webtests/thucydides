package net.thucydides.core.statistics.dao;

import org.joda.time.DateTime;

public class SystemDateProvider implements DateProvider {
    @Override
    public DateTime getCurrentTime() {
        return new DateTime();
    }
}
