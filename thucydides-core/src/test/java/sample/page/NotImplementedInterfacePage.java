package sample.page;

import org.openqa.selenium.WebDriver;

import sample.elements.WebElementFacadeInputImpl;
import sample.elements.WebElementFacadeNotImplemented;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;

public class NotImplementedInterfacePage extends PageObject{
	
	@FindBy(css = "#lastname")
	public WebElementFacadeNotImplemented elementLast;
	
	public NotImplementedInterfacePage(WebDriver driver) {
		super(driver);
	}
	
}
