package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jhlabs.image.BoxBlurFilter;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    private Optional<BlurLevel> blurLevel;

    private final Logger logger = LoggerFactory.getLogger(Photographer.class);
    private ScreenshotProcessor screenshotProcessor;

    protected Logger getLogger() {
        return logger;
    }

    private static final ScreenshotSequence DEFAULT_SCREENSHOT_SEQUENCE = new ScreenshotSequence();

    public Photographer(final WebDriver driver, final File targetDirectory) {
        this(driver, targetDirectory, Injectors.getInjector().getInstance(ScreenshotProcessor.class),
                Optional.<BlurLevel>absent());
    }

    public Photographer(final WebDriver driver, final File targetDirectory, final Optional<BlurLevel> blurLevel) {
        this(driver, targetDirectory, Injectors.getInjector().getInstance(ScreenshotProcessor.class), blurLevel);
    }

    public Photographer(final WebDriver driver, final File targetDirectory, final ScreenshotProcessor screenshotProcessor) {
        this(driver, targetDirectory, Injectors.getInjector().getInstance(ScreenshotProcessor.class),
                Optional.<BlurLevel>absent());
    }


    public Photographer(final WebDriver driver, final File targetDirectory,
                            final ScreenshotProcessor screenshotProcessor, Optional<BlurLevel> blurLevel) {
        Preconditions.checkNotNull(targetDirectory);
        Preconditions.checkNotNull(screenshotProcessor);

        this.driver = driver;
        this.targetDirectory = targetDirectory;
        this.screenshotProcessor = screenshotProcessor;
        this.screenshotSequence = DEFAULT_SCREENSHOT_SEQUENCE;
        this.blurLevel = blurLevel;
    }

    protected long nextScreenshotNumber() {
        return screenshotSequence.next();
    }

    private String nextScreenshotName(final String prefix) {
        long nextScreenshotNumber = nextScreenshotNumber();
        return "screenshot-" + Digest.ofTextValue(prefix) + nextScreenshotNumber + ".png";
    }

    /**
     * Take a screenshot of the current browser and store it in the output directory.
     */
    public Optional<File> takeScreenshot(final String prefix) {
        if (driverCanTakeSnapshots()) {
            try {
                File screenshotFile = null;
                Object capturedScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                if (isAFile(capturedScreenshot)) {
                    screenshotFile = (File) capturedScreenshot;
                } else if (isByteArray(capturedScreenshot)) {
                    screenshotFile = saveScreenshotData((byte[]) capturedScreenshot);
                }
                if (screenshotFile != null && blurLevel.isPresent()) {
                    screenshotFile = blur(screenshotFile);
                }
                if (screenshotFile != null) {
                    File savedScreenshot = targetScreenshot(prefix);
                    screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotFile, savedScreenshot));

                    if (! blurLevel.isPresent()) savePageSourceFor(savedScreenshot.getAbsolutePath());

                    return Optional.of(savedScreenshot);
                }
            } catch (Throwable e) {
                getLogger().warn("Failed to write screenshot (possibly an out of memory error): " + e.getMessage());
            }
        }
        return Optional.absent();
    }

    protected File blur(File srcFile) throws Exception {
        BufferedImage srcImage = ImageIO.read(srcFile);
        BufferedImage destImage = deepCopy(srcImage);
        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(blurLevel.get().getRadius());
        boxBlurFilter.setIterations(3);
        destImage = boxBlurFilter.filter(srcImage, destImage);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(destImage, "png", outStream);

        return  saveScreenshotData(outStream.toByteArray());
    }

    private BufferedImage deepCopy(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private File saveScreenshotData(byte[] capturedScreenshot) throws IOException {
        File screenshotFile;
        String screenshotTempFileName = "screenshot_" + UUID.randomUUID();
        screenshotFile = new File(FileUtils.getTempDirectory(), screenshotTempFileName);
        byte[] screenshotData = capturedScreenshot;
        screenshotFile.deleteOnExit();
        if (screenshotData.length > 0) {
            FileUtils.writeByteArrayToFile(screenshotFile, screenshotData);
        } else {
            FileUtils.touch(screenshotFile);
        }
        return screenshotFile;
    }

    private boolean isAFile(Object screenshot) {
        return (screenshot instanceof File);
    }

    private boolean isByteArray(Object screenshot) {
        return (screenshot instanceof byte[]);
    }

    private File targetScreenshot(String prefix) {
        targetDirectory.mkdirs();
        return new File(targetDirectory, nextScreenshotName(prefix));
    }

    protected boolean driverCanTakeSnapshots() {
        if (driver == null) {
            return false;
        } else if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).canTakeScreenshots()
                    && (((WebDriverFacade) driver).getProxiedDriver() != null);
        } else {
            return TakesScreenshot.class.isAssignableFrom(driver.getClass());
        }
    }

    private void savePageSourceFor(final String screenshotFile) throws IOException {
        try {
            WebDriver webdriver = driver;
            String pageSource = webdriver.getPageSource();

            File savedSource = new File(sourceCodeFileFor(screenshotFile));
            FileUtils.writeStringToFile(savedSource, pageSource);
        } catch (WebDriverException e) {
            getLogger().warn("Failed to save screen source code", e);
        }
    }


    private String sourceCodeFileFor(final String screenshotFile) {
        String rootFilename = screenshotFile.substring(0, screenshotFile.length() - PNG_SUFFIX_LENGTH);
        return rootFilename + ".html";
    }

    public File getMatchingSourceCodeFor(final File screenshot) {
        if (screenshot != null) {
            return new File(sourceCodeFileFor(screenshot.getAbsolutePath()));
        } else {
            return null;
        }
    }

    public void setScreenshotProcessor(ScreenshotProcessor screenshotProcessor) {
        this.screenshotProcessor = screenshotProcessor;
    }

    protected ScreenshotProcessor getScreenshotProcessor() {
        return screenshotProcessor;
    }
}
