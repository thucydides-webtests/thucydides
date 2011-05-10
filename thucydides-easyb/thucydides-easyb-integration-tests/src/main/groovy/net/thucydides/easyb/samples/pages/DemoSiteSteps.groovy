package net.thucydides.easyb.samples.pages;


import net.thucydides.core.annotations.Step
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.annotations.Steps
import net.thucydides.core.annotations.StepGroup

class DemoSiteSteps extends ScenarioSteps {

    DemoSiteSteps(Pages pages) {
        super(pages)
    }


    @Steps
    public NestedSteps nestedSteps;


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

    @StepGroup("Use nested steps")
    def use_nested_steps() {
        nestedSteps.enter_values('Label 1', true)
        nestedSteps.enter_values('Label 2', true)
        nestedSteps.enter_values('Label 3', true)
    }

    @Step
    def trigger_exception() {
        assert true == false
    }

}