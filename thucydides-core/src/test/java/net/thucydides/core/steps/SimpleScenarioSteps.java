package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;

public class SimpleScenarioSteps extends ScenarioSteps {
    
    public SimpleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void clickOnProjects() throws WrongPageError {
    }
    
    @Step
    public void clickOnCategories() throws WrongPageError {
    }

    @Step
    public void clickOnInexistantLink() throws WrongPageError {
    }

    @Step
    public void clickOnProjectAndCheckTitle() throws WrongPageError {
    }

    @Step @Pending
    public void notImplementedYet() throws WrongPageError {}
}
