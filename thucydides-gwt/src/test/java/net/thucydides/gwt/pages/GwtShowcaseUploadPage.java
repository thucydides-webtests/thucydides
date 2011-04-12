package net.thucydides.gwt.pages;

import net.thucydides.core.annotations.At;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@At("http://gwt.google.com/samples1/Showcase/Showcase.html#!CwFileUpload")
public class GwtShowcaseUploadPage extends GwtPageObject {

    @FindBy(id="gwt-debug-cwFileUpload")
    public WebElement fileUploadField;

    public GwtShowcaseUploadPage(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String filename) {
        if (filename.startsWith("/")) {
            uploadFileFromResourcePath(filename).to(fileUploadField);
        } else {
            uploadFileFromFileSystem(filename).to(fileUploadField);
        }
        getButtonLabelled("Upload File").click();
    }

    public void clickOkInAlertWindow() {

        Alert alert = getDriver().switchTo().alert();
        alert.accept();
    }

}
