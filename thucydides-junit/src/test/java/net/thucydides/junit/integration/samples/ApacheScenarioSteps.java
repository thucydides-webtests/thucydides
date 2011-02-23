package net.thucydides.junit.integration.samples;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageException;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.integration.pages.ApacheHomePage;
import net.thucydides.junit.integration.pages.ApacheProjectPage;
import net.thucydides.junit.steps.ScenarioSteps;

public class ApacheScenarioSteps extends ScenarioSteps {
    
    public ApacheScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void clickOnProjects() throws WrongPageException {
        ApacheHomePage page = (ApacheHomePage) getPages().currentPageAt(ApacheHomePage.class);
        page.clickOnProjects();
    }
    
    @Step
    public void clickOnCategories() throws WrongPageException {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnCategories();
    }

}
