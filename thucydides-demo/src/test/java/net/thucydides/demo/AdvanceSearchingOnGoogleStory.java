package net.thucydides.demo;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.demo.steps.GoogleSearchSteps;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.ManagedPages;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class AdvanceSearchingOnGoogleStory {

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public GoogleSearchSteps steps;

    @Test
    public void searching_for_cats_should_find_the_wikipedia_entry() {
        steps.open_google_and_search_for("cats");
        steps.resultListShouldContain("Cat - Wikipedia, the free encyclopedia");
    }
    
    @Test
    public void searching_for_dogs_should_find_the_wikipedia_entry() {
        steps.open_google_and_search_for("dogs");
        steps.resultListShouldContain("Dog - Wikipedia, the free encyclopedia");
    }
    
    @Test
    public void searching_for_hamsters_should_find_the_wikipedia_entry() {
        steps.open_google_and_search_for("hampsters");
        steps.resultListShouldContain("Hamster - Wikipedia, the free encyclopedia");
    }
}
