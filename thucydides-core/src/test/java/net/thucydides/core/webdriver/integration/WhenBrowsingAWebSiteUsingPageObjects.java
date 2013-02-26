package net.thucydides.core.webdriver.integration;

import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.PageUrls;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class WhenBrowsingAWebSiteUsingPageObjects {

  public class IndexPage extends PageObject {

    public WebElement multiselect;

    public WebElement checkbox;

    public WebElement color;

    public WebElementFacade firstname;

    @FindBy(name = "specialField")
    public WebElementFacade extra;

    WebElementFacade checkbox() {
      return element(checkbox);
    }

    public IndexPage(WebDriver driver, int timeout) {
      super(driver, timeout);
    }
  }

  public class IndexPageWithShortTimeout extends PageObject {

    public WebElement multiselect;

    public WebElement checkbox;

    public IndexPageWithShortTimeout(WebDriver driver, int timeout) {
      super(driver, 1);
    }
  }

  WebDriver driver;

  static WebDriver firefoxDriver;

  IndexPage indexPage;

  MockEnvironmentVariables environmentVariables;

  Configuration configuration;

  @Before
  public void openLocalStaticSite() {
    driver = new HtmlUnitDriver();
    openStaticTestSite();
    indexPage = new IndexPage(driver, 1);
    indexPage.setWaitForTimeout(100);
  }

  @Before
  public void initConfiguration() {
    environmentVariables = new MockEnvironmentVariables();
    configuration = new SystemPropertiesConfiguration(environmentVariables);
  }


  @After
  public void closeDriver() {
    if (firefoxDriver != null) {
      firefoxDriver.quit();
    }
  }

  private void openStaticTestSite() {
    File baseDir = new File(System.getProperty("user.dir"));
    File testSite = new File(baseDir, "src/test/resources/static-site/index.html");
    this.driver.get("file://" + testSite.getAbsolutePath());
  }

  @Test
  public void should_find_page_title() {
    assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
  }

  @Test
  public void should_print_web_element_facades_in_a_readable_form() {

    assertThat(indexPage.checkbox()
        .toString(), is("<input id=\"checkbox\" type=\"checkbox\" value=\"checkbox_value\" />"));
  }

  @Test
  public void should_print_web_element_facade_without_a_webelement_in_a_readable_form() {

    WebElementFacade WebElement = new WebElementFacade(driver, null, 0);
    assertThat(WebElement.toString(), is("<Undefined web element>"));
  }

  @Test
  public void should_find_text_contained_in_page() {
    indexPage.shouldContainText("Some test pages");
  }

  @Test(expected = NoSuchElementException.class)
  public void should_not_find_text_not_contained_in_page() {
    indexPage.shouldContainText("This text is not in the pages");
  }

  @Test
  public void should_select_in_multiple_select_lists_correctly() {
    indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 1", "Label 3");

    Set<String> selectedLabels = indexPage.getSelectedOptionLabelsFrom(indexPage.multiselect);
    assertThat(selectedLabels.size(), is(2));
    assertThat(selectedLabels, hasItems("Label 1", "Label 3"));
  }

  @Test
  public void should_select_values_in_multiple_select_lists_correctly() {

    indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 1", "Label 3");

    Set<String> selectedValues = indexPage.getSelectedOptionValuesFrom(indexPage.multiselect);
    assertThat(selectedValues.size(), is(2));
    assertThat(selectedValues, hasItems("1", "3"));
  }

  @Test
  public void should_return_selected_value_in_select() {

    indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 2");
    String selectedValue = indexPage.getSelectedValueFrom(indexPage.multiselect);
    assertThat(selectedValue, is("2"));
  }

  @Test
  public void should_return_selected_label_in_select() {

    indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect, "Label 2");
    String selectedLabel = indexPage.getSelectedLabelFrom(indexPage.multiselect);
    assertThat(selectedLabel, is("Label 2"));
  }

  @Test
  public void should_select_values_in_select() {
    indexPage.selectFromDropdown(indexPage.color, "Red");
    assertThat(indexPage.getSelectedOptionValuesFrom(indexPage.color), hasItem("red"));
  }

  @Test
  public void ticking_an_empty_checkbox_should_set_the_value_to_true() {
    indexPage.setCheckbox(indexPage.checkbox, true);

    assertThat(indexPage.checkbox.isSelected(), is(true));
  }

  @Test
  public void ticking_a_set_checkbox_should_set_the_value_to_true() {
    if (indexPage.checkbox.isSelected()) {
      indexPage.checkbox.click();
    }

    indexPage.setCheckbox(indexPage.checkbox, true);

    assertThat(indexPage.checkbox.isSelected(), is(true));
  }

  @Test
  public void unticking_an_unset_checkbox_should_set_the_value_to_false() {

    indexPage.setCheckbox(indexPage.checkbox, false);

    assertThat(indexPage.checkbox.isSelected(), is(false));
  }

  @Test
  public void unticking_a_set_checkbox_should_set_the_value_to_false() {
    if (indexPage.checkbox.isSelected()) {
      indexPage.checkbox.click();
    }

    indexPage.setCheckbox(indexPage.checkbox, false);

    assertThat(indexPage.checkbox.isSelected(), is(false));
  }


  @Test
  public void should_know_when_text_appears_on_a_page() {

    indexPage.waitForTextToAppear("Label 1");
  }

  @Test
  public void should_know_when_an_element_is_visible() {
    indexPage.getDriver().navigate().refresh();
    assertThat(indexPage.isElementVisible(By.id("visible")), is(true));
  }

  @Test(expected = TimeoutException.class)
  public void should_fail_if_text_does_not_appear_on_a_page() {

    indexPage.waitForTextToAppear("Label that is not present");
  }

  @Test
  public void should_know_when_one_of_several_texts_appears_on_a_page() {
    indexPage.waitForAnyTextToAppear("Label 1", "Label that is not present");
  }

  @Test(expected = TimeoutException.class)
  public void should_fail_if_the_requested_text_is_not_on_the_page() {
    indexPage.waitForAnyTextToAppear("Label that is not present");
  }

  @Test
  public void should_know_when_all_of_a_set_of_texts_appears_on_a_page() {
    indexPage.waitForAllTextToAppear("Label 1", "Label 2");
  }

  @Test(expected = TimeoutException.class)
  public void should_fail_if_one_of_a_set_of_requested_texts_does_not_appear_on_a_page() {
    indexPage.waitForAllTextToAppear("Label 1", "Label that is not present");
  }

  @Test(expected = TimeoutException.class)
  public void should_fail_if_none_of_the_requested_texts_appear_on_a_page() {
    indexPage.waitForAllTextToAppear("Label that is not present", "Another label that is not present");
  }

  @Test
  public void should_initialize_a_web_element_facade_by_name_or_id()
  {
    assertNotNull(indexPage.firstname);
    assertThat(indexPage.firstname.getValue(), is("<enter first name>"));
  }

  @Test
  public void should_initialize_a_web_element_facade_by_annotation()
  {
    assertNotNull(indexPage.extra);
    assertThat(indexPage.extra.getValue(), is("Special"));
  }


  @Test
  public void the_page_can_be_read_from_a_file_on_the_classpath() {

    IndexPageWithShortTimeout indexPage = new IndexPageWithShortTimeout(driver, 1);

    assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
  }

  @Test
  public void the_page_can_be_opened_using_an_unsecure_certificates_compatible_profile() {

    environmentVariables.setProperty("webdriver.driver", "firefox");
    environmentVariables.setProperty("refuse.untrusted.certificates", "true");

    IndexPageWithShortTimeout indexPage = new IndexPageWithShortTimeout(driver, 1);
    PageUrls pageUrls = new PageUrls(indexPage, configuration);
    indexPage.setPageUrls(pageUrls);

    assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
  }

  public class FluentPage extends PageObject {

    public WebElement state;

    public FluentPage(WebDriver driver) {
      super(driver);
    }

    public void setState(String stateValue) {
      fluent().fill("#state").with(stateValue);
    }

    public String getStateValue() {
      return $(state).getValue();
    }
  }


  @Test
  public void the_page_should_support_fluentlenium() {
    File baseDir = new File(System.getProperty("user.dir"));
    File testSite = new File(baseDir, "src/test/resources/static-site/index.html");
    driver.get("file://" + testSite.getAbsolutePath());

    FluentPage page = new FluentPage(driver);
    page.setState("NSW");
    assertThat(page.getStateValue(), is("NSW"));
  }
}
