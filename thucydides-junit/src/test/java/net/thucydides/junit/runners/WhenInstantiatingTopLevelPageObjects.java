package net.thucydides.junit.runners;

import net.thucydides.core.annotations.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.IndexPage;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(ThucydidesRunner.class)
public class WhenInstantiatingTopLevelPageObjects {
    
    @Managed(driver = "htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    IndexPage page;

    @Test
    public void shouldInstantiateDeclaredPageObjects() throws Throwable {
        assertThat(page).isNotNull();
    }

    @Test
    public void shouldInstantiateNestedPageObjects() throws Throwable {
        assertThat(page).isNotNull();
        assertThat(page.nestedIndexPage).isNotNull();
    }

}
