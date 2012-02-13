package net.thucydides.core.statistics;

import net.thucydides.core.statistics.service.WithTagNamed;

public class With {
    public static With title(String titleValue) {
        return new WithTitle(titleValue);
    }

    public static With tag(String tagValue) {
        return new WithTagNamed(tagValue);
    }
}
