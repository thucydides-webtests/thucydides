package net.thucydides.easyb.samples.pages;


import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.pages.Pages
import net.thucydides.core.annotations.Step

class GoogleSearchSteps extends ScenarioSteps {

    GoogleSearchSteps(Pages pages) {
        super(pages)
    }

    @Step
    def search_for(String query) {
        GoogleSearchPage searchPage = pages.currentPageAt(GoogleSearchPage)
        searchPage.searchFor query
    }

    @Step
    def click_on_search_result_with_title(String title) {
        GoogleSearchResultsPage resultsPage = pages.currentPageAt(GoogleSearchResultsPage)
        resultsPage.clickOnSearchResult(title)
    }
}