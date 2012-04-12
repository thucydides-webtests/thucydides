package net.thucydides.core.reports.json;

import flexjson.JSONSerializer;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.reports.TestOutcomes;

public class JSONResultTree {

    private final JSONTreeNode root;

    private ColorScheme colorScheme;

    public JSONResultTree() {
        colorScheme = Injectors.getInjector().getInstance(ColorScheme.class);
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

    public void addTestOutcomesForTag(String tag, TestOutcomes testOutcomes) {
        root.addTestOutcomesForTag(tag, testOutcomes);
    }
}
