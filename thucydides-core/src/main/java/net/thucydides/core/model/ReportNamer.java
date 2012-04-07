package net.thucydides.core.model;

import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.Inflection;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;

import static net.thucydides.core.util.NameConverter.withNoArguments;
import static net.thucydides.core.util.NameConverter.withNoIssueNumbers;

/**
 * Determies the correct default name for test reports.
 * @author johnsmart
 *
 */
public class ReportNamer {

    public static ReportNamer forReportType(ReportType type) {
        return new ReportNamer(type);
    }

    private ReportType type;

    private ReportNamer(final ReportType type) {
        this.type = type;
    }

    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getNormalizedTestNameFor(final TestOutcome testOutcome) {
        String testName = "";
        if (testOutcome.getUserStory() != null) {
            testName = NameConverter.underscore(testOutcome.getUserStory().getName());
        }
        String scenarioName = NameConverter.underscore(testOutcome.getMethodName());
        testName = withNoIssueNumbers(withNoArguments(appendToIfNotNull(testName, scenarioName)));
        return appendSuffixTo(testName);
    }

    private String appendToIfNotNull(final String baseString, final String nextElement) {
        if (StringUtils.isNotEmpty(baseString)) {
            return baseString + "_" + nextElement;
        } else {
            return nextElement;
        }
    }

    public String getQualifiedTestNameFor(final TestOutcome testOutcome, final String qualifier) {
        String userStory = "";
        if (testOutcome.getUserStory() != null) {
            userStory = NameConverter.underscore(testOutcome.getUserStory().getName()) + "_";
        }
        String normalizedQualifier = qualifier.replaceAll(" ", "_");
        return appendSuffixTo(userStory + withNoArguments(testOutcome.getMethodName()) + "_" + normalizedQualifier);
    }

    public String getNormalizedTestNameFor(final Story userStory) {
        return getNormalizedTestNameFor(userStory.getName());
    }

    public String getNormalizedTestNameFor(final ApplicationFeature feature) {
        return getNormalizedTestNameFor(feature.getName());
    }

    public String getNormalizedTestNameFor(String name) {
        String testNameWithUnderscores = NameConverter.underscore(name);
        return appendSuffixTo(testNameWithUnderscores);
    }

    private String appendSuffixTo(final String testNameWithUnderscores) {
        if (type == ReportType.ROOT) {
            return testNameWithUnderscores;
        } else {
            return testNameWithUnderscores + "." + type.toString();
        }
    }
}
