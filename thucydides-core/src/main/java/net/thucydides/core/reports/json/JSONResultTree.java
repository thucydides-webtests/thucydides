package net.thucydides.core.reports.json;

import flexjson.JSONSerializer;
import net.thucydides.core.model.FeatureResults;

public class JSONResultTree {

    private final JSONTreeNode root;

    private ColorScheme colorScheme;

    public JSONResultTree() {
        colorScheme = new ColorScheme();
        root = new JSONTreeNode("root", "Application", getColorScheme());
    }

    public String toJSON() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.exclude("*.class")
                         .exclude("*.colorScheme")
                         .deepSerialize(root);
    }

    public void addFeature(FeatureResults feature) {
        root.addFeature(feature);
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

}
