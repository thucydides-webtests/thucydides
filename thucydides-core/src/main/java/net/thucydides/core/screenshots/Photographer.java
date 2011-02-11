package net.thucydides.core.screenshots;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * The photographer takes and stores screenshots during the test.
 * The actual screenshots are taken using the specified web driver,
 * and are stored in the specified target directory. Screenshots
 * are numbered sequentially.
 *
 * @author johnsmart
 *
 */
public class Photographer {

    private final TakesScreenshot driver;
    private final File targetDirectory;
    private final ScreenshotSequence screenshotSequence;

    private static final ScreenshotSequence DEFAULT_SCREENSHOT_SEQUENCE = new ScreenshotSequence();
    
    public Photographer(final TakesScreenshot driver, final File targetDirectory) {
        this.driver = driver;
        this.targetDirectory = targetDirectory;
        this.screenshotSequence = DEFAULT_SCREENSHOT_SEQUENCE;
    }

    protected long nextScreenshotNumber() {
        return screenshotSequence.next();
    }
    
    private String nextScreenshotName(final String prefix) {
        long nextScreenshotNumber = nextScreenshotNumber() ;
        return prefix + nextScreenshotNumber + ".png";
    }

    /**
     * Take a screenshot of the current browser and store it in the output directory.
     */
    public File takeScreenshot(final String prefix) throws IOException {
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        File savedScreenshot = new File(targetDirectory, nextScreenshotName(prefix));
        FileUtils.copyFile(screenshot, savedScreenshot);
        
        return savedScreenshot;
    }

}
