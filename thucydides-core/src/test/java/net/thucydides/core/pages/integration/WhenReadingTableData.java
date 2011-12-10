package net.thucydides.core.pages.integration;


import net.thucydides.core.pages.components.HtmlTable;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.Map;

import static net.thucydides.core.matchers.BeanMatchers.the;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

public class WhenReadingTableData extends FluentElementAPITestsBaseClass {

    WebDriver htmlUnitDriver;
    StaticSitePage page;

    @Before
    public void openStaticPage() {
        htmlUnitDriver = new WebDriverFacade(HtmlUnitDriver.class, new WebDriverFactory());
        page = new StaticSitePage(htmlUnitDriver, 1);
        page.setWaitForTimeout(500);
        page.open();
    }


    @Test
    public void should_read_table_headings() {
        HtmlTable table = new HtmlTable(page.clients);
        List<String> tableHeadings = table.getHeadings();
        assertThat(tableHeadings.toString(), is("[First Name, Last Name, Favorite Colour]"));
    }

    @Test
    public void should_read_table_data_as_a_list_of_maps() {
        HtmlTable table = new HtmlTable(page.clients);

        List<Map<String, String>> tableRows = table.getRows();

        assertThat(tableRows.size(), is(3));
        assertThat(tableRows.get(0), allOf(hasEntry("First Name", "Tim"), hasEntry("Last Name", "Brooke-Taylor"), hasEntry("Favorite Colour", "Red")));
        assertThat(tableRows.get(1), allOf(hasEntry("First Name", "Graeme"), hasEntry("Last Name", "Garden"), hasEntry("Favorite Colour", "Green")));
        assertThat(tableRows.get(2), allOf(hasEntry("First Name", "Bill"),hasEntry("Last Name", "Oddie"), hasEntry("Favorite Colour","Blue")));
    }

    @Test
    public void should_read_table_data_using_a_static_method() {
        HtmlTable table = new HtmlTable(page.clients);

        List<Map<String, String>> tableRows = HtmlTable.rowsFrom(page.clients);

        assertThat(tableRows.size(), is(3));
        assertThat(tableRows.get(0), allOf(hasEntry("First Name", "Tim"), hasEntry("Last Name", "Brooke-Taylor"), hasEntry("Favorite Colour", "Red")));
        assertThat(tableRows.get(1), allOf(hasEntry("First Name", "Graeme"), hasEntry("Last Name", "Garden"), hasEntry("Favorite Colour", "Green")));
        assertThat(tableRows.get(2), allOf(hasEntry("First Name", "Bill"),hasEntry("Last Name", "Oddie"), hasEntry("Favorite Colour","Blue")));
    }


    @Test
    public void should_ignore_data_in_extra_cells() {
        HtmlTable table = new HtmlTable(page.clients_with_extra_cells);

        List<Map<String, String>> tableRows = table.getRows();

        assertThat(tableRows.size(), is(3));
        assertThat(tableRows.get(0), allOf(hasEntry("First Name", "Tim"),hasEntry("Last Name", "Brooke-Taylor"), hasEntry("Favorite Colour","Red")));
        assertThat(tableRows.get(1), allOf(hasEntry("First Name", "Graeme"),hasEntry("Last Name", "Garden"), hasEntry("Favorite Colour","Green")));
        assertThat(tableRows.get(2), allOf(hasEntry("First Name", "Bill"),hasEntry("Last Name", "Oddie"), hasEntry("Favorite Colour","Blue")));
    }

    @Test
    public void should_ignore_data_in_missing_cells() {
        HtmlTable table = new HtmlTable(page.clients_with_missing_cells);

        List<Map<String, String>> tableRows = table.getRows();

        assertThat(tableRows.size(), is(3));
        assertThat(tableRows.get(0), allOf(hasEntry("First Name", "Tim"),hasEntry("Last Name", "Brooke-Taylor"), hasEntry("Favorite Colour","Red")));
        assertThat(tableRows.get(1), allOf(hasEntry("First Name", "Graeme"),hasEntry("Last Name", "Garden")));
        assertThat(tableRows.get(2), allOf(hasEntry("First Name", "Bill"),hasEntry("Last Name", "Oddie"), hasEntry("Favorite Colour","Blue")));
    }

    @Test
    public void should_find_row_elements_matching_a_given_criteria() {
        HtmlTable table = new HtmlTable(page.clients);

        List<WebElement> matchingRows = table.getRowElementsMatching(the("First Name", is("Tim")),the("Last Name", containsString("Taylor")));
        assertThat(matchingRows.size(), is(1));
        assertThat(matchingRows.get(0).getText(), containsString("Brooke-Taylor"));
    }

    @Test
    public void should_find_row_elements_matching_a_given_criteria_using_a_static_method() {
        HtmlTable table = new HtmlTable(page.clients);

        List<WebElement> matchingRows = HtmlTable.filterRows(page.clients, the("First Name", is("Tim")),the("Last Name", containsString("Taylor")));
        assertThat(matchingRows.size(), is(1));
        assertThat(matchingRows.get(0).getText(), containsString("Brooke-Taylor"));
    }

}
