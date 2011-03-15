package net.thucydides.junit.integration.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageException;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.junit.annotations.TestsRequirement;
import net.thucydides.junit.integration.pages.ApacheHomePage;
import net.thucydides.junit.integration.pages.ApacheProjectPage;

public class ApacheScenarioSteps extends ScenarioSteps {
    
    public ApacheScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    @TestsRequirement("R123-1") 
    public void clickOnProjects() throws WrongPageException {
        ApacheHomePage page = (ApacheHomePage) getPages().currentPageAt(ApacheHomePage.class);
        page.clickOnProjects();
    }
    
    @Step
    @TestsRequirement("R123-2") 
    public void clickOnCategories() throws WrongPageException {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnCategories();
    }

    @Step
    public void clickOnInexistantLink() throws WrongPageException {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnCategories();
    }

    @Step
    public void clickOnProjectAndCheckTitle() throws WrongPageException {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnProjectsAndCheckTitle();
    }

    @Step @Pending
    public void notImplementedYet() throws WrongPageException {}
}
