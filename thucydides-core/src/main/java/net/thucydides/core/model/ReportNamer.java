package net.thucydides.core.model;

import net.thucydides.core.util.NameConverter;

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
    public String getNormalizedTestNameFor(final AcceptanceTestRun testRun) {
        String userStory = "";
        if (testRun.getUserStory() != null) {
            userStory = NameConverter.underscore(testRun.getUserStory().getName()) + "_";
        }
        return appendSuffixTo(userStory + testRun.getMethodName());
    }

    public String getNormalizedTestNameFor(final AcceptanceTestRun testRun, final String qualifier) {
        String userStory = "";
        if (testRun.getUserStory() != null) {
            userStory = NameConverter.underscore(testRun.getUserStory().getName()) + "_";
        }
        String normalizedQualifier = qualifier.replaceAll(" ", "_");
        return appendSuffixTo(userStory + testRun.getMethodName() + "_" + normalizedQualifier);
    }

    public String getNormalizedTestNameFor(final UserStory userStory) {
        String testNameWithUnderscores = NameConverter.underscore(userStory.getName());
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
