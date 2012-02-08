package net.thucydides.core.statistics;

public class WithTitle extends With {
    
    private final String title;

    public WithTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
