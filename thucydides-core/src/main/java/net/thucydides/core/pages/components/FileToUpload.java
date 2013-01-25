package net.thucydides.core.pages.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * A class that helps upload a file to an HTML form in using a fluent API.
 */
public class FileToUpload {
    private final String filename;
    static final String WINDOWS_PATH_PATTERN = "^[A-Z]:\\\\.*";

    private static Pattern fullWindowsPath = Pattern.compile(WINDOWS_PATH_PATTERN);

    public FileToUpload(final String filename) {
        if (isOnTheClasspath(filename)) {
            this.filename = getFileFromResourcePath(filename);
        } else {
            this.filename = getFileFromFileSystem(filename);
        }
    }


    private boolean isOnTheClasspath(final String filename) {
        if (isOnTheUnixFileSystem(filename) || isOnTheWindowsFileSystem(filename)) {
            return false;
        } else {
            return (resourceOnClasspath(filename) != null);
        }
    }

    private URL resourceOnClasspath(final String filename) {
        ClassLoader cldr = Thread.currentThread().getContextClassLoader();
        return cldr.getResource(filename);
    }

    public static boolean isOnTheWindowsFileSystem(final String filename) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(filename).exists();
        } else {
            return false;
        }
    }

    public static boolean isAFullWindowsPath(final String filename) {
        return fullWindowsPath.matcher(filename).find();
    }

    public static boolean isOnTheUnixFileSystem(final String filename) {
        if (SystemUtils.IS_OS_UNIX) {
            return new File(filename).exists();
        } else {
            return false;
        }
    }

    private String getFileFromResourcePath(final String filename) {
        return resourceOnClasspath(filename).getFile();
    }

    private String getFileFromFileSystem(final String filename) {
        File fileToUpload = new File(filename);
        return fileToUpload.getAbsolutePath();
    }


    public void to(final WebElement uploadFileField) {
        uploadFileField.sendKeys(osSpecificPathOf(filename));
    }


    private String osSpecificPathOf(final String fileToUpload) {
        if (isAFullWindowsPath(fileToUpload)) {
            return windowsNative(fileToUpload);
        } else {
            return fileToUpload;
        }
    }

    private String windowsNative(final String fileToUpload) {
        String bareFilename = (fileToUpload.charAt(0) == '/') ? fileToUpload.substring(1) : fileToUpload;
        return StringUtils.replace(bareFilename,"/","\\");
    }

}

