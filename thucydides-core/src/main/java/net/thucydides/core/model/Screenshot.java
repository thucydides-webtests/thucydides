package net.thucydides.core.model;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Represents a screenshot stored during a test execution.
 */
public class Screenshot {
    private final String filename;
    private final String description;
    private final int width;
    private final Throwable error;

    public Screenshot(final String filename,
                      final String description,
                      final int width,
                      final Throwable error) {
        this.filename = filename;
        this.description = description;
        this.width = width;
        this.error = error;
    }

    public Screenshot(final String filename,
                      final String description,
                      final int width) {
        this(filename, description, width, null);
    }

    public Throwable getError() {
        return error;
    }

    public String getErrorMessage() {
        return (error != null) ? errorMessageFrom(error) : "";
    }

    private String errorMessageFrom(final Throwable error) {
        return (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage();
    }

    /**
     * Returns the first line only of the error message.
     * This avoids polluting the UI with unnecessary details such as browser versions and so forth.
     * @return
     */
    public String getShortErrorMessage() {
        return new ErrorMessageFormatter(getErrorMessage()).getShortErrorMessage();
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

    public HtmlFormattedInfo getHtml() {
        return new HtmlFormattedInfo(description);
    }

    public class HtmlFormattedInfo {
        private final String description;

        public HtmlFormattedInfo(String description) {
            this.description = description;
        }

        public String getDescription() {
            return StringEscapeUtils.escapeHtml(description);
        }
    }
}
