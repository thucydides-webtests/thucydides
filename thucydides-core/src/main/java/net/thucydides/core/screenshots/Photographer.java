package net.thucydides.core.screenshots;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The photographer takes and stores screenshots during the test.
 * The actual screenshots are taken using the specified web driver,
 * and are stored in the specified target directory. Screenshots
 * are numbered sequentially.
 *
 * @author johnsmart
 */
public class Photographer {

    private static final int MESSAGE_DIGEST_MASK = 0xFF;
    private static final int PNG_SUFFIX_LENGTH = ".png".length();
    private final WebDriver driver;
    private final File targetDirectory;
    private final ScreenshotSequence screenshotSequence;
    private final MessageDigest digest;

    private static final Logger LOGGER = LoggerFactory.getLogger(Photographer.class);

    private static final ScreenshotSequence DEFAULT_SCREENSHOT_SEQUENCE = new ScreenshotSequence();

    public Photographer(final WebDriver driver, final File targetDirectory) {
        this.driver = driver;
        this.targetDirectory = targetDirectory;
        this.screenshotSequence = DEFAULT_SCREENSHOT_SEQUENCE;
        this.digest = getMd5Digest();
    }

    private MessageDigest getMd5Digest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to create digest for screenshot name.", e);
        }
        return md;
    }

    protected long nextScreenshotNumber() {
        return screenshotSequence.next();
    }

    private String nextScreenshotName(final String prefix) {
        long nextScreenshotNumber = nextScreenshotNumber();
        return "screenshot-" + getMD5DigestFrom(prefix) + nextScreenshotNumber + ".png";
    }

    private String getMD5DigestFrom(final String value) {
        byte[] messageDigest = digest.digest(value.getBytes());
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(MESSAGE_DIGEST_MASK & messageDigest[i]));
        }
        return hexString.toString();
    }

    /**
     * Take a screenshot of the current browser and store it in the output directory.
     */
    public File takeScreenshot(final String prefix) {
        if (driverCanTakeSnapehots()) {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            if (screenshot != null) {
                try {
                    return saveScreenshoot(prefix, screenshot);
                } catch (IOException e) {
                    throw new ScreenshotException("Screenshot could not be saved", e);
                }
            }
        }
        return null;

    }

    protected File saveScreenshoot(final String prefix, final File screenshot) throws IOException{
        File savedScreenshot = new File(targetDirectory, nextScreenshotName(prefix));
        FileUtils.copyFile(screenshot, savedScreenshot);
        savePageSourceFor(savedScreenshot.getAbsolutePath());
        return savedScreenshot;
    }

    private boolean driverCanTakeSnapehots() {
        try {
            TakesScreenshot screenshotTaker = (TakesScreenshot) driver;
            return true;
        } catch (ClassCastException e) {
            return false;
        }

    }

    private void savePageSourceFor(final String screenshotFile) throws IOException {
        if (WebDriver.class.isAssignableFrom(driver.getClass())) {
            WebDriver webdriver = (WebDriver) driver;
            String pageSource = webdriver.getPageSource();

            File savedSource = new File(sourceCodeFileFor(screenshotFile));
            FileUtils.writeStringToFile(savedSource, pageSource);
        }
    }


    private String sourceCodeFileFor(final String screenshotFile) {
        String rootFilename = screenshotFile.substring(0, screenshotFile.length() - PNG_SUFFIX_LENGTH);
        return rootFilename + ".html";
    }

    public File getMatchingSourceCodeFor(final File screenshot) {
        return new File(sourceCodeFileFor(screenshot.getAbsolutePath()));
    }
}
