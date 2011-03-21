package net.thucydides.gwt.pages;

import net.thucydides.gwt.widgets.GwtButton;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class GwtShowcaseButtonPage extends GwtPageObject {

    public GwtShowcaseButtonPage(WebDriver driver) {
        super(driver);
    }

    public GwtButton getButtonLabelled(String label) {
        String buttonXPath = String.format("//button[contains(.,'%s')]", label);
        WebElement button = getDriver().findElement(By.xpath(buttonXPath));
        return new GwtButton(label, button);
    }

}
