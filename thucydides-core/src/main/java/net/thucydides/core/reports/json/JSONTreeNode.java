package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;
import static net.thucydides.core.reports.json.ColorScheme.rgbFormatOf;

/**
 * Used to store data in JSON form to be displayed on the report dashboard.
 */
public class JSONTreeNode {

    private final String id;

    private final String name;

    private final Map<String, Object> data;

    private final List<JSONTreeNode> children;

    private final ColorScheme colorScheme;

    public JSONTreeNode(final String id, final String name, final ColorScheme colorScheme) {
        this.id = id;
        this.name = name;
        this.colorScheme = colorScheme;
        data = new HashMap<String, Object>();
        children = new ArrayList<JSONTreeNode>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public List<JSONTreeNode> getChildren() {
        return children;
    }

    public void addFeature(final FeatureResults feature) {
        JSONTreeNode featureNode = new JSONTreeNode(feature.getFeature().getId(),
                feature.getFeature().getName(),
                colorScheme);
        featureNode.getData().put("$area", feature.getTotalSteps());
        featureNode.getData().put("$color", rgbFormatOf(colorScheme.colorFor(feature)));
        featureNode.getData().put("stories", feature.getTotalStories());
        featureNode.getData().put("tests", feature.getTotalTests());
        featureNode.getData().put("passing", feature.getPassingTests());
        featureNode.getData().put("pending", feature.getPendingTests());
        featureNode.getData().put("failing", feature.getFailingTests());
        featureNode.getData().put("steps", feature.getTotalSteps());

        featureNode.children.addAll(getStoryNodesFor(feature));

        children.add(featureNode);

    }

    private List<JSONTreeNode> getStoryNodesFor(final FeatureResults feature) {
        List<JSONTreeNode> stories = new ArrayList<JSONTreeNode>();

        for (StoryTestResults storyResult : feature.getStoryResults()) {
            JSONTreeNode storyNode = new JSONTreeNode(storyResult.getStory().getId(),
                    storyResult.getStory().getName(),
                    colorScheme);

            storyNode.getData().put("$area", storyResult.getStepCount());
            storyNode.getData().put("$color", rgbFormatOf(colorScheme.colorFor(storyResult)));
            storyNode.getData().put("tests", storyResult.getTotal());
            storyNode.getData().put("passing", storyResult.getSuccessCount());
            storyNode.getData().put("pending", storyResult.getPendingCount());
            storyNode.getData().put("failing", storyResult.getFailureCount());
            storyNode.getData().put("steps", storyResult.getStepCount());

            storyNode.children.addAll(getTestOutcomeNodesFor(storyResult, averageTestSizeIn(feature)));

            stories.add(storyNode);
        }
        return stories;

    }

    private int averageTestSizeIn(final FeatureResults feature) {
        int totalExecutedSteps = totalStepsInExecutedTestsIn(feature);
        int totalExecutedTests = totalExecutedTestsIn(feature);
        if (totalExecutedTests > 0) {
            return totalExecutedSteps / totalExecutedTests;
        } else {
            return 1;
        }
    }

    protected int totalExecutedTestsIn(final FeatureResults feature) {
        int testTally = 0;

        List<StoryTestResults> storyTestResults = feature.getStoryResults();
        for (StoryTestResults testResults : storyTestResults) {
            List<TestOutcome> outcomes = testResults.getTestOutcomes();
            for(TestOutcome outcome : outcomes) {
                if (outcome.isFailure() || outcome.isSuccess()) {
                    testTally++;
                }
            }

        }
        return testTally;
    }

    protected int totalStepsInExecutedTestsIn(final FeatureResults feature) {
        int stepTally = 0;

        List<StoryTestResults> storyTestResults = feature.getStoryResults();
        for (StoryTestResults testResults : storyTestResults) {
            List<TestOutcome> outcomes = testResults.getTestOutcomes();
            for(TestOutcome outcome : outcomes) {
                if (outcome.isFailure() || outcome.isSuccess()) {
                    stepTally += outcome.getStepCount();
                }
            }

        }
        return stepTally;
    }

    private List<JSONTreeNode> getTestOutcomeNodesFor(final StoryTestResults storyTestResults,
                                                      final int sizeOfPendingOrSkippedTests) {
        List<JSONTreeNode> outcomes = new ArrayList<JSONTreeNode>();

        for (TestOutcome outcome : storyTestResults.getTestOutcomes()) {
            JSONTreeNode node = new JSONTreeNode(outcome.getMethodName(),
                    outcome.getTitle(),
                    colorScheme);

            int nodeArea = findTestArea(sizeOfPendingOrSkippedTests, outcome);
            node.getData().put("$area", nodeArea);
            node.getData().put("$color", rgbFormatOf(colorScheme.colorFor(outcome)));
            node.getData().put("result", outcome.getResult());
            node.getData().put("steps", outcome.countTestSteps());
            node.getData().put("report", outcome.getReportName(HTML));


            //node.children.addAll(getTestStepNodesFor(outcome.getTestSteps()));

            outcomes.add(node);
        }
        return outcomes;
    }

    private Collection<? extends JSONTreeNode> getTestStepNodesFor(List<TestStep> testSteps) {
        List<JSONTreeNode> stepNodes = new ArrayList<JSONTreeNode>();

        for (TestStep step : testSteps) {
            JSONTreeNode node = new JSONTreeNode(step.getDescription(),
                                                 step.getDescription(),
                                                 colorScheme);

            node.getData().put("$area", 100);
            node.getData().put("$color", rgbFormatOf(colorScheme.colorFor(step)));
            node.getData().put("result", step.getResult());
            stepNodes.add(node);
        }
        return stepNodes;
    }

    private int findTestArea(final int sizeOfPendingOrSkippedTests, final TestOutcome outcome) {
        int nodeArea;
        if (testWasSkipped(outcome)) {
            nodeArea = sizeOfPendingOrSkippedTests;
        } else {
            nodeArea = outcome.countTestSteps();
        }
        return nodeArea;
    }

    private boolean testWasSkipped(final TestOutcome outcome) {
        return (outcome.isPending()
                || (outcome.getResult() == TestResult.IGNORED)
                || (outcome.getResult() == TestResult.SKIPPED));
    }

}
