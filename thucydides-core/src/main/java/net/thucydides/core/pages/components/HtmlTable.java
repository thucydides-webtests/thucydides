package net.thucydides.core.pages.components;

import ch.lambdaj.function.convert.Converter;
import net.thucydides.core.matchers.BeanFieldMatcher;
import net.thucydides.core.matchers.BeanMatcher;
import net.thucydides.core.matchers.BeanPropertyMatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.of;

/**
 * Class designed to make it easier reading from and reasoning about data in HTML tables.
 */
public class HtmlTable {
    private final WebElement tableElement;

    public HtmlTable(final WebElement tableElement) {
        this.tableElement = tableElement;
    }

    public List<Map<String, String>> getRows() {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        List<String> headings = getHeadings();
        List<WebElement> rows = getRowElements();

        for (WebElement row : rows) {
            results.add(rowDataFrom(row, headings));
        }
        return results;
    }

    public List<String> getHeadings() {
        return convert(getHeadingElements(), toTextValues());
    }

    public List<WebElement> getHeadingElements() {
        return tableElement.findElements(By.tagName("th"));
    }

    public List<WebElement> getRowElements() {
        return tableElement.findElements(By.xpath(".//tr[td]"));
    }

    public List<WebElement> getRowElementsMatching(BeanFieldMatcher... matchers) {

        List<WebElement> rowElements = getRowElements();
        List<Integer> matchingRowIndexes = findMatchingIndexesFor(rowElements, matchers);

        List<WebElement> matchingElements = new ArrayList<WebElement>();
        for(Integer index : matchingRowIndexes) {
            matchingElements.add(rowElements.get(index));
        }
        return matchingElements;
    }

    private List<Integer> findMatchingIndexesFor(List<WebElement> rowElements,
                                                 BeanFieldMatcher[] matchers) {
        List<Integer> indexes = new ArrayList<Integer>();
        List<String> headings = getHeadings();

        int index = 0;
        for(WebElement rowElement : rowElements) {
            Map<String, String> rowData = rowDataFrom(rowElement, headings);
            if (matches(rowData, matchers)) {
                indexes.add(index);
            }
            index++;
        }

        return indexes;
    }

    private boolean matches(Map<String, String> rowData, BeanFieldMatcher[] matchers) {
        for(BeanFieldMatcher matcher : matchers) {
            if (!matcher.matches(rowData)) {
                return false;
            }
        }
        return true;
    }


    private Map<String,String> rowDataFrom(WebElement row, List<String> headings) {
        Map<String,String> rowData = new HashMap<String, String>();

        List<WebElement> cells = row.findElements(By.tagName("td"));

        int column = 0;
        for (String heading : headings) {
            String cell = cellValueAt(column++, cells);
            if (cell != null) {
                rowData.put(heading, cell);
            }
        }
        return rowData;
    }

    private String cellValueAt(final int column, final List<WebElement> cells) {
        if (column < cells.size()) {
            return cells.get(column).getText();
        } else {
            return null;
        }
    }

    private Converter<WebElement, String> toTextValues() {
        return new Converter<WebElement, String>() {
            @Override
            public String convert(WebElement from) {
                return from.getText();
            }
        };
    }

    public static List<Map<String, String>> rowsFrom(final WebElement table) {
        return new HtmlTable(table).getRows();
    }

    public static List<WebElement> filterRows(final WebElement table, final BeanFieldMatcher... matchers) {
        return new HtmlTable(table).getRowElementsMatching(matchers);
    }
}
