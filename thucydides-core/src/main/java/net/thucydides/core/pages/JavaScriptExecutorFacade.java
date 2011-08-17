package net.thucydides.core.pages;

import net.thucydides.core.webdriver.WebDriverFacade;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Simple encapsulation of Javascript execution.
 */
public class JavaScriptExecutorFacade {
    private WebDriver driver;

    public JavaScriptExecutorFacade(final WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Execute some Javascript in the underlying WebDriver driver.
     * @param script
     * @return
     */
    public Object executeScript(final String script) {
        WebDriverFacade driverFacade = (WebDriverFacade) driver;
        JavascriptExecutor js = (JavascriptExecutor) driverFacade.getProxiedDriver();
        return js.executeScript(script);
    }

}
