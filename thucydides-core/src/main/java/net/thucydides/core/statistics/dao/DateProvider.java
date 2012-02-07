package net.thucydides.core.statistics.dao;

import org.joda.time.DateTime;

public interface DateProvider {
    DateTime getCurrentTime();
}
