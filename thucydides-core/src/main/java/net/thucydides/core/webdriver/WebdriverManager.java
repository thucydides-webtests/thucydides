package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 * 
 * @author johnsmart
 *
 */
public interface WebdriverManager {

    public WebDriver getWebdriver();

    public WebDriver getWebdriver(final String driver);

    public void closeDriver();

}