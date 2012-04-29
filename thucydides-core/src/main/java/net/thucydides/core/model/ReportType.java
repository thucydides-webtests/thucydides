package net.thucydides.core.model;

/**
 * The report namer knows how to findBy names for these types of reports
 */
public enum ReportType {
    /** report name with no suffix. */
    ROOT(""),

    /** XML reports. */
    XML("xml"),

    /** HTML reports. */
    HTML("html");

    private String suffix;

    ReportType(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix;
    }
}
