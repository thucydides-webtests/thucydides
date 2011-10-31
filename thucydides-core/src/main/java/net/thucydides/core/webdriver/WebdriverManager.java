package net.thucydides.core.webdriver;

import net.thucydides.core.guice.Injectors;
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

    public void closeDriver();

}