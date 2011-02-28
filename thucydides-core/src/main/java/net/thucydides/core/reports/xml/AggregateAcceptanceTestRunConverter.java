package net.thucydides.core.reports.xml;

import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.AggregateTestResults;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter used to generate the aggregate test report for a user story.
 * 
 * @author johnsmart
 * 
 */
public class AggregateAcceptanceTestRunConverter implements Converter {

    /**
     * Determines which classes this converter applies to.
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return AggregateTestResults.class.isAssignableFrom(clazz);
    }

    /**
     * Generate an XML report given an AcceptanceTestRun object.
     */
    public void marshal(final Object value, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        AggregateTestResults testResults = (AggregateTestResults) value;
        Preconditions.checkNotNull(testResults, "The test results were null - WTF?");

        writer.addAttribute("title", testResults.getTitle());
        writer.addAttribute("total", Integer.toString(testResults.getTotal()));
        writer.addAttribute("successful", Integer.toString(testResults.getSuccessCount()));
        writer.addAttribute("failures", Integer.toString(testResults.getFailureCount()));
        writer.addAttribute("pending", Integer.toString(testResults.getPendingCount()));
        writer.addAttribute("result", testResults.getResult().toString());
        List<AcceptanceTestRun> testRuns = testResults.getTestRuns();
        for (AcceptanceTestRun testRun : testRuns) {
            writer.startNode("acceptance-test");
            writer.addAttribute("title", testRun.getTitle());
            writer.addAttribute("name", testRun.getMethodName());
            writer.addAttribute("steps", Integer.toString(testRun.getStepCount()));
            writer.addAttribute("failures", Integer.toString(testRun.getFailureCount()));
            writer.addAttribute("pending", Integer.toString(testRun.getPendingCount()));
            writer.addAttribute("skipped", Integer.toString(testRun.getSkippedCount()));
            writer.endNode();
        }
    }

    /**
     * Convert XML to an AcceptanceTestRun object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {

        throw new NotImplementedException();
    }
}