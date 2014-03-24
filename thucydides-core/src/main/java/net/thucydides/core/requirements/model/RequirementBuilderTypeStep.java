package net.thucydides.core.requirements.model;

public class RequirementBuilderTypeStep {
    final RequirementBuilderNameStep requirementBuilderNameStep;
    final String type;

    public RequirementBuilderTypeStep(RequirementBuilderNameStep requirementBuilderNameStep, String type) {
        this.requirementBuilderNameStep = requirementBuilderNameStep;
        this.type = type;
    }

    public Requirement withNarrative(String narrativeText) {
        String name = requirementBuilderNameStep.name;
        String displayName = requirementBuilderNameStep.displayName;
        String cardNumber = requirementBuilderNameStep.cardNumber;
        return new Requirement(name, displayName, cardNumber, type, new CustomFieldValue("Narrative", narrativeText));
    }
}