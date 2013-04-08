package net.thucydides.core.webdriver.smart;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.thucydides.core.annotations.DelayElementLocation;
import net.thucydides.core.annotations.implementedBy;
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
    	implementedBy implBy = interfaceType.getAnnotation(implementedBy.class);
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
	    	if (method.isAnnotationPresent(DelayElementLocation.class)) {
	    		Constructor<?> constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, long.class);
	            Object webElementFacadeExt = constructor.newInstance(driver, locator, timeoutInMilliseconds);
	            return method.invoke(implementerClass.cast(webElementFacadeExt), objects);
	        }
	        WebElement element = locator.findElement();
	
	        if ("getWrappedElement".equals(method.getName())) {
	            return element;
	        }
	        
	        Constructor<?> constructor = implementerClass.getConstructor(WebDriver.class,WebElement.class, long.class);
	        Object webElementFacadeExt = constructor.newInstance(driver, element, timeoutInMilliseconds);
	        //try {
	            return method.invoke(implementerClass.cast(webElementFacadeExt), objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
	
}

