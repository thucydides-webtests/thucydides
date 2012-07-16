package net.thucydides.core.capabilities;

/**
 * A capability represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what capabilities have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Capability {

    private final String name;
    private final String code;

    Capability(String name, String code) {
        this.name = name;
        this.code = code;
    }

    String getName() {
        return name;
    }

    String getCode() {
        return code;
    }
}
