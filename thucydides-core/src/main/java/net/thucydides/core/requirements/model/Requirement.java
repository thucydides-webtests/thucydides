package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

/**
 * A capability represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what capabilities have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Requirement implements Comparable {

    private final String displayName;
    private final String name;
    private final String type;
    private final String narrativeText;
    private final String cardNumber;
    private final List<Requirement> children;

    protected Requirement(String name, String displayName, String cardNumber, String type, String narrativeText, List<Requirement> children) {
        this.name = name;
        this.displayName = displayName;
        this.cardNumber = cardNumber;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = ImmutableList.copyOf(children);
    }

    protected Requirement(String name, String displayName, String cardNumber, String type, String narrativeText) {
        this.name = name;
        this.displayName = displayName;
        this.cardNumber = cardNumber;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = Collections.EMPTY_LIST;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String getChildType() {
        return (!children.isEmpty()) ? children.get(0).getType() : null;
    }

    public String getNarrativeText() {
        return narrativeText;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public List<Requirement> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public int compareTo(Object otherRequirement) {
        return name.compareTo(((Requirement) otherRequirement).getName());
    }

    public static RequirementBuilderNameStep named(String name) {
        return new RequirementBuilderNameStep(name);
    }

    public Requirement withChildren(List<Requirement> children) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.type, this.narrativeText, children);
    }

    public static class RequirementBuilderNameStep {

        final String name;
        String displayName;
        String cardNumber;

        public RequirementBuilderNameStep(String name) {
            this.name = name;
            this.displayName = name;
        }

        public RequirementBuilderNameStep withOptionalDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public RequirementBuilderNameStep withOptionalCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public RequirementBuilderTypeStep withType(String type) {
            return new RequirementBuilderTypeStep(this, type);
        }

    }

    public static class RequirementBuilderTypeStep {
        final RequirementBuilderNameStep requirementBuilderNameStep;
        final String type;

        public RequirementBuilderTypeStep(RequirementBuilderNameStep requirementBuilderNameStep, String type) {
            this.requirementBuilderNameStep = requirementBuilderNameStep;
            this.type = type;
        }

        public Requirement withNarrativeText(String narrativeText) {
            String name = requirementBuilderNameStep.name;
            String displayName = requirementBuilderNameStep.displayName;
            String cardNumber = requirementBuilderNameStep.cardNumber;
            return new Requirement(name, displayName, cardNumber, type, narrativeText);
        }
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "displayName='" + displayName + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", narrativeText='" + narrativeText + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", children=" + children +
                '}';
    }
}
