package net.thucydides.core.webdriver;

import java.lang.reflect.Field;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.pages.WebElementFacadeImpl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class ThucydidesElementProxyCreator implements ElementProxyCreator{

	public void proxyElements(PageObject pageObject,
			WebDriver driver, int timeoutInSeconds) {
		 	ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(driver);
	        PageFactory.initElements(finder, pageObject);
	        initWebElementFacades(new WebElementFacadeFieldDecorator(finder), pageObject, driver);
	}
	
	public void proxyElements(PageObject pageObject,
			WebDriver driver) {
	ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(driver);
    PageFactory.initElements(finder, pageObject);
    initWebElementFacades(new WebElementFacadeFieldDecorator(finder), pageObject, driver);
	}


	private ElementLocatorFactorySelector getElementLocatorFactorySelector() {
	    Configuration configuration = Injectors.getInjector().getInstance(Configuration.class);
	    return new ElementLocatorFactorySelector(configuration);
	}

	
	private void initWebElementFacades(WebElementFacadeFieldDecorator decorator, PageObject page, final WebDriver driver) {
        Class<?> proxyIn = page.getClass();
        while (proxyIn != Object.class) {
            proxyFields(decorator, page, proxyIn, driver);
            proxyIn = proxyIn.getSuperclass();
        }
    }

    private void proxyFields(WebElementFacadeFieldDecorator decorator, PageObject page, Class<?> proxyIn, final WebDriver driver) {
        Field[] fields = proxyIn.getDeclaredFields();
        for (Field field : fields) {
            WebElement webElementValue = decorator.decorate(page.getClass().getClassLoader(), field);
            if (webElementValue != null) {
                try {
                    WebElementFacade facadeValue = new WebElementFacadeImpl(driver, webElementValue,page.waitForTimeoutInMilliseconds());
                    field.setAccessible(true);
                    field.set(page, facadeValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
	

}
