package net.thucydides.gwt.widgets;

import org.openqa.selenium.WebElement;

/**
 * A class that helps upload a file to an HTML form in using a fluent API.
 */
public class FileToUpload {
    private final String filename;

    public FileToUpload(final String filename) {
        this.filename = filename;
    }

    public void to(final WebElement uploadFileField) {
         uploadFileField.sendKeys(filename);
    }
}
