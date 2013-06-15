package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.thucydides.core.annotations.ImplementedBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;


public class SmartElementHandler implements InvocationHandler{	
    private final ElementLocator locator;
    private final WebDriver driver;
    private final Class<?> implementerClass;
    private final long timeoutInMilliseconds;
    
    private Class<?> getImplementer(Class<?> interfaceType){
    	ImplementedBy implBy = interfaceType.getAnnotation(ImplementedBy.class);
    	Class<?> implementerClass = implBy.value();
    	if (!interfaceType.isAssignableFrom(implementerClass)) {
    		throw new RuntimeException("implementer Class does not implement the interface " + interfaceType.getName());
    	}
    	return implementerClass;
    }

    public SmartElementHandler(Class<?> interfaceType, ElementLocator locator,
			WebDriver driver, long timeoutInMilliseconds) {
    	this.driver = driver;
        this.locator = locator;
        if (!WebElementFacade.class.isAssignableFrom(interfaceType)) {
            throw new RuntimeException("interface not assignable to WebElementFacade");
        }
        
        this.implementerClass = getImplementer(interfaceType);
        this.timeoutInMilliseconds = timeoutInMilliseconds; 
    }

	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
    	try {
	        if ("getWrappedElement".equals(method.getName())) {
	            return locator.findElement();
	        }
	        Constructor<?> constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, long.class);
	        Object webElementFacadeExt = constructor.newInstance(driver, locator, timeoutInMilliseconds);
	        
	        return method.invoke(implementerClass.cast(webElementFacadeExt), objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
	
}

