package net.thucydides.core.statistics.service;

import net.thucydides.core.statistics.With;

public class WithTagNamed extends With {

    private final String tag;

    public WithTagNamed(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
