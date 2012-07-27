package net.thucydides.core.requirements.model;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

/**
 * A capability represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what capabilities have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Requirement implements Comparable {

    private final String name;
    private final String type;
    private final String narrativeText;
    private final List<Requirement> children;

    public Requirement(String name, String type, String narrativeText) {
        this.name = name;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = Collections.EMPTY_LIST;
    }

    public Requirement(String name, String type, String narrativeText, List<Requirement> children) {
        this.name = name;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = ImmutableList.copyOf(children);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getNarrativeText() {
        return narrativeText;
    }

    public List<Requirement> getChildren() {
        return ImmutableList.copyOf(children);
    }

    @Override
    public int compareTo(Object otherRequirement) {
        return name.compareTo(((Requirement) otherRequirement).getName());
    }
}
