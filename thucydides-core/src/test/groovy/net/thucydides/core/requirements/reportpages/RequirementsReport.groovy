package net.thucydides.core.requirements.reportpages

import net.thucydides.core.pages.WebElementFacade
import org.openqa.selenium.WebDriver
import net.thucydides.core.pages.PageObject
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Models the capabilities report page for testing purposes
 */
class RequirementsReport extends PageObject {

    static RequirementsReport inDirectory(File directory) {
        def driver = new HtmlUnitDriver();
        def report = new RequirementsReport(driver)
        report.openAt("file:///" +  directory.getAbsolutePath() + "/capabilities.html");
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
        List<WebElementFacade> titleTab = findAll(By.xpath("//div[@id='tabs']//a[@href='#tabs-1']"));
        return (!titleTab.isEmpty()) ? titleTab.get(0).getText() : ""
    }

    List<RequirementRow> getRequirements() {
        List<WebElement> rows = driver.findElements(By.cssSelector("#req-results-table .requirementRow"));
        rows.collect {
            List<WebElement> cells = it.findElements(By.cssSelector(".requirementRowCell"))
            def iconImage = it.findElement(By.cssSelector(".summary-icon")).getAttribute("src")
            new RequirementRow(id: cells[1].text,
                               description : cells[2].text,
                               children: Integer.parseInt(cells[3].text),
                               tests: Integer.parseInt(cells[4].text),
                               icon: iconImage)
        }
    }

    def close() {
        driver.close()
    }

    class RequirementRow {
        String id
        String description
        String icon
        int children
        int tests
    }
}
