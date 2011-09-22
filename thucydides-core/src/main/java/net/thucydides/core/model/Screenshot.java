package net.thucydides.core.model;

/**
 * Represents a screenshot stored during a test execution.
 */
public class Screenshot {
    private final String filename;
    private final String description;
    private final int width;

    public Screenshot(final String filename,
                      final String description,
                      final int width) {
        this.filename = filename;
        this.description = description;
        this.width = width;
    }


    public String getFilename() {
        return filename;
    }

    public String getDescription() {
        return description;
    }

    public int getWidth() {
        return width;
    }

}
