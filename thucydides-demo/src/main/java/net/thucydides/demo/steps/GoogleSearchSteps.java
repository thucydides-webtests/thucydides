package net.thucydides.demo.steps;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import net.thucydides.core.pages.Pages;
import net.thucydides.demo.pages.GoogleHomePage;
import net.thucydides.demo.pages.GoogleResultsPage;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.steps.ScenarioSteps;

public class GoogleSearchSteps extends ScenarioSteps {
    
    public GoogleSearchSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void open_home_page() {
        System.out.println("Open home page");
        getPages().currentPageAt(GoogleHomePage.class);
    }

    @Step
    public void searchFor(String term) {
        System.out.println("searchFor " + term);
        GoogleHomePage page = (GoogleHomePage) getPages().currentPageAt(GoogleHomePage.class);
        page.searchFor(term);
    }
    
    @Step
    public void resultListShouldContain(String term) {
        System.out.println("resultListShouldContain " + term);
        GoogleResultsPage page = (GoogleResultsPage) getPages().currentPageAt(GoogleResultsPage.class);
        List<String> resultHeadings = page.getResultTitles();
        assertThat(resultHeadings, hasItem(containsString(term)));
    }
}
