package net.thucydides.core.steps;

import net.thucydides.core.webdriver.WebdriverAssertionError;

import java.io.Serializable;

public class ErrorConvertor implements Serializable {

    private final Throwable throwable;
    public static ErrorConvertor forError(final Throwable throwable) {
        return new ErrorConvertor(throwable);
    }

    protected ErrorConvertor(Throwable throwable) {
        this.throwable = throwable;
    }

    public AssertionError convertToAssertion() {
        return new WebdriverAssertionError(messageFrom(throwable), throwable);
    }

    public String messageFrom(final Throwable error) {
        String message = (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage();
        if (message != null) {
            return message;
        } else {
            return error.toString();
        }
    }
}