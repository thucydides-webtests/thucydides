package net.thucydides.core.statistics;

public class WithTagNamed extends With {

    private final String tag;

    public WithTagNamed(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
