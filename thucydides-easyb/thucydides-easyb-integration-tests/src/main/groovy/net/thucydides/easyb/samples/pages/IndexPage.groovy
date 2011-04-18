package net.thucydides.easyb.samples.pages;


import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.pages.PageObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

@DefaultUrl("classpath:demosite/index.html")
class IndexPage extends PageObject {

    public WebElement multiselect;

    public WebElement checkbox;

    public IndexPage(WebDriver driver) {
        super(driver);
    }
    
    def selectItem(String option) {
        this.selectFromDropdown(multiselect, option)
    }

    def getSelectedValues() {
        this.getSelectedOptionValuesFrom(multiselect)
    }
    def setCheckboxOption(boolean value) {
        this.setCheckbox(checkbox, value);
    }

}