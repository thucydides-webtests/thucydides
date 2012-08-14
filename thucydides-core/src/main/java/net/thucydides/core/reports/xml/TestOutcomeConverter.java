package net.thucydides.core.reports.xml;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * XStream converter used to generate the XML acceptance test report.
 *
 * @author johnsmart
 */
public class TestOutcomeConverter implements Converter {

    private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String ID_FIELD = "id";
    private static final String PATH_FIELD = "path";
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
    private static final String FEATURE = "feature";
    private static final String ISSUES = "issues";
    private static final String ISSUE = "issue";
    private static final String TAGS = "tags";
    private static final String TAG = "tag";
    private static final String QUALIFIER_FIELD = "qualifier";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";
    private static final String EXCEPTION = "exception";
    private static final String ERROR = "error";
    private static final String SCREENSHOT_LIST_FIELD = "screenshots";
    private static final String SCREENSHOT_FIELD = "screenshot";
    private static final String SCREENSHOT_IMAGE = "image";
    private static final String SCREENSHOT_SOURCE = "source";
    private static final String DESCRIPTION = "description";
    private static final String DURATION = "duration";
    private static final String SESSION_ID = "session-id";

    public TestOutcomeConverter() {
    }

    /**
     * Determines which classes this converter applies to.
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return TestOutcome.class.isAssignableFrom(clazz);
    }

    /**
     * Generate an XML report given an TestOutcome object.
     */
    public void marshal(final Object value, final HierarchicalStreamWriter writer,
                        final MarshallingContext context) {
        TestOutcome testOutcome = (TestOutcome) value;
        Preconditions.checkNotNull(testOutcome, "The test run was null - WTF?");

        writer.addAttribute(TITLE_FIELD, titleFrom(testOutcome));
        writer.addAttribute(NAME_FIELD, nameFrom(testOutcome));
        if (testOutcome.getQualifier() != null && testOutcome.getQualifier().isPresent()) {
            writer.addAttribute(QUALIFIER_FIELD, testOutcome.getQualifier().get());
        }
        writer.addAttribute(STEPS_FIELD, Integer.toString(testOutcome.countTestSteps()));
        writer.addAttribute(SUCCESSFUL_FIELD, Integer.toString(testOutcome.getSuccessCount()));
        writer.addAttribute(FAILURES_FIELD, Integer.toString(testOutcome.getFailureCount()));
        writer.addAttribute(SKIPPED_FIELD, Integer.toString(testOutcome.getSkippedCount()));
        writer.addAttribute(IGNORED_FIELD, Integer.toString(testOutcome.getIgnoredCount()));
        writer.addAttribute(PENDING_FIELD, Integer.toString(testOutcome.getPendingCount()));
        writer.addAttribute(RESULT_FIELD, testOutcome.getResult().toString());
        writer.addAttribute(DURATION, Long.toString(testOutcome.getDuration()));
        if (isNotEmpty(testOutcome.getSessionId())) {
            writer.addAttribute(SESSION_ID, testOutcome.getSessionId());
        }
        addUserStoryTo(writer, testOutcome.getUserStory());
        addIssuesTo(writer, testOutcome.getIssues());
        addTagsTo(writer, testOutcome.getTags());

        List<TestStep> steps = testOutcome.getTestSteps();
        for (TestStep step : steps) {
            writeStepTo(writer, step);
        }
    }

    private String titleFrom(final TestOutcome testOutcome) {
        return testOutcome.getTitle();
    }

    private String nameFrom(final TestOutcome testOutcome) {
        if (testOutcome.getMethodName() != null) {
            return testOutcome.getMethodName();
        } else {
            return testOutcome.getTitle();
        }
    }


    private void writeStepTo(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.isAGroup()) {
            writer.startNode(TEST_GROUP);
            writer.addAttribute(NAME_FIELD, step.getDescription());
            writeResult(writer, step);
            writeScreenshotIfPresent(writer, step);

            List<TestStep> nestedSteps = step.getChildren();
            for (TestStep nestedStep : nestedSteps) {
                writeStepTo(writer, nestedStep);
            }
            writer.endNode();
        } else {
            writer.startNode(TEST_STEP);
            writeResult(writer, step);
            writer.addAttribute(DURATION, Long.toString(step.getDuration()));
            writeScreenshotIfPresent(writer, step);
            writeDescription(writer, step);
            writeErrorForFailingTest(writer, step);
            writer.endNode();
        }
    }

    private void addUserStoryTo(final HierarchicalStreamWriter writer, final Story userStory) {
        if (userStory != null) {
            writer.startNode(USER_STORY);
            writer.addAttribute(ID_FIELD, userStory.getId());
            writer.addAttribute(NAME_FIELD, userStory.getName());
            if (userStory.getPath() != null) {
                writer.addAttribute(PATH_FIELD, userStory.getPath());
            }
            if (userStory.getFeatureClass() != null) {
                writeFeatureNode(writer, userStory);
            }
            writer.endNode();
        }
    }

    private void writeFeatureNode(HierarchicalStreamWriter writer, Story userStory) {
        ApplicationFeature feature = ApplicationFeature.from(userStory.getFeatureClass());
        writer.startNode(FEATURE);
        writer.addAttribute(ID_FIELD, feature.getId());
        writer.addAttribute(NAME_FIELD, feature.getName());
        writer.endNode();
    }

    private void addIssuesTo(final HierarchicalStreamWriter writer, final Set<String> issues) {
        if (!issues.isEmpty()) {
            writer.startNode(ISSUES);
            for (String issue : issues) {
                writer.startNode(ISSUE);
                writer.setValue(issue);
                writer.endNode();
            }
            writer.endNode();
        }
    }


    private void addTagsTo(HierarchicalStreamWriter writer, Set<TestTag> tags) {
        if (!CollectionUtils.isEmpty(tags)) {
            writer.startNode(TAGS);
            for (TestTag tag : tags) {
                writer.startNode(TAG);
                writer.addAttribute(TAG_NAME, tag.getName());
                writer.addAttribute(TAG_TYPE, tag.getType());
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
        if ((step.getErrorMessage() != null) && (!StringUtils.isEmpty(step.getErrorMessage()))) {
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

    private void writeScreenshotIfPresent(final HierarchicalStreamWriter writer, final TestStep step) {
        if ((step.getScreenshots() != null) && (step.getScreenshots().size() > 0)) {
            writer.startNode(SCREENSHOT_LIST_FIELD);
            for(ScreenshotAndHtmlSource screenshotAndHtmlSource : step.getScreenshots()) {
                writer.startNode(SCREENSHOT_FIELD);
                writer.addAttribute(SCREENSHOT_IMAGE, screenshotAndHtmlSource.getScreenshotFile().getName());
                writer.addAttribute(SCREENSHOT_SOURCE, screenshotAndHtmlSource.getSourcecode().getName());
                writer.endNode();
            }
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
     * Convert XML to an TestOutcome object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
                            final UnmarshallingContext context) {

        String methodName = reader.getAttribute(NAME_FIELD);
        TestOutcome testOutcome = new TestOutcome(methodName);
        testOutcome.setTitle(reader.getAttribute(TITLE_FIELD));
        if (reader.getAttribute(QUALIFIER_FIELD) != null) {
            testOutcome = testOutcome.withQualifier(reader.getAttribute(QUALIFIER_FIELD));
        }
        Long duration = readDuration(reader);
        testOutcome.setDuration(duration);
        String sessionId = readSessionId(reader);
        testOutcome.setSessionId(sessionId);
        readChildren(reader, testOutcome);
        return testOutcome;
    }

    private void readChildren(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(TEST_STEP)) {
                readTestStep(reader, testOutcome);
            } else if (childNode.equals(TEST_GROUP)) {
                readTestGroup(reader, testOutcome);
            } else if (childNode.equals(ISSUES)) {
                readTestRunIssues(reader, testOutcome);
            } else if (childNode.equals(USER_STORY)) {
                readUserStory(reader, testOutcome);
            } else if (childNode.equals(TAGS)) {
                readTags(reader, testOutcome);
            }
            reader.moveUp();
        }
    }


    private void readUserStory(final HierarchicalStreamReader reader,
                               final TestOutcome testOutcome) {

        String storyId = reader.getAttribute(ID_FIELD);
        String storyName = reader.getAttribute(NAME_FIELD);
        String storyPath = reader.getAttribute(PATH_FIELD);
        ApplicationFeature feature = null;

        if (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(FEATURE)) {
                feature = readFeature(reader);
            }
            reader.moveUp();
        }
        Story story;
        if (feature == null) {
            story = Story.withIdAndPath(storyId, storyName,storyPath);
        } else {
            story = Story.withId(storyId, storyName, feature.getId(), feature.getName());
        }
        testOutcome.setUserStory(story);
    }

    private ApplicationFeature readFeature(final HierarchicalStreamReader reader) {

        String featureId = reader.getAttribute(ID_FIELD);
        String featureName = reader.getAttribute(NAME_FIELD);
        return new ApplicationFeature(featureId, featureName);
    }


    private void readTestRunIssues(final HierarchicalStreamReader reader,
                                         final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String issue = reader.getValue();
            testOutcome.isRelatedToIssue(issue);
            reader.moveUp();
        }
    }

    private void readTags(final HierarchicalStreamReader reader,
                          final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<TestTag>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(TAG)) {
                tags.add(readTag(reader));
            }
            reader.moveUp();
        }
        testOutcome.setTags(tags);
    }

    private TestTag readTag(HierarchicalStreamReader reader) {
        return TestTag.withName(reader.getAttribute("name")).andType(reader.getAttribute("type"));
    }

    /*
    * <test-step result="SUCCESS"> <description>The customer navigates to the
    * metro masthead site.</description>
    * <screenshot>the_customer_navigates_to_the_metro_masthead_site2
    * .png</screenshot> </test-step>
    */
    private void readTestStep(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        TestStep step = new TestStep();
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        step.setResult(result);

        Long duration = readDuration(reader);
        step.setDuration(duration);
        readTestStepChildren(reader, step);

        testOutcome.recordStep(step);
    }

    private long readDuration(HierarchicalStreamReader reader) {
        String durationValue = reader.getAttribute(DURATION);
        if (StringUtils.isNumeric(durationValue)) {
            return Long.parseLong(reader.getAttribute(DURATION));
        } else {
            return 0;
        }
    }

    private String readSessionId(HierarchicalStreamReader reader) {
        return reader.getAttribute(SESSION_ID);
    }

    private void readTestGroup(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        String name = reader.getAttribute(NAME_FIELD);
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        testOutcome.recordStep(new TestStep(name));
        testOutcome.startGroup();
        testOutcome.getCurrentGroup().setResult(result);
        readChildren(reader, testOutcome);
        testOutcome.endGroup();
    }

    private void readTestStepChildren(final HierarchicalStreamReader reader, final TestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(DESCRIPTION)) {
                step.setDescription(reader.getValue());
            } else if (childNode.equals(SCREENSHOT_LIST_FIELD)) {
                readScreenshots(reader, step);
            }
            reader.moveUp();
        }
    }

    private void readScreenshots(HierarchicalStreamReader reader, TestStep step) {
        if (reader.getNodeName().equals(SCREENSHOT_LIST_FIELD)) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String childNode = reader.getNodeName();
                if (childNode.equals(SCREENSHOT_FIELD)) {
                    String screenshot = reader.getAttribute(SCREENSHOT_IMAGE);
                    String source = reader.getAttribute(SCREENSHOT_SOURCE);
                    step.addScreenshot(new ScreenshotAndHtmlSource(new File(screenshot), new File(source)));
                }
                reader.moveUp();
            }
        }
    }
}