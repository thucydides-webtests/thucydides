package net.thucydides.core.model

import spock.lang.Specification

class WhenFormattingShortenedErrorMessages extends Specification {

    def "should shorten multi-line error messages to one line"() {
        expect:
           shortenedErrorMessage == new ErrorMessageFormatter(fullErrorMessage).shortErrorMessage
        where:
            fullErrorMessage              | shortenedErrorMessage
            "A short error message"       | "A short error message"
            "A multiline\nerror\nmessage" | "A multiline"
            "A multiline\r\nerror\r\nmessage" | "A multiline"
            ""                            | ""
            null                          | ""
    }

    def "should strip exceptions from the start of the message"() {
        expect:
            shortenedErrorMessage == new ErrorMessageFormatter(fullErrorMessage).shortErrorMessage
        where:
            fullErrorMessage                                | shortenedErrorMessage
            "java.lang.AssertionError: oops!"               | "oops!"
            "java.lang.AssertionError:\noops!"              | "oops!"
            "java.lang.AssertionError:\r\noops!"            | "oops!"
            "java.lang.AssertionError:\noops!\nMore stuff"  | "oops!"
    }
}
