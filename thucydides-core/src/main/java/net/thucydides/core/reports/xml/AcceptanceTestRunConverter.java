package net.thucydides.core.reports.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
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

    private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String STEPS_FIELD = "steps";
    private static final String SUCCESSFUL_FIELD = "successful";
    private static final String FAILURES_FIELD = "failures";
    private static final String SKIPPED_FIELD = "skipped";
    private static final String IGNORED_FIELD = "ignored";
    private static final String PENDING_FIELD = "pending";
    private static final String RESULT_FIELD = "result";
    private static final String TEST_GROUP = "test-group";
    private static final String TEST_STEP = "test-step";
    private static final String USER_STORY = "user-story";
    private static final String CODE_FIELD = "code";
    private static final String SOURCE_FIELD = "source";
    private static final String REQUIREMENTS = "requirements";
    private static final String REQUIREMENT = "requirement";
    private static final String EXCEPTION = "exception";
    private static final String ERROR = "error";
    private static final String SCREENSHOT = "screenshot";
    private static final String DESCRIPTION = "description";

    private transient String qualifier;

    public AcceptanceTestRunConverter() {}

    public AcceptanceTestRunConverter(final String qualifier) {
        this();
        this.qualifier = qualifier;
    }

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

        writer.addAttribute(TITLE_FIELD, titleFrom(testRun));
        writer.addAttribute(NAME_FIELD, nameFrom(testRun));
        writer.addAttribute(STEPS_FIELD, Integer.toString(testRun.countTestSteps()));
        writer.addAttribute(SUCCESSFUL_FIELD, Integer.toString(testRun.getSuccessCount()));
        writer.addAttribute(FAILURES_FIELD, Integer.toString(testRun.getFailureCount()));
        writer.addAttribute(SKIPPED_FIELD, Integer.toString(testRun.getSkippedCount()));
        writer.addAttribute(IGNORED_FIELD, Integer.toString(testRun.getIgnoredCount()));
        writer.addAttribute(PENDING_FIELD, Integer.toString(testRun.getPendingCount()));
        writer.addAttribute(RESULT_FIELD, testRun.getResult().toString());
        addUserStoryTo(writer, testRun.getUserStory());
        addRequirementsTo(writer, testRun.getTestedRequirements());

        List<TestStep> steps = testRun.getTestSteps();
        for (TestStep step : steps) {
            writeStepTo(writer, step);
        }
    }


    private String titleFrom(final AcceptanceTestRun testRun) {
        if (qualifier == null) {
            return testRun.getTitle();
        } else {
            return testRun.getTitle() + " [" + humanized(qualifier) + "]";
        }
    }

    private String humanized(final String qualifier) {
        return qualifier.replaceAll("_","/");
    }

    private String nameFrom(final AcceptanceTestRun testRun) {
        if (qualifier == null) {
            return testRun.getMethodName();
        } else {
            String qualifier_without_spaces = qualifier.replaceAll(" ","_");
            return testRun.getMethodName() + "_" + qualifier_without_spaces;
        }
    }


    private void writeStepTo(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step instanceof TestStepGroup) {
            writer.startNode(TEST_GROUP);
            writer.addAttribute(NAME_FIELD, step.getDescription());
            List<TestStep> nestedSteps = ((TestStepGroup) step).getSteps();
            for(TestStep nestedStep : nestedSteps) {
                writeStepTo(writer, nestedStep);
            }
            writer.endNode();
        } else {
            ConcreteTestStep concreteStep = (ConcreteTestStep) step;
            writer.startNode(TEST_STEP);
            writeResult(writer, concreteStep);
            addRequirementsTo(writer, step.getTestedRequirements());
            writeDescription(writer, concreteStep);
            writeErrorForFailingTest(writer, concreteStep);
            writeScreenshotIfPresent(writer, concreteStep);
            writer.endNode();
        }
    }

    private void addUserStoryTo(final HierarchicalStreamWriter writer, final UserStory userStory) {
        if (userStory != null) {
            writer.startNode(USER_STORY);
            writer.addAttribute(NAME_FIELD, userStory.getName());
            writer.addAttribute(CODE_FIELD, userStory.getCode());
            writer.addAttribute(SOURCE_FIELD, userStory.getSource());
            writer.endNode();
        }
    }

    private void addRequirementsTo(final HierarchicalStreamWriter writer, final Set<String> set) {
        if (!set.isEmpty()) {
            writer.startNode(REQUIREMENTS);
            for (String requirement : set) {
                writer.startNode(REQUIREMENT);
                writer.setValue(requirement);
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void writeErrorForFailingTest(final HierarchicalStreamWriter writer, final ConcreteTestStep step) {
        if (step.isFailure()) {
            writeErrorMessageAndException(writer, step);
        }

    }

    private void writeErrorMessageAndException(final HierarchicalStreamWriter writer,
            final ConcreteTestStep step) {
        if (step.getErrorMessage() != null) {
            writeErrorMessageNode(writer, step.getErrorMessage());
            if (step.getException() != null) {
                writeExceptionNode(writer, step.getException());
            }
        }
    }

    private void writeExceptionNode(final HierarchicalStreamWriter writer, final Throwable cause) {
        writer.startNode(EXCEPTION);
        StringWriter stringWriter = new StringWriter();
        cause.printStackTrace(new PrintWriter(stringWriter));
        writer.setValue(stringWriter.toString());
        writer.endNode();
    }

    private void writeErrorMessageNode(final HierarchicalStreamWriter writer,
            final String errorMessage) {
        writer.startNode(ERROR);
        writer.setValue(errorMessage);
        writer.endNode();
    }

    private void writeScreenshotIfPresent(final HierarchicalStreamWriter writer, final ConcreteTestStep step) {
        if (step.getScreenshot() != null) {
            writer.startNode(SCREENSHOT);
            writer.setValue(step.getScreenshot().getName());
            writer.endNode();
        }
    }

    private void writeResult(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.addAttribute(RESULT_FIELD, step.getResult().toString());
    }

    private void writeDescription(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.startNode(DESCRIPTION);
        writer.setValue(step.getDescription());
        writer.endNode();
    }

    /**
     * Convert XML to an AcceptanceTestRun object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {

        AcceptanceTestRun testRun = new AcceptanceTestRun();

        testRun.setTitle(reader.getAttribute(TITLE_FIELD));
        testRun.setMethodName(reader.getAttribute(NAME_FIELD));
        readChildren(reader, testRun);
        return testRun;
    }

    private void readChildren(final HierarchicalStreamReader reader, final AcceptanceTestRun testRun) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(TEST_STEP)) {
                readTestStep(reader, testRun);
            } else if (childNode.equals(TEST_GROUP)) {
                readTestGroup(reader, testRun);
            } else if (childNode.equals(REQUIREMENTS)) {
                readTestRunRequirements(reader, testRun);
            } else if (childNode.equals(USER_STORY)) {
                readUserStory(reader, testRun);
            }
            reader.moveUp();
        }
    }


    private void readUserStory(final HierarchicalStreamReader reader,
                               final AcceptanceTestRun testRun) {
        String storyName = reader.getAttribute(NAME_FIELD);
        String storyCode = reader.getAttribute(CODE_FIELD);
        String storySource = reader.getAttribute(SOURCE_FIELD);
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
        ConcreteTestStep step = new ConcreteTestStep();
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        step.setResult(result);

        readTestStepChildren(reader, step);

        testRun.recordStep(step);
    }

    private void readTestGroup(final HierarchicalStreamReader reader, final AcceptanceTestRun testRun) {
        String name = reader.getAttribute(NAME_FIELD);
        testRun.startGroup(name);
        readChildren(reader, testRun);
        testRun.endGroup();
    }

    private void readTestStepChildren(final HierarchicalStreamReader reader, final ConcreteTestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(DESCRIPTION)) {
                step.setDescription(reader.getValue());
            } else if (childNode.equals(REQUIREMENTS)) {
                readTestStepRequirements(reader, step);
            } else if (childNode.equals(SCREENSHOT)) {
                step.setScreenshotPath(reader.getValue());
            }
            reader.moveUp();
        }
    }
}