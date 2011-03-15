package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageException;

public class SimpleScenarioSteps extends ScenarioSteps {
    
    public SimpleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void clickOnProjects() throws WrongPageException {
    }
    
    @Step
    public void clickOnCategories() throws WrongPageException {
    }

    @Step
    public void clickOnInexistantLink() throws WrongPageException {
    }

    @Step
    public void clickOnProjectAndCheckTitle() throws WrongPageException {
    }

    @Step @Pending
    public void notImplementedYet() throws WrongPageException {}
}
