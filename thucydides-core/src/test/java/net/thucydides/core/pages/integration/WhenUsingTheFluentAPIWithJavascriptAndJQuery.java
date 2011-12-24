package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.jquery.ByJQuery;
import net.thucydides.core.webdriver.jquery.ByJQuerySelector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentAPIWithJavascriptAndJQuery  extends FluentElementAPITestsBaseClass {

    private static StaticSitePage chromePage;

    @BeforeClass
    public static void setupDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class, new WebDriverFactory());
        chromeDriver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
        chromePage = new StaticSitePage(chromeDriver, 1);
        page.open();
        page.addJQuerySupport();
        chromePage.open();
        chromePage.addJQuerySupport();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
        chromeDriver.quit();
    }

    public void refreshPage() {
        refresh(page);
        refresh(chromePage);
    }

    @Test
    public void should_inject_jquery_into_the_page() {
        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_work_correctly_in_the_page_with_jquery_already_present() {
        StaticSitePage page = new StaticSitePage(driver, 1);
        page.open("classpath:static-site/index-with-jquery.html");
        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_inject_jquery_into_the_page_in_chrome() {

        chromePage.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) chromePage.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page_in_chrome() {

        chromePage.evaluateJavascript("$('#firstname').focus();");

        assertThat(chromePage.element(chromePage.firstName).hasFocus(), is(true));

        chromePage.evaluateJavascript("$('#lastname').focus();");

        assertThat(chromePage.element(chromePage.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page() {

        page.evaluateJavascript("$('#firstname').focus();");

        assertThat(page.element(page.firstName).hasFocus(), is(true));

        page.evaluateJavascript("$('#lastname').focus();");

        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_that_return_values_in_the_page() {

        Object result = page.evaluateJavascript("return $('#country').val();");

        assertThat(result.toString(), is("Australia"));
    }

    @Test
    public void should_be_able_to_find_an_element_using_a_jquery_expression() {
        WebElement link = driver.findElement(ByJQuery.selector("a[title='Click Me']"));
        assertThat(link.isDisplayed(), is(true));
    }

    @Test
    public void should_be_able_to_find_multiple_elements_using_a_jquery_expression() {
        List<WebElement> links = driver.findElements(ByJQuery.selector("h2"));
        assertThat(links.size(), is(2));
    }

    @Test(expected = NoSuchElementException.class)
    public void should_fail_gracefully_if_no_jquery_element_is_found() {
        driver.findElement(ByJQuery.selector("a[title='Does Not Exist']"));
    }

    @Test(expected = NoSuchElementException.class)
    public void should_fail_gracefully_if_jquery_selector_is_invalid() {
        driver.findElement(ByJQuery.selector("a[title='Does Not Exist'"));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        String result = (String) page.evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        assertThat(page.element(page.firstName).hasFocus(), is(false));
        page.evaluateJavascript("document.getElementById('firstname').focus()");
        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }


    @Test
    public void a_jquery_selector_should_be_described_by_the_corresponding_jquery_expression() {
        ByJQuerySelector jQuerySelector = ByJQuery.selector("a[title='Click Me']");
        
        assertThat(jQuerySelector.toString(), containsString("a[title='Click Me']"));
    }
}
