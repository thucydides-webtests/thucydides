package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;

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

    public void addFeature(FeatureResults feature) {
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

        children.add(featureNode);

    }

    private String colorFor(FeatureResults feature) {
        return "#ff0000";
    }

}
