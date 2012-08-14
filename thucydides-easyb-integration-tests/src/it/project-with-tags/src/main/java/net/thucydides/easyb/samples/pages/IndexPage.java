package net.thucydides.easyb.samples.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

@DefaultUrl("classpath:demosite/index.html")
public class IndexPage extends PageObject {

    public WebElement multiselect;

    public WebElement checkbox;

    public IndexPage(WebDriver driver) {
        super(driver);
    }
    
    public void selectItem(String option) {
        this.selectFromDropdown(multiselect, option);
    }

    public Set<String> getSelectedValues() {
        return this.getSelectedOptionValuesFrom(multiselect);
    }

    public void setCheckboxOption(boolean value) {
        this.setCheckbox(checkbox, value);
    }

}