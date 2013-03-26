package net.thucydides.core.webdriver.smart;

import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public final class SmartElementLocatorFactory implements ElementLocatorFactory {
	  private final WebDriver webDriver;
	  private int timeoutInSeconds;

	  public SmartElementLocatorFactory(WebDriver webDriver, int timeoutInSeconds) {
	    this.webDriver = webDriver;
	    this.timeoutInSeconds = timeoutInSeconds;
	  }

	  public ElementLocator createLocator(Field field) {
	    return new SmartAjaxElementLocator(webDriver, field, timeoutInSeconds);
	  }
}
