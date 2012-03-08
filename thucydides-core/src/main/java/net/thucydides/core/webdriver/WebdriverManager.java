package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

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

    public SessionId getSessionId();

    public void closeDriver();

    public void closeAllDrivers();

    public void resetDriver();

}