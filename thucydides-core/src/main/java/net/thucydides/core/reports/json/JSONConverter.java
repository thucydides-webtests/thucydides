package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

/**
 * A description goes here.
 * User: john
 * Date: 12/05/2014
 * Time: 5:10 PM
 */
public interface JSONConverter {
    String toJson(TestOutcome testOutcome);
    TestOutcome fromJson(String jsonString);
}
