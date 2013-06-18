package sample.page;

import org.openqa.selenium.WebDriver;

import sample.elements.HasBadImplementer;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;

public class WithBadImplementerPage extends PageObject{

	@FindBy(css = "#firstname")
	public HasBadImplementer intefaceNotImplementedByClass;
	
	public WithBadImplementerPage(WebDriver driver) {
		super(driver);
	}

}
