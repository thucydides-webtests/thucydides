package net.thucydides.core.pages;

import net.thucydides.core.webdriver.ThucydidesWebDriverEventListener;
import org.openqa.selenium.WebDriver;

/**
 * Opens the browser to the default page URL as soon as it is opened.
 */
public class PagesEventListener implements ThucydidesWebDriverEventListener {

    private final Pages pages;

    public PagesEventListener(final Pages pages) {
        this.pages = pages;
    }

    public void driverCreatedIn(final WebDriver driver) {
    }
}
