package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.smart.SmartElementLocatorFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class ElementLocatorFactorySelector {

    private final int timeoutInSeconds;
    private final EnvironmentVariables environmentVariables;

    public ElementLocatorFactorySelector(Configuration configuration) {
        this(configuration.getElementTimeout(), configuration.getEnvironmentVariables());
    }

    public ElementLocatorFactorySelector(int timeoutInSeconds, EnvironmentVariables environmentVariables) {
        this.timeoutInSeconds = timeoutInSeconds;
        this.environmentVariables = environmentVariables.copy();
    }

    public ElementLocatorFactory getLocatorFor(WebDriver driver) {
        String locatorType = ThucydidesSystemProperty.LOCATOR_FACTORY.from(environmentVariables,"SmartElementLocatorFactory");
        if (locatorType.equals("DisplayedElementLocatorFactory")) {
            return new DisplayedElementLocatorFactory(driver, timeoutInSeconds);
        } else if (locatorType.equals("AjaxElementLocatorFactory")) {
            return new AjaxElementLocatorFactory(driver, timeoutInSeconds);
        } else if (locatorType.equals("DefaultElementLocatorFactory")) {
            return new DefaultElementLocatorFactory(driver);
        } else if (locatorType.equals("SmartElementLocatorFactory")){
        	return new SmartElementLocatorFactory(driver, timeoutInSeconds);
        
        } else {
            throw new IllegalArgumentException("Unsupported ElementLocatorFactory implementation: " + locatorType);
        }
    }

    public ElementLocatorFactorySelector withTimeout(int timeoutInSeconds) {
        return new ElementLocatorFactorySelector(timeoutInSeconds, environmentVariables);
    }
}
