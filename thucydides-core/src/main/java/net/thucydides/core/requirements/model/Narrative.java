package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;

public class Narrative {
    private final Optional<String> title;
    private final Optional<String> cardNumber;
    private final String text;
    private String type;

    public Narrative(Optional<String> title, Optional<String> cardNumber, String type, String text) {
        this.title = title;
        this.cardNumber = cardNumber;
        this.type = type;
        this.text = text;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getCardNumber() {
        return cardNumber;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
