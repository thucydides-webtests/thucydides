package net.thucydides.junit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageException;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.ManagedPages;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.integration.pages.ApacheHomePage;
import net.thucydides.junit.integration.pages.ApacheProjectPage;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(ThucydidesRunner.class)
public class WhenManagingPagesUsingTheManagedPagesAnnotation {

    @Managed
    public WebDriver driver;

    @ManagedPages(defaultUrl="http://www.apache.org/")
    public Pages pages;
    
    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test @Step(1)
    public void the_annotated_pages_object_should_be_instantiated_automatically() {
        assertThat(pages, is(not(nullValue())));
    }    

    @Test @Step(2)
    public void the_default_url_should_be_used_if_no_system_property_value_is_provided() {
        pages.openHomePage();
        assertThat(driver.getCurrentUrl(), is("http://www.apache.org/"));
    }    
    
    @Test  @Step(3)
    public void the_system_property_should_be_used_if_provided() {
        System.setProperty("webdriver.base.url", "http://projects.apache.org");
        pages.openHomePage();
        assertThat(driver.getCurrentUrl(), is("http://projects.apache.org/"));
    }    
    
    @Test  @Step(4)
    public void the_pages_objects_manager_should_know_what_page_object_you_should_use() throws WrongPageException {
        pages.openHomePage();
        ApacheHomePage page = (ApacheHomePage) pages.currentPageAt(ApacheHomePage.class);
        
        assertThat(page, is(not(nullValue())));
    }    

    @Test @Step(5)
    public void when_you_go_to_another_page_it_should_return_the_corresponding_next_page_object() throws WrongPageException {
        ApacheHomePage page = (ApacheHomePage) pages.currentPageAt(ApacheHomePage.class);
        page.clickOnProjects();
        
        ApacheProjectPage apacheProjectPage = (ApacheProjectPage) pages.currentPageAt(ApacheProjectPage.class);
        assertThat(apacheProjectPage, is(not(nullValue())));
    }    
    
    @Test(expected=WrongPageException.class)  @Step(6)
    public void if_you_are_not_at_the_right_page_it_will_raise_an_exception() throws WrongPageException {

        ApacheHomePage apacheProjectPage = (ApacheHomePage) pages.currentPageAt(ApacheHomePage.class);
        assertThat(apacheProjectPage, is(not(nullValue())));
    }    

}
