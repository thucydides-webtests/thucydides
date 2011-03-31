package net.thucydides.gwt.widgets;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that helps upload a file to an HTML form in using a fluent API.
 */
public class FileToUpload {
    private final String filename;
    private final Pattern windowsPath = Pattern.compile("/[A-Z]:.*");


    public FileToUpload(final String filename) {
        this.filename = filename;
    }

    public void to(final WebElement uploadFileField) {
        uploadFileField.sendKeys(osSpecificPathOf(filename));
    }


    private String osSpecificPathOf(final String fileToUpload) {
        if (isAWindows(fileToUpload)) {
            return windowsNative(fileToUpload);
        } else {
            return fileToUpload;
        }
    }

    private String windowsNative(final String fileToUpload) {
        String bareFilename = fileToUpload.substring(1);
        return StringUtils.replace(bareFilename,"/","\\");
    }

    private boolean isAWindows(final String fileToUpload) {
        Matcher matcher = windowsPath.matcher(fileToUpload);
        return matcher.matches();
    }
}
