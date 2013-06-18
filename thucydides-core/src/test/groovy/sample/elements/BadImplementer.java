package sample.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import net.thucydides.core.pages.WebElementFacadeImpl;

public class BadImplementer extends WebElementFacadeImpl{

	public BadImplementer(WebDriver driver, ElementLocator locator,
			long timeoutInMilliseconds) {
		super(driver, locator, timeoutInMilliseconds);
	}

}
