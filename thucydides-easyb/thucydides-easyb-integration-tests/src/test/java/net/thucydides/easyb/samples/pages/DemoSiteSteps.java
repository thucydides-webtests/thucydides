package net.thucydides.easyb.samples.pages;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class DemoSiteSteps extends ScenarioSteps {

    public DemoSiteSteps(Pages pages) {
        super(pages);
    }

    @Steps
    public NestedSteps nestedSteps;

    @Step
    public void enter_values(String selectValue, boolean checkboxValue) {
        IndexPage page = getPages().get(IndexPage.class);
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
    }

    @Step
    public void fields_should_be_displayed() {
        IndexPage page = getPages().currentPageAt(IndexPage.class);
        page.shouldBeVisible(page.multiselect);
    }

    @Step
    public void should_display(String selectValue) {
        IndexPage page = getPages().currentPageAt(IndexPage.class);
        page.shouldContainText(selectValue);
    }

    @Step
    public void should_have_selected_value(String selectValue) {
        IndexPage page = getPages().currentPageAt(IndexPage.class);
        if (!page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError("Value " + selectValue + " not in " + page.getSelectedValues());
        }
    }

    @Step
    public void should_not_have_selected_value(String selectValue) {
        IndexPage page = getPages().currentPageAt(IndexPage.class);
        if (page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError();
        }
    }

//    @StepGroup("Use nested steps")
    @Step
    public void use_nested_steps() {
        nestedSteps.enter_values("Label 1", true);
        nestedSteps.should_have_selected_value("1");
    }

    @Step
    public void trigger_exception() {
        throw new AssertionError("It broke");
    }

}