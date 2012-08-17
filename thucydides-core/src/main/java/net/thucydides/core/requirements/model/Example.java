package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;

public class Example {
    private final String description;
    private final Optional<String> cardNumber;

    private final static Optional<String> NO_CARD_NUMBER = Optional.absent();

    public Example(String description, Optional<String> cardNumber) {
        this.description = description;
        this.cardNumber = cardNumber;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getCardNumber() {
        return cardNumber;
    }

    @Override
    public String toString() {
        if (cardNumber.isPresent()) {
            return description + " [" + cardNumber + "]";
        } else {
            return description;
        }
    }

    public static ExampleBuilder withDescription(String description) {
        return new ExampleBuilder(description);
    }

    public static class ExampleBuilder {
        private final String description;

        public ExampleBuilder(String description) {
            this.description = description;
        }

        public Example andCardNumber(String cardNumber) {
            return new Example(description, Optional.of(cardNumber));
        }

        public Example andNoCardNumber() {
            return new Example(description, NO_CARD_NUMBER);
        }
    }
}
