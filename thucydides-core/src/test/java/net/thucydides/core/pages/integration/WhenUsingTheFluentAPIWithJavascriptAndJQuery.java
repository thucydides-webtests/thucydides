package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.jquery.ByJQuery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentAPIWithJavascriptAndJQuery  extends FluentElementAPITestsBaseClass {

    @BeforeClass
    public static void setupDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class, new WebDriverFactory());
        chromeDriver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
        chromeDriver.quit();
    }

    @Before
    public void openStaticPage() {
        page.setWaitForTimeout(5000);
        page.open();
    }


    @Test
    public void should_inject_jquery_into_the_page() {
        page.open();
        page.addJQuerySupport();
        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_not_inject_jquery_into_the_page_for_non_jquery_script() {
        StaticSitePage page = new StaticSitePage(driver, 1);
        page.open();
        page.evaluateJavascript("document.getElementById('firstname').focus()");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(false));
    }

    @Test
    public void should_work_correctly_in_the_page_with_jquery_already_present() {
        page.open("classpath:static-site/index-with-jquery.html");
        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_inject_jquery_into_the_page_in_chrome() {
        StaticSitePage page = new StaticSitePage(chromeDriver, 2000);
        page.open();
        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page_in_chrome() {

        StaticSitePage page = new StaticSitePage(chromeDriver, 2000);
        page.open();

        assertThat(page.element(page.firstName).hasFocus(), is(false));

        page.evaluateJavascript("$('#firstname').focus();");

        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page() {

        page.open();

        assertThat(page.element(page.firstName).hasFocus(), is(false));

        page.evaluateJavascript("$('#firstname').focus();");

        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_that_return_values_in_the_page() {

        page.open();

        assertThat(page.element(page.firstName).hasFocus(), is(false));

        Object result = page.evaluateJavascript("return $('#country').val();");

        assertThat(result.toString(), is("Australia"));
    }

    @Test
    public void should_be_able_to_find_an_element_using_a_jquery_expression() {
        page.open();
        page.addJQuerySupport();
        WebElement link = driver.findElement(ByJQuery.selector("a[title='Click Me']"));
        assertThat(link.isDisplayed(), is(true));
    }

    @Test
    public void should_be_able_to_find_multiple_elements_using_a_jquery_expression() {
        page.open();
        page.addJQuerySupport();
        List<WebElement> links = driver.findElements(ByJQuery.selector("h2"));
        assertThat(links.size(), is(2));
    }

    @Test(expected = NoSuchElementException.class)
    public void should_fail_gracefully_if_no_jquery_element_is_found() {
        page.open();
        page.addJQuerySupport();
        driver.findElement(ByJQuery.selector("a[title='Does Not Exist']"));
    }

    @Test(expected = NoSuchElementException.class)
    public void should_fail_gracefully_if_jquery_selector_is_invalid() {
        page.open();
        page.addJQuerySupport();
        driver.findElement(ByJQuery.selector("a[title='Does Not Exist'"));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        String result = (String) page.evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        page.open();
        assertThat(page.element(page.firstName).hasFocus(), is(false));
        page.evaluateJavascript("document.getElementById('firstname').focus()");
        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

}
