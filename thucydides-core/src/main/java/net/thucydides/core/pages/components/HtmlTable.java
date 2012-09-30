package net.thucydides.core.pages.components;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.matchers.BeanMatcher;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.convert;

/**
 * Class designed to make it easier reading from and reasoning about data in HTML tables.
 */
public class HtmlTable {
    private final WebElement tableElement;
    private List<String> headings;

    public HtmlTable(final WebElement tableElement) {
        this.tableElement = tableElement;
        this.headings = null;
    }

    public HtmlTable(final WebElement tableElement, List<String> headings) {
        this.tableElement = tableElement;
        this.headings = headings;
    }

    public static HtmlTable inTable(final WebElement table) {
        return new HtmlTable(table);
    }

    public List<Map<Object, String>> getRows() {

        List<Map<Object, String>> results = new ArrayList<Map<Object, String>>();

        List<String> headings = getHeadings();
        List<WebElement> rows = getRowElementsFor(headings);

        for (WebElement row : rows) {
            List<WebElement> cells = cellsIn(row);
            if (enoughCellsFor(headings).in(cells)) {
                results.add(rowDataFrom(cells, headings));
            }
        }
        return results;
    }

    public WebElement findFirstRowWhere(final BeanMatcher... matchers) {
        List<WebElement> rows = getRowElementsWhere(matchers);
        if (rows.isEmpty()) {
            throw new AssertionError("Expecting a table with at least one row where: " + Arrays.deepToString(matchers));
        }
        return rows.get(0);
    }

    public boolean containsRowElementsWhere(BeanMatcher... matchers) {
        List<WebElement> rows = getRowElementsWhere(matchers);
        return (!rows.isEmpty());
    }

    public void shouldHaveRowElementsWhere(BeanMatcher... matchers) {
        List<WebElement> rows = getRowElementsWhere(matchers);
        if (rows.isEmpty()) {
            throw new AssertionError("Expecting a table with at least one row where: " + Arrays.deepToString(matchers));
        }
    }

    public void shouldNotHaveRowElementsWhere(BeanMatcher... matchers) {
        List<WebElement> rows = getRowElementsWhere(matchers);
        if (!rows.isEmpty()) {
            throw new AssertionError("Expecting a table with no rows where: " + Arrays.deepToString(matchers));
        }
    }

    public static HtmlTableBuilder withColumns(String... headings) {
        return new HtmlTableBuilder(Arrays.asList(headings));
    }

    public static class HtmlTableBuilder {
        private final List<String> headings;

        public HtmlTableBuilder(List<String> headings) {
            this.headings = headings;
        }

        public List<Map<Object, String>> readRowsFrom(WebElement table) {
            return new HtmlTable(table, headings).getRows();
        }

        public HtmlTable inTable(WebElement table) {
            return new HtmlTable(table, headings);
        }
    }

    private class EnoughCellsCheck {
        private final int minimumNumberOfCells;

        private EnoughCellsCheck(List<String> headings) {
            this.minimumNumberOfCells = headings.size();
        }
        
        public boolean in(List<WebElement> cells) {
            return (cells.size() >= minimumNumberOfCells);
        }
    }

    private EnoughCellsCheck enoughCellsFor(List<String> headings) {
        return new EnoughCellsCheck(headings);
    }

    public List<String> getHeadings() {
        if (headings == null) {
            List<String> thHeadings = convert(headingElements(), toTextValues());
            if (thHeadings.isEmpty()) {
                headings = convert(firstRowElements(), toTextValues());
            } else {
                headings = thHeadings;
            }
        }
        return headings;
    }

    public List<WebElement> headingElements() {
        return tableElement.findElements(By.xpath(".//th"));
    }

    public List<WebElement> firstRowElements() {
        return tableElement.findElement(By.tagName("tr")).findElements(By.xpath(".//td"));
    }

    public List<WebElement> getRowElementsFor(List<String> headings) {
        
        List<WebElement> rowCandidates = tableElement.findElements(By.xpath(".//tr[td][count(td)>=" + headings.size() + "]"));
        rowCandidates = stripHeaderRowIfPresent(rowCandidates, headings);
        return rowCandidates;
    }

    public List<WebElement> getRowElements() {

        return getRowElementsFor(getHeadings());
    }

    private List<WebElement> stripHeaderRowIfPresent(List<WebElement> rowCandidates, List<String> headings) {
        if (!rowCandidates.isEmpty()) {
            WebElement firstRow = rowCandidates.get(0);
            if (hasMatchingCellValuesIn(firstRow, headings)) {
                rowCandidates.remove(0);    
            }
        }
        return rowCandidates;
    }

    private boolean hasMatchingCellValuesIn(WebElement firstRow, List<String> headings) {
        List<WebElement> cells = firstRow.findElements(By.xpath("./td"));
        for(int cellIndex = 0; cellIndex < headings.size(); cellIndex++) {
            if ((cells.size() < cellIndex) || (!cells.get(cellIndex).getText().equals(headings.get(cellIndex)))) {
                return false;
            }
        }
        return true;
    }

    public List<WebElement> getRowElementsWhere(BeanMatcher... matchers) {

        List<WebElement> rowElements = getRowElementsFor(getHeadings());
        List<Integer> matchingRowIndexes = findMatchingIndexesFor(rowElements, matchers);

        List<WebElement> matchingElements = new ArrayList<WebElement>();
        for(Integer index : matchingRowIndexes) {
            matchingElements.add(rowElements.get(index));
        }
        return matchingElements;
    }

    private List<Integer> findMatchingIndexesFor(List<WebElement> rowElements,
                                                 BeanMatcher[] matchers) {
        List<Integer> indexes = new ArrayList<Integer>();
        List<String> headings = getHeadings();

        int index = 0;
        for(WebElement row : rowElements) {
            List<WebElement> cells = cellsIn(row);
            Map<Object, String> rowData = rowDataFrom(cells, headings);
            if (matches(rowData, matchers)) {
                indexes.add(index);
            }
            index++;
        }

        return indexes;
    }

    private boolean matches(Map<Object, String> rowData, BeanMatcher[] matchers) {
        for(BeanMatcher matcher : matchers) {
            if (!matcher.matches(rowData)) {
                return false;
            }
        }
        return true;
    }


    private Map<Object,String> rowDataFrom(List<WebElement> cells, List<String> headings) {
        Map<Object,String> rowData = new HashMap<Object, String>();

        int column = 0;
        for (String heading : headings) {
            String cell = cellValueAt(column++, cells);
            if (!StringUtils.isEmpty(heading)) {
                rowData.put(heading, cell);
            }
            rowData.put(column, cell);
        }
        return rowData;
    }

    private List<WebElement> cellsIn(WebElement row) {
        return row.findElements(By.xpath("./td"));
    }

    private String cellValueAt(final int column, final List<WebElement> cells) {
        return cells.get(column).getText();
    }

    private Converter<WebElement, String> toTextValues() {
        return new Converter<WebElement, String>() {
            public String convert(WebElement from) {
                return from.getText();
            }
        };
    }

    public static List<Map<Object, String>> rowsFrom(final WebElement table) {
        return new HtmlTable(table).getRows();
    }

    public static List<WebElement> filterRows(final WebElement table, final BeanMatcher... matchers) {
        return new HtmlTable(table).getRowElementsWhere(matchers);
    }

    public List<WebElement> filterRows(final BeanMatcher... matchers) {
        return new HtmlTable(tableElement).getRowElementsWhere(matchers);
    }

}
