package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public JSONTreeNode(String id, String name, ColorScheme colorScheme) {
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

            storyNode.children.addAll(getTestOutcomeNodesFor(storyResult));

            stories.add(storyNode);
        }
        return stories;

    }

    private List<JSONTreeNode> getTestOutcomeNodesFor(final StoryTestResults storyTestResults) {
        List<JSONTreeNode> outcomes = new ArrayList<JSONTreeNode>();

        for (TestOutcome outcome : storyTestResults.getTestOutcomes()) {
            JSONTreeNode node = new JSONTreeNode(outcome.getMethodName(),
                                                      outcome.getTitle(),
                                                      colorScheme);

            node.getData().put("$area", outcome.countTestSteps());
            node.getData().put("$color", rgbFormatOf(colorScheme.colorFor(outcome)));
            node.getData().put("result", outcome.getResult());
            node.getData().put("steps", outcome.countTestSteps());
            outcomes.add(node);
        }
        return outcomes;
    }

}
