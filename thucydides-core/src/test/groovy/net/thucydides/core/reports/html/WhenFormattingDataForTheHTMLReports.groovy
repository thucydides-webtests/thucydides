package net.thucydides.core.reports.html

import net.thucydides.core.issues.IssueTracking
import spock.lang.Specification
import spock.lang.Unroll

class WhenFormattingDataForTheHTMLReports extends Specification {

    IssueTracking issueTracking = Mock();

    @Unroll
    def "should display foreign characters as HTML entities"() {
        expect:
            def formatter = new net.thucydides.core.reports.html.Formatter(issueTracking);
            formatter.htmlCompatible(foreignWord) == formattedWord
        where:
            foreignWord         | formattedWord
            "François"          | "Fran&ccedil;ois"
            "störunterdrückung" | "st&ouml;runterdr&uuml;ckung"
            "CatÃ¡logo"         | "Cat&Atilde;&iexcl;logo"
    }

    @Unroll
    def "should shorten long lines if requested"() {
        expect:
            def formatter = new net.thucydides.core.reports.html.Formatter(issueTracking);
            formatter.truncatedHtmlCompatible(value, length) == formattedValue
        where:
            value                 | length |  formattedValue
            "the quick brown dog" | 3      | "the&hellip;"
            "the quick brown dog" | 10     | "the quick&hellip;"
            "the quick brown dog" | 20     | "the quick brown dog"
            "François"            | 5      | "Fran&ccedil;&hellip;"
    }
}
