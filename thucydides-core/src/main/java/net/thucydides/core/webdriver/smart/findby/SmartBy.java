package net.thucydides.core.webdriver.smart.findby;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class SmartBy extends By{

	/**
	* @param scLocator The scLocator to use
	* @return a MyBy which locates elements via AutoTest
	*/
	public static SmartBy sclocator(final String scLocator) {
	    if (scLocator == null)
	      throw new IllegalArgumentException(
	          "Cannot find elements when the scLocator expression is null.");

	    return new ByScLocator(scLocator);
	}
	
	public static class ByScLocator extends SmartBy {
	    private final String scLocator;

	    public ByScLocator(String scLocator) {
	      this.scLocator = scLocator;
	    }

	    @Override 
        public List<WebElement> findElements(SearchContext context) {
	    	throw new IllegalArgumentException(
	  	          "SmartGWT does not provide the functionality to find multiple elements");
//	    	return Collections.<WebElement>emptyList(); 
        }

        @Override 
        public WebElement findElement(SearchContext context) {
        		WebElement element;
        		try{
                	 element = (new WebDriverWait((WebDriver)context, 1))
                			.until(new ExpectedCondition<WebElement>() {
                				public WebElement apply(WebDriver driver){
                					try{
	                					return (WebElement) ((JavascriptExecutor)driver)
	                							.executeScript("return AutoTest.getElement(arguments[0]);", scLocator);
                					} catch (WebDriverException e) {
                						return null;
                					}
                				}
							});
        		} catch (TimeoutException e){        			
        			throw new NoSuchElementException("Cannot locate an element using "
        			          + toString());
        			
        		}
                return element;
        } 

	    @Override
	    public String toString() {
	      return "By.sclocator: " + scLocator;
	    }
	  }
	/**
	* @param jquery The jquery to use
	* @return a MyBy which locates elements via jQuery
	*/
	public static SmartBy jquery(final String jQuerySelector) {
	    if (jQuerySelector == null)
	      throw new IllegalArgumentException(
	          "Cannot find elements when the jQuerySelector expression is null.");

	    return new ByjQuerySelector(jQuerySelector);
	}
	
	public static class ByjQuerySelector extends SmartBy {
	    private final String jQuerySelector;

	    public ByjQuerySelector(String jQuerySelector) {
	      this.jQuerySelector = jQuerySelector;
	    }

	    @Override 
        public List<WebElement> findElements(SearchContext context) {
	    	List<WebElement> elements;
	    	try{
		    	elements = (new WebDriverWait((WebDriver)context, 1))
	        			.until(new ExpectedCondition<List<WebElement>>() {
	        				@SuppressWarnings("unchecked")
							public List<WebElement> apply(WebDriver driver){
	        					try{
	            					return (List<WebElement>) ((JavascriptExecutor)driver)
	            							.executeScript("var elements = $(arguments[0]).get(); return ((elements.length) ? elements : null)", jQuerySelector);
	        					} catch (WebDriverException e) {
	        						return null;
	        					}
	        				}
						});
	    	} catch (TimeoutException e){
	    		throw new NoSuchElementException("Cannot locate elements using " + toString());
	    	}
	    	return elements;
	    	
        }

        @Override 
        public WebElement findElement(SearchContext context) {
        	WebElement element;
	    	try{
		    	element = (new WebDriverWait((WebDriver)context, 1))
	        			.until(new ExpectedCondition<WebElement>() {
							public WebElement apply(WebDriver driver){
	        					try{
	            					return (WebElement) ((JavascriptExecutor)driver)
	            							.executeScript("return $(arguments[0]).get(0)", jQuerySelector);
	        					} catch (WebDriverException e) {
	        						return null;
	        					}
	        				}
						});
	    	} catch (TimeoutException e){
	    		throw new NoSuchElementException("Cannot locate an element using " + toString());
	    	}
	    	return element;
        } 

	    @Override
	    public String toString() {
	      return "By.jQuerySelector: " + jQuerySelector;
	    }
	  }
	

}
