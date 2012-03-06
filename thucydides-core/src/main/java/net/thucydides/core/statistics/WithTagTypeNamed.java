package net.thucydides.core.statistics;

public class WithTagTypeNamed extends With {

    private final String tagType;

    public WithTagTypeNamed(String tagType) {
        this.tagType = tagType;
    }

    public String getTagType() {
        return tagType;
    }
}
