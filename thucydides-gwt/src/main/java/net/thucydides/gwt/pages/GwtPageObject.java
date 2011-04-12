package net.thucydides.gwt.pages;

import net.thucydides.gwt.widgets.FileToUpload;
import net.thucydides.gwt.widgets.GwtButton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebElement;

import java.io.File;

/**
 * An extension of the Thucydides PageObject class providing extra features for GWT apps.
 * 
 * @author johnsmart
 * 
 */
public abstract class GwtPageObject extends PageObject {

    public GwtPageObject(final WebDriver driver) {
        super(driver);
    }

    /**
     * Return a GWT Button widget with a given label.
     */
    public GwtButton getButtonLabelled(final String label) {
        String buttonXPath = String.format("//button[contains(.,'%s')]", label);
        WebElement button = getDriver().findElement(By.xpath(buttonXPath));
        return new GwtButton(label, button);
    }

    /**
     * Get a Gwt button using a Selenium identifier.
     */
    public GwtButton findButton(final By byIdentifer) {
        WebElement button = getDriver().findElement(byIdentifer);
        return new GwtButton(button.getText(), button);
    }

    /**
     * Upload a file using a fluent interface.
     * Files are assumed to be on the classpath, e.g. in the src/main/resources directory
     */
    public FileToUpload uploadFileFromResourcePath(final String filename) {
        ClassLoader cldr = Thread.currentThread().getContextClassLoader();
        String uploadPath = cldr.getResource(filename).getFile();
        return new FileToUpload(uploadPath);

    }


    /**
     * Upload a file using a fluent interface.
     * Files are assumed to be on the classpath, e.g. in the src/main/resources directory
     */
    public FileToUpload uploadFileFromFileSystem(final String filename) {
        File fileToUpload= new File(filename);
        String uploadPath = fileToUpload.getAbsolutePath();
        return new FileToUpload(uploadPath);

    }

}
