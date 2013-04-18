package net.thucydides.core.reports.html

import com.google.common.collect.ImmutableList
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
    def "should display objects in string form"() {
        expect:
            def formatter = new net.thucydides.core.reports.html.Formatter(issueTracking);
            formatter.htmlCompatible(object) == formattedObject
        where:
            object                          | formattedObject
            [1,2,3]                         | "[1, 2, 3]"
            ["a":"1","b":2]                 | "{a=1, b=2}"
            ImmutableList.of("a","b","c")   | "[a, b, c]"
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
