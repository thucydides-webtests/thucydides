package net.thucydides.core.statistics;

public class With {
    public static With title(String titleValue) {
        return new WithTitle(titleValue);
    }
}
