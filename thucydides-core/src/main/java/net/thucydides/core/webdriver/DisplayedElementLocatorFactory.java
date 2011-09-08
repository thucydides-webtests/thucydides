package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;

class DisplayedElementLocatorFactory extends AjaxElementLocatorFactory {
    private final WebDriver driver;
    private final int timeOutInSeconds;
    public DisplayedElementLocatorFactory(WebDriver driver, int timeOutInSeconds) {
        super(driver, timeOutInSeconds);
        this.driver = driver;
        this.timeOutInSeconds = timeOutInSeconds;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new DisplayedElementLocator(driver, field, timeOutInSeconds);    //To change body of overridden methods use File | Settings | File Templates.
    }
}