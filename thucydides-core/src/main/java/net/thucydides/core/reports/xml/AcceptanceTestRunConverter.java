package net.thucydides.core.reports.xml;

import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestStep;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AcceptanceTestRunConverter implements Converter {

    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return AcceptanceTestRun.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        AcceptanceTestRun testRun = (AcceptanceTestRun) value;
        Preconditions.checkNotNull(testRun, "The test run was null - WTF?");
        
        writer.addAttribute("title", testRun.getTitle());
        writer.addAttribute("steps", Integer.toString(testRun.getTestSteps().size()));
        writer.addAttribute("successful", Integer.toString(testRun.getSuccessCount()));
        writer.addAttribute("failures", Integer.toString(testRun.getFailureCount()));
        writer.addAttribute("skipped", Integer.toString(testRun.getSkippedCount()));
        writer.addAttribute("ignored", Integer.toString(testRun.getIgnoredCount()));
        writer.addAttribute("pending", Integer.toString(testRun.getPendingCount()));
        writer.addAttribute("result", testRun.getResult().toString());

        List<TestStep> steps = testRun.getTestSteps();
        for (TestStep step : steps) {
            writer.startNode("test-step");
            writeResult(writer, step);
            writeDescription(writer, step);
            writeScreenshotIfPresent(writer, step);
            writer.endNode();
        }
    }

    private void writeScreenshotIfPresent(HierarchicalStreamWriter writer, TestStep step) {
        if (step.getScreenshot() != null) {
            writer.startNode("screenshot");
            writer.setValue(step.getScreenshot().getName());
            writer.endNode();
        }
    }

    private void writeResult(HierarchicalStreamWriter writer, TestStep step) {
        writer.addAttribute("result", step.getResult().toString());
    }

    private void writeDescription(HierarchicalStreamWriter writer, TestStep step) {
        writer.startNode("description");
        writer.setValue(step.getDescription());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

}