package net.thucydides.gwt.widgets;

import org.openqa.selenium.WebElement;

public class FileToUpload {
    final String filename;

    public FileToUpload(final String filename) {
        this.filename = filename;
    }

    public void to(WebElement uploadFileField) {
         uploadFileField.sendKeys(filename);
    }
}
