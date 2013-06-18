package sample.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import net.thucydides.core.pages.WebElementFacadeImpl;

public class WebElementFacadeInputImpl extends WebElementFacadeImpl implements WebElementFacadeInput{

	public WebElementFacadeInputImpl(WebDriver driver, ElementLocator locator,
			long timeoutInMilliseconds) {
		super(driver, locator, timeoutInMilliseconds);
	}

	@Override
	public void enterText(String text) {
		getElement().sendKeys(text);
		
	}
	
	
}