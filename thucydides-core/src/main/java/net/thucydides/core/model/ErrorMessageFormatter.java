package net.thucydides.core.model;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a full WebDriver message into a shorter, more web-friendly format.
 */
public class ErrorMessageFormatter {
    private final String originalMessage;

    Pattern LEADING_EXCEPTIONS = Pattern.compile("^<?[\\w\\.]*:\\s");

    public ErrorMessageFormatter(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    /**
     * Returns the first line only of the error message.
     * This avoids polluting the UI with unnecessary details such as browser versions and so forth.
     *
     * @return
     */
    public String getShortErrorMessage() {
        String lines[] = originalMessage.split("\\r?\\n");
        return StringUtils.trimToEmpty(replaceDoubleQuotes((removeLeadingExceptionFrom(lines[0]))));
    }

    private String removeLeadingExceptionFrom(final String message) {
        Matcher matcher = LEADING_EXCEPTIONS.matcher(message);
        if (matcher.find()) {
            return matcher.replaceFirst("");
        } else {
            return message;
        }
    }

    private String replaceDoubleQuotes(final String message) {
        return message.replaceAll("\"","'");
    }
}
