package net.thucydides.core.capabilities.model;

import com.google.common.base.Optional;

public class Narrative {
    private final Optional<String> title;
    private final String text;
    private String type;

    public Narrative(Optional<String> title, String type, String text) {
        this.title = title;
        this.type = type;
        this.text = text;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
