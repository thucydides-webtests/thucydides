package net.thucydides.core.requirements.model;

public class RequirementBuilderTypeStep {
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