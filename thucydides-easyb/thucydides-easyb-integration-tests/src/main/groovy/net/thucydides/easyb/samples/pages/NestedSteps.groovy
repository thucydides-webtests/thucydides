package net.thucydides.easyb.samples.pages;


import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps

class NestedSteps extends ScenarioSteps {

    NestedSteps(Pages pages) {
        super(pages)
    }

    @Step
    def enter_values(String selectValue, boolean checkboxValue) {
        println "Enter value $selectValue"
        IndexPage page = pages.currentPageAt(IndexPage)
        page.selectItem selectValue
        page.setCheckboxOption checkboxValue
    }

    @Step
    def fields_should_be_displayed() {
        IndexPage page = pages.currentPageAt(IndexPage)
        page.shouldBeVisible(page.multiselect)
    }

    @Step
    def should_display(String selectValue) {
        IndexPage page = pages.currentPageAt(IndexPage)
        page.shouldContainText(selectValue)
    }

    @Step
    def should_have_selected_value(String selectValue) {
        IndexPage page = pages.currentPageAt(IndexPage)
        if (!page.selectedValues.contains(selectValue)) {
            throw new AssertionError("Selected value $selectValue not in $page.selectedValues")
        }
    }

    @Step
    def should_not_have_selected_value(String selectValue) {
        IndexPage page = pages.currentPageAt(IndexPage)
        if (page.selectedValues.contains(selectValue)) {
            throw new AssertionError()
        }
    }

    @Step
    def trigger_exception() {
        assert true == false
    }

}