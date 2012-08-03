package net.thucydides.core.requirements.reportpages

import org.openqa.selenium.WebDriver
import net.thucydides.core.pages.PageObject
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * A description goes here.
 * User: john
 * Date: 30/07/12
 * Time: 9:14 PM
 */
class RequirementsReport extends PageObject {

    static RequirementsReport inDirectory(File directory) {
        def driver = new HtmlUnitDriver();
        def report = new RequirementsReport(driver)
        report.openAt("file:///" +  directory.getAbsolutePath() + "/requirements.html");
        return report
    }

    RequirementsReport(WebDriver driver) {
        super(driver)
    }

    List<String> getNames() {
        driver.findElements(By.cssSelector(".requirementName")).collect { name ->
            name.text
        }
    }

    String getTableTitle() {
        find(By.tagName("h2")).text
    }

    List<RequirementRow> getRequirements() {
        List<WebElement> rows = driver.findElements(By.cssSelector("#req-results-table tbody tr"));
        rows.collect {
            List<WebElement> cells = it.findElements(By.tagName("td"))
            def iconImage = it.findElement(By.cssSelector(".summary-icon")).getAttribute("src")
            new RequirementRow(id: cells[1].text,
                               description : cells[2].text,
                               children: Integer.parseInt(cells[3].text),
                               tests: Integer.parseInt(cells[4].text),
                               icon: iconImage)
        }
    }

    class RequirementRow {
        String id
        String description
        String icon
        int children
        int tests
    }
}
