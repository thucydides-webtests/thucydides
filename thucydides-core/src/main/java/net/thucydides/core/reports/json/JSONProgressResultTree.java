package net.thucydides.core.reports.json;

import flexjson.JSONSerializer;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.reports.TestOutcomes;

public class JSONProgressResultTree {

    private final JSONTreeNode root;

    private final ColorScheme colorScheme;

    public JSONProgressResultTree() {
        colorScheme = new ProgressColorScheme();
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
