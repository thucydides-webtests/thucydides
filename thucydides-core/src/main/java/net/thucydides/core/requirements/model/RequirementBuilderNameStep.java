package net.thucydides.core.requirements.model;

public class RequirementBuilderNameStep {

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