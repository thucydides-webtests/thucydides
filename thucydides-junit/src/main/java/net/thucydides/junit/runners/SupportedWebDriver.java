package net.thucydides.junit.runners;

public enum SupportedWebDriver {
    FIREFOX, CHROME;

    public static String listOfSupportedDrivers() {
        StringBuffer result = new StringBuffer();
        boolean firstEntry = true;
        for (SupportedWebDriver value: SupportedWebDriver.values()) {
            if (firstEntry) {
                firstEntry = false;
            } else {
                result.append(", ");
            }
            result.append(value);
        }
        return result.toString();
    }
}