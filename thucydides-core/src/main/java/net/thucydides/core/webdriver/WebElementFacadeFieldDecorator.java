package net.thucydides.core.webdriver;

import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public class WebElementFacadeFieldDecorator extends DefaultFieldDecorator {


    public WebElementFacadeFieldDecorator(ElementLocatorFactory factory) {
        super(factory);
    }


    @Override
    public WebElement decorate(ClassLoader classLoader, Field field) {

        if (! WebElementFacade.class.isAssignableFrom(field.getType())) {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        return proxyForLocator(classLoader, locator);
    }
}
