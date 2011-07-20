package net.thucydides.core.model;

/**
 * Represents a screenshot stored during a test execution.
 */
public class Screenshot {
    private final String filename;
    private final String description;

    public Screenshot(final String filename,
                      final String description) {
        this.filename = filename;
        this.description = description;
    }


    public String getFilename() {
        return filename;
    }

    public String getDescription() {
        return description;
    }

}
