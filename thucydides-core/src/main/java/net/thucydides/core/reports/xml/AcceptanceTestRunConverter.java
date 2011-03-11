package net.thucydides.core.reports.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.UserStory;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter used to generate the XML acceptance test report.
 * 
 * @author johnsmart
 * 
 */
public class AcceptanceTestRunConverter implements Converter {

    /**
     * Determines which classes this converter applies to.
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return AcceptanceTestRun.class.isAssignableFrom(clazz);
    }

    /**
     * Generate an XML report given an AcceptanceTestRun object.
     */
    public void marshal(final Object value, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        AcceptanceTestRun testRun = (AcceptanceTestRun) value;
        Preconditions.checkNotNull(testRun, "The test run was null - WTF?");

        writer.addAttribute("title", testRun.getTitle());
        writer.addAttribute("name", testRun.getMethodName());
        writer.addAttribute("steps", Integer.toString(testRun.getTestSteps().size()));
        writer.addAttribute("successful", Integer.toString(testRun.getSuccessCount()));
        writer.addAttribute("failures", Integer.toString(testRun.getFailureCount()));
        writer.addAttribute("skipped", Integer.toString(testRun.getSkippedCount()));
        writer.addAttribute("ignored", Integer.toString(testRun.getIgnoredCount()));
        writer.addAttribute("pending", Integer.toString(testRun.getPendingCount()));
        writer.addAttribute("result", testRun.getResult().toString());
        addUserStoryTo(writer, testRun.getUserStory());
        addRequirementsTo(writer, testRun.getTestedRequirements());

        List<TestStep> steps = testRun.getTestSteps();
        for (TestStep step : steps) {
            writer.startNode("test-step");
            writeResult(writer, step);
            addRequirementsTo(writer, step.getTestedRequirements());
            writeDescription(writer, step);
            writeErrorForFailingTest(writer, step);
            writeScreenshotIfPresent(writer, step);
            writer.endNode();
        }

    }

    private void addUserStoryTo(final HierarchicalStreamWriter writer, final UserStory userStory) {
        if (userStory != null) {
            writer.startNode("user-story");
            writer.addAttribute("name", userStory.getName());
            writer.addAttribute("code", userStory.getCode());
            writer.addAttribute("source", userStory.getSource());
            writer.endNode();
        }
    }

    private void addRequirementsTo(final HierarchicalStreamWriter writer, final Set<String> set) {
        if (!set.isEmpty()) {
            writer.startNode("requirements");
            for (String requirement : set) {
                writer.startNode("requirement");
                writer.setValue(requirement);
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void writeErrorForFailingTest(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.isFailure()) {
            writeErrorMessageAndException(writer, step);
        }

    }

    private void writeErrorMessageAndException(final HierarchicalStreamWriter writer,
            final TestStep step) {
        if (step.getErrorMessage() != null) {
            writeErrorMessageNode(writer, step.getErrorMessage());
            if (step.getException() != null) {
                writeExceptionNode(writer, step.getException());
            }
        }
    }

    private void writeExceptionNode(final HierarchicalStreamWriter writer, final Throwable cause) {
        writer.startNode("exception");
        StringWriter stringWriter = new StringWriter();
        cause.printStackTrace(new PrintWriter(stringWriter));
        writer.setValue(stringWriter.toString());
        writer.endNode();
    }

    private void writeErrorMessageNode(final HierarchicalStreamWriter writer,
            final String errorMessage) {
        writer.startNode("error");
        writer.setValue(errorMessage);
        writer.endNode();
    }

    private void writeScreenshotIfPresent(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.getScreenshot() != null) {
            writer.startNode("screenshot");
            writer.setValue(step.getScreenshot().getName());
            writer.endNode();
        }
    }

    private void writeResult(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.addAttribute("result", step.getResult().toString());
    }

    private void writeDescription(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.startNode("description");
        writer.setValue(step.getDescription());
        writer.endNode();
    }

    /**
     * Convert XML to an AcceptanceTestRun object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {

        AcceptanceTestRun testRun = new AcceptanceTestRun();

        testRun.setTitle(reader.getAttribute("title"));
        testRun.setMethodName(reader.getAttribute("name"));
        readChildren(reader, testRun);
        return testRun;
    }

    private void readChildren(final HierarchicalStreamReader reader, final AcceptanceTestRun testRun) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals("test-step")) {
                readTestStep(reader, testRun);
            } else if (childNode.equals("requirements")) {
                readTestRunRequirements(reader, testRun);
            } else if (childNode.equals("user-story")) {
                readUserStory(reader, testRun);
            }
            reader.moveUp();
        }
    }


    private void readUserStory(final HierarchicalStreamReader reader,
                               final AcceptanceTestRun testRun) {
        String storyName = reader.getAttribute("name");
        String storyCode = reader.getAttribute("code");
        String storySource = reader.getAttribute("source");
        testRun.setUserStory(new UserStory(storyName, storyCode, storySource));
    }


    private void readTestRunRequirements(final HierarchicalStreamReader reader,
            final AcceptanceTestRun testRun) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String requirement = reader.getValue();
            testRun.testsRequirement(requirement);
            reader.moveUp();
        }
    }

    private void readTestStepRequirements(final HierarchicalStreamReader reader, final TestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String requirement = reader.getValue();
            step.testsRequirement(requirement);
            reader.moveUp();
        }
    }

    /*
     * <test-step result="SUCCESS"> <description>The customer navigates to the
     * metro masthead site.</description>
     * <screenshot>the_customer_navigates_to_the_metro_masthead_site2
     * .png</screenshot> </test-step>
     */
    private void readTestStep(final HierarchicalStreamReader reader, final AcceptanceTestRun testRun) {
        TestStep step = new TestStep();
        String testResultValue = reader.getAttribute("result");
        TestResult result = TestResult.valueOf(testResultValue);
        step.setResult(result);

        readTestStepChildren(reader, step);

        testRun.recordStep(step);
    }

    private void readTestStepChildren(final HierarchicalStreamReader reader, final TestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals("description")) {
                step.setDescription(reader.getValue());
            } else if (childNode.equals("requirements")) {
                readTestStepRequirements(reader, step);
            } else if (childNode.equals("screenshot")) {
                step.setScreenshotPath(reader.getValue());
            }
            reader.moveUp();
        }
    }
}