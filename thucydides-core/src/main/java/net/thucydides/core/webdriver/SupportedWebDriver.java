package net.thucydides.core.webdriver;

import com.google.common.base.Joiner;

public enum SupportedWebDriver {
    FIREFOX, CHROME;

    public static String listOfSupportedDrivers() {
        return Joiner.on(", ").join(SupportedWebDriver.values());
    }
}