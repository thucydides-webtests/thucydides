package net.thucydides.core.statistics;

public class With {
    public static With title(String titleValue) {
        return new WithTitle(titleValue);
    }

    public static With tag(String tagValue) {
        return new WithTagNamed(tagValue);
    }

    public static With tagType(String type) {
        return new WithTagTypeNamed(type);
    }
}
