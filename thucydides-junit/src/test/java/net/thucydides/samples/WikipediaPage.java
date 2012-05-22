package net.thucydides.samples;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

@DefaultUrl("http://www.wikipedia.org")
public class WikipediaPage extends PageObject {

    public WikipediaPage(WebDriver driver) {
        super(driver);
    }
}