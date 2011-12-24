package net.thucydides.core.webdriver.javascript;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

/**
 * Simple encapsulation of Javascript execution.
 */
public class JavascriptExecutorFacade {
    private WebDriver driver;

    public JavascriptExecutorFacade(final WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Execute some Javascript in the underlying WebDriver driver.
     * @param script
     * @return
     */
    public Object executeScript(final String script) {
        if (javascriptIsSupportedIn(driver)) {
            JavascriptExecutor js = getJavascriptEnabledDriver();
            return js.executeScript(script);
        } else {
            return null;
        }
    }

    private WebDriver getRealDriver() {
        if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
            WebDriverFacade driverFacade = (WebDriverFacade) driver;
            return driverFacade.getProxiedDriver();
        } else {
            return driver;
        }
    }

    private JavascriptExecutor getJavascriptEnabledDriver() {
        return (JavascriptExecutor) getRealDriver();
    }

}
