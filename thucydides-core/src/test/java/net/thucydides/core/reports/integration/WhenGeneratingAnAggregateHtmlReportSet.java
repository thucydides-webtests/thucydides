package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.reports.html.ReportProperties;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static net.thucydides.core.matchers.FileMatchers.exists;
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenGeneratingAnAggregateHtmlReportSet {

    private static File outputDirectory;

    WebDriver driver;
    
    @BeforeClass
    public static void generateReports() throws IOException {
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project");
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);

        File sourceDirectory = directoryInClasspathCalled("/test-outcomes/containing-failure");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);
    }

    @AfterClass
    public static void deleteReportDirectory() {
        outputDirectory.delete();
    }

    private static File newTemporaryDirectory() throws IOException {
        File createdFolder= File.createTempFile("reports", "");
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    @Before
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
        driver = new HtmlUnitDriver();
    }

    @Test
    public void should_generate_an_aggregate_dashboard() throws Exception {
        assertThat(new File(outputDirectory,"index.html"), exists());
    }

    @Test
    public void should_generate_overall_passed_failed_and_pending_reports() throws Exception {
        assertThat(new File(outputDirectory,"result_success.html"), exists());
        assertThat(new File(outputDirectory,"result_pending.html"), exists());
        assertThat(new File(outputDirectory,"result_pending.html"), exists());
    }

    @Test
    public void should_generate_overall_passed_failed_and_pending_reports_for_each_tag() throws Exception {
        assertThat(new File(outputDirectory,"context_a_feature_result_success.html"), exists());
        assertThat(new File(outputDirectory,"context_a_feature_result_pending.html"), exists());
        assertThat(new File(outputDirectory,"context_a_feature_result_pending.html"), exists());
    }

    @Test
    public void should_generate_an_aggregate_report_for_each_tag() throws Exception {
        assertThat(new File(outputDirectory,"tag_a_feature.html"), exists());
        assertThat(new File(outputDirectory,"tag_a_story.html"), exists());
        assertThat(new File(outputDirectory,"tag_another_story.html"), exists());
        assertThat(new File(outputDirectory,"tag_another_different_story.html"), exists());
        assertThat(new File(outputDirectory,"tag_an_epic.html"), exists());
    }

    @Test
    public void should_generate_an_aggregate_report_for_tags_in_each_tag_type() throws Exception {
        assertThat(new File(outputDirectory,"context_a_feature_tag_a_story.html"), exists());
        assertThat(new File(outputDirectory,"context_a_feature_tag_another_story.html"), exists());
        assertThat(new File(outputDirectory,"context_an_epic_tag_another_different_story.html"), exists());
    }

    @Test
    public void should_generate_a_summary_report_for_each_tag_type() throws Exception {
        assertThat(new File(outputDirectory,"tagtype_feature.html"), exists());
        assertThat(new File(outputDirectory,"tagtype_story.html"), exists());
        assertThat(new File(outputDirectory,"tagtype_epic.html"), exists());
    }

    @Test
    public void aggregate_dashboard_should_contain_a_list_of_all_tag_types() throws Exception {

        File report = new File(outputDirectory,"index.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.cssSelector(".tagTypeTitle"));
        List<String> tagTypeNames = extract(tagTypes, on(WebElement.class).getText());
        assertThat(tagTypeNames, hasItems("Stories","Features", "Epics"));
    }

    private String urlFor(File report) {
        return "file:///" + report.getAbsolutePath();
    }

    @Test
    public void aggregate_dashboard_should_contain_a_list_of_all_tags() throws Exception {

        File report = new File(outputDirectory,"index.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.cssSelector(".tagTitle"));
        List<String> tagTypeNames = extract(tagTypes, on(WebElement.class).getText());
        assertThat(tagTypeNames, hasItems("A Story","A Feature", "An Epic", "Another Different Story"));
    }

    @Test
    public void aggregate_dashboard_should_contain_links_to_associated_detailed_tag_type_reports() throws Exception {

        File report = new File(outputDirectory,"index.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.xpath("//div[@class='tagTitle']//a"));
        List<String> tagTypeLinks = extract(tagTypes, on(WebElement.class).getAttribute("href"));
        assertThat(tagTypeLinks, hasItems(endsWith("tag_a_feature.html"),
                                        endsWith("tag_a_story.html"),
                                        endsWith("tag_an_epic.html"),
                                        endsWith("tag_another_story.html"),
                                        endsWith("tag_another_different_story.html")));
    }

    @Test
    public void aggregate_dashboard_should_contain_links_to_all_overview_tag_type_reports() throws Exception {

        File report = new File(outputDirectory,"index.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.xpath("//div[@class='menu']//a"));
        List<String> tagTypeLinks = extract(tagTypes, on(WebElement.class).getAttribute("href"));
        assertThat(tagTypeLinks, hasItems(endsWith("tagtype_feature.html"),
                endsWith("tagtype_story.html"),
                endsWith("tagtype_epic.html")));
    }

    @Test
    public void tagtype_overview_report_should_contain_links_to_all_other_overview_tag_type_reports() throws Exception {

        File report = new File(outputDirectory,"tagtype_feature.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.xpath("//div[@class='menu']//a"));
        List<String> tagTypeLinks = extract(tagTypes, on(WebElement.class).getAttribute("href"));
        assertThat(tagTypeLinks, hasItems(endsWith("tagtype_feature.html"),
                endsWith("tagtype_story.html"),
                endsWith("tagtype_epic.html")));
    }

    @Test
    public void story_report_should_contain_links_to_associated_result_reports() throws Exception {

        File report = new File(outputDirectory,"tag_a_story.html");
        driver.get(urlFor(report));

        WebElement passedLink = driver.findElement(By.xpath("//a[.='passed']"));
        String resultPage = passedLink.getAttribute("href");
        assertThat(resultPage, endsWith("/context_a_story_result_success.html"));
    }

    @Test
    public void nested_test_result_report_should_not_contain_result_links() throws Exception {

        File report = new File(outputDirectory,"context_a_feature_result_success.html");
        driver.get(urlFor(report));

        List<WebElement> passedLinks = driver.findElements(By.xpath("//a[.='passed']"));
        assertThat(passedLinks.size(), is(0));
    }

    @Test
    public void aggregate_report_should_contain_links_to_overall_result_reports() throws Exception {

        File report = new File(outputDirectory,"index.html");
        driver.get(urlFor(report));

        WebElement passedLink = driver.findElement(By.xpath("//a[.='passed']"));
        String resultPage = passedLink.getAttribute("href");
        assertThat(resultPage, endsWith("/result_success.html"));
    }

    @Test
    public void should_not_display_links_to_test_result_reports_in_test_result_reports() {
        ReportProperties reportProperties = ReportProperties.forTestResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(false));
    }

    @Test
    public void should_display_links_to_test_result_reports_in_tag_reports() {
        ReportProperties reportProperties = ReportProperties.forTagResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(true));
    }

    @Test
    public void should_display_links_to_test_result_reports_in_top_level_reports() {
        ReportProperties reportProperties = ReportProperties.forAggregateResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(true));
    }
}
