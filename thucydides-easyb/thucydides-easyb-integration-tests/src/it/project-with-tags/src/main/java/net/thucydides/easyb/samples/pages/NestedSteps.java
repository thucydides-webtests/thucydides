package net.thucydides.easyb.samples.pages;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class NestedSteps extends ScenarioSteps {

    public NestedSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
    }

    @Step
    public void fields_should_be_displayed() {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.shouldBeVisible(page.multiselect);
    }

    @Step
    public void should_display(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.shouldContainText(selectValue);
    }

    @Step
    public void should_have_selected_value(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        if (!page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError("Selected value $selectValue not in $page.selectedValues");
        }
    }

    @Step
    public void should_not_have_selected_value(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        if (page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError();
        }
    }

    @Step
    public void trigger_exception() {
        assert true == false;
    }

}