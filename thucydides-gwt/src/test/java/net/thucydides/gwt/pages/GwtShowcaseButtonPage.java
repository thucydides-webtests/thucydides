package net.thucydides.gwt.pages;

import net.thucydides.core.annotations.At;
import net.thucydides.gwt.widgets.GwtButton;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@At("http://gwt.google.com/samples1/Showcase/Showcase.html#!CwBasicButton")
public class GwtShowcaseButtonPage extends GwtPageObject {

    public GwtShowcaseButtonPage(WebDriver driver) {
        super(driver);
    }

}
