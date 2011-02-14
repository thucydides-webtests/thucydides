package net.thucydides.core.reports;

import org.modeshape.common.text.Inflector;

import net.thucydides.core.model.AcceptanceTestRun;

/**
 * Determies the correct default name for test reports.
 * @author johnsmart
 *
 */
public class ReportNamer {

    /**
     * The report namer knows how to find names for thest types of reports
     */
    public enum ReportType {
        /** XML reports */
        XML("xml"), 
        
        /** HTML reports */
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

    private final Inflector inflector = Inflector.getInstance();
    
    private ReportType type;
        
    public ReportNamer(final ReportType type) {
        this.type = type;
    }
    
    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getNormalizedTestNameFor(final AcceptanceTestRun testRun) {
        String testCaseNameWithUnderscores = inflector.underscore(testRun.getTitle());
        String lowerCaseTestCaseName = testCaseNameWithUnderscores.toLowerCase();
        String lowerCaseTestCaseNameWithUnderscores = lowerCaseTestCaseName.replaceAll("\\s", "_");
        return lowerCaseTestCaseNameWithUnderscores + "." + type.toString();
    }

}
