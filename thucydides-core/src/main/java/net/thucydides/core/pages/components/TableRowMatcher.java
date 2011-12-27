package net.thucydides.core.pages.components;

import net.thucydides.core.matchers.BeanMatcher;
import org.openqa.selenium.WebElement;

public abstract class TableRowMatcher {

    final protected BeanMatcher[] matchers;

    public TableRowMatcher(final BeanMatcher... matchers) {
        this.matchers = matchers;
    }

    public abstract WebElement from(final WebElement table);
}
