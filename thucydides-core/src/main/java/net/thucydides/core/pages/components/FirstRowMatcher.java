package net.thucydides.core.pages.components;

import net.thucydides.core.matchers.BeanMatcher;
import org.openqa.selenium.WebElement;

public class FirstRowMatcher extends TableRowMatcher {

    public FirstRowMatcher(BeanMatcher... matchers) {
        super(matchers);
    }

    @Override
    public WebElement from(final WebElement table) {
        return null;
    }
}
