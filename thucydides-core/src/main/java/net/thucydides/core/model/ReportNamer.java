package net.thucydides.core.model;

import static net.thucydides.core.util.NameConverter.withNoArguments;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.NameConverter;

import org.apache.commons.lang.StringUtils;

/**
 * Determies the correct default name for test reports.
 * @author johnsmart
 *
 */
public class ReportNamer {

    /**
     * The report namer knows how to find names for these types of reports
     */
    public enum ReportType {
        /** report name with no suffix. */
        ROOT(""),
        
        /** XML reports. */
        XML("xml"), 
        
        /** HTML reports. */
        HTML("html");
        
        private String suffix;
        
        private ReportType(final String suffix) {
            this.suffix = suffix;
        }
        
        @Override
        public String toString() {
            return suffix;
        }
    }
    
    private ReportType type;
        
    public ReportNamer(final ReportType type) {
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
        testName = withNoArguments(appendToIfNotNull(testName, scenarioName));
        return appendSuffixTo(testName);
    }

    private String appendToIfNotNull(final String baseString, final String nextElement) {
        String appendedString = baseString;
        if (StringUtils.isNotEmpty(nextElement)) {
            if (StringUtils.isNotEmpty(baseString)) {
                appendedString = baseString + "_" + nextElement;
            } else {
                appendedString = nextElement;
            }
        } else {
            appendedString = baseString;
        }

        return appendedString;
    }

    public String getNormalizedTestNameFor(final TestOutcome testOutcome, final String qualifier) {
        String userStory = "";
        if (testOutcome.getUserStory() != null) {
            userStory = NameConverter.underscore(testOutcome.getUserStory().getName()) + "_";
        }
        String normalizedQualifier = qualifier.replaceAll(" ", "_");
        return appendSuffixTo(userStory + withNoArguments(testOutcome.getMethodName()) + "_" + normalizedQualifier);
    }

    public String getNormalizedTestNameFor(final Story userStory) {
        String testNameWithUnderscores = NameConverter.underscore(userStory.getName());
        return appendSuffixTo(testNameWithUnderscores);
    }

    public String getNormalizedTestNameFor(final ApplicationFeature feature) {
        String testNameWithUnderscores = NameConverter.underscore(feature.getName());
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
