package net.thucydides.core.webdriver.jquery;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ByJQuerySelector extends By {

    private static final char DOUBLE_QUOTE = '"';
    private static final String SINGLE_QUOTE = "'";
    private final String jQuerySelector;

    public ByJQuerySelector(final String selector) {
        this.jQuerySelector = selector;
    }

    @Override
    public List findElements(SearchContext context) {
        String jquery = "return $(" + quoted(jQuerySelector) + ").get();";
        return (List) ((JavascriptExecutor) context).executeScript(jquery);
    }

    @Override
    public WebElement findElement(SearchContext context) {
        String jquery = "return $(" + quoted(jQuerySelector) + ").get(0);";
        WebElement element = (WebElement) ((JavascriptExecutor) context).executeScript(jquery);
        if (element == null) {
            throw new NoSuchElementException("No element found matching JQuery selector " + jQuerySelector);
        } else {
            return element;
        }

    }

    private String quoted(final String jQuerySelector) {
        if (jQuerySelector.contains("'")) {
            return DOUBLE_QUOTE + jQuerySelector + '"';
        } else {
            return  "'" + jQuerySelector + SINGLE_QUOTE;
        }
    }

    @Override
    public String toString() {
        return "By.jQuerySelector: " + jQuerySelector;
    }


}