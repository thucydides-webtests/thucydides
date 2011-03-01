package net.thucydides.core.reports;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.AggregateTestResults;
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
    
    private ReportType type;
        
    public ReportNamer(final ReportType type) {
        this.type = type;
    }
    
    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getNormalizedTestNameFor(final AcceptanceTestRun testRun) {
        return testRun.getMethodName() + "." + type.toString();
    }

    public String getNormalizedTestNameFor(final AggregateTestResults testResults) {
        String testNameWithUnderscores = NameConverter.underscore(testResults.getTitle());
        return testNameWithUnderscores + "." + type.toString();
    }

}
