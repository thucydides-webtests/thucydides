package net.thucydides.core.webdriver;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.BASE_URL;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_STORE_HTML_SOURCE;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_TAKE_SCREENSHOTS;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 * 
 */
public class SystemPropertiesConfiguration implements Configuration {

    /**
     * Use the 'webdriver.driver' property to tell Thucydides what browser to
     * run the tests in.
     */
    public static final String WEBDRIVER_DRIVER = ThucydidesSystemProperty.DRIVER.getPropertyName();

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";

    /**
     * Default timeout when waiting for AJAX elements in pages, in milliseconds.
     */
    public static final int DEFAULT_ELEMENT_TIMEOUT_SECONDS = 5;

    public static final Integer DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT = 5;

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = ThucydidesSystemProperty.OUTPUT_DIRECTORY.getPropertyName();

    private static final String MAVEN_BUILD_DIRECTORY = "project.build.directory";

    private static final String MAVEN_REPORTS_DIRECTORY = "project.reporting.OutputDirectory";
    /**
     * By default, when accepting untrusted SSL certificates, assume that these certificates will come from an
     * untrusted issuer or will be self signed. Due to limitation within Firefox, it is easy to find out if the
     * certificate has expired or does not match the host it was served for, but hard to find out if the issuer of
     * the certificate is untrusted. By default, it is assumed that the certificates were not be issued from a trusted
     * CA. If you are receive an "untrusted site" prompt on Firefox when using a certificate that was issued by valid
     * issuer, but has expired or is being served served for a different host (e.g. production certificate served in
     * a testing environment) set this to false.
     */
    public static final String REFUSE_UNTRUSTED_CERTIFICATES
            = ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.getPropertyName();

    public static final String MAX_RETRIES = "max.retries";

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/site/thucydides";

    /**
     * HTML and XML reports will be generated in this directory.
     */
    private File outputDirectory;

    private String defaultBaseUrl;

    private final EnvironmentVariables environmentVariables;

    private final FilePathParser filePathParser;

    @Inject
    public SystemPropertiesConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        filePathParser = new FilePathParser(environmentVariables);
    }

    public Configuration copy() {
        return withEnvironmentVariables(environmentVariables);
    }

    public Configuration withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        SystemPropertiesConfiguration copy = new SystemPropertiesConfiguration(environmentVariables.copy());
        copy.outputDirectory = outputDirectory;
        copy.defaultBaseUrl = defaultBaseUrl;
        return copy;
    }

    public EnvironmentVariables getEnvironmentVariables() {
//        if (environmentVariables == null) {
//            environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
//        }
        return environmentVariables;
    }

    public int maxRetries() {
        int maxRetries = getEnvironmentVariables().getPropertyAsInteger(MAX_RETRIES, 0);
        return maxRetries;
    }

    /**
     * Get the currently-configured browser type.
     */
    public SupportedWebDriver getDriverType() {
        String driverType = environmentVariables.getProperty(WEBDRIVER_DRIVER, DEFAULT_WEBDRIVER_DRIVER);
        return lookupSupportedDriverTypeFor(driverType);
    }

    /**
     * Where should the reports go?
     */
    public File loadOutputDirectoryFromSystemProperties() {
        String systemDirectoryProperty = environmentVariables.getProperty(OUTPUT_DIRECTORY_PROPERTY, getMavenBuildDirectory());
        String instantiatedPath = filePathParser.getInstanciatedPath(systemDirectoryProperty);
        String systemDefinedDirectory = (instantiatedPath != null) ? instantiatedPath : DEFAULT_OUTPUT_DIRECTORY;

        File newOutputDirectory = new File(systemDefinedDirectory);
        newOutputDirectory.mkdirs();
        return newOutputDirectory;
    }

    private String getMavenBuildDirectory() {
        String mavenBuildDirectory = getEnvironmentVariables().getProperty(MAVEN_BUILD_DIRECTORY);
        String mavenReportsDirectory = getEnvironmentVariables().getProperty(MAVEN_REPORTS_DIRECTORY);
        String defaultMavenRelativeTargetDirectory = null;
        if (StringUtils.isNotEmpty(mavenReportsDirectory)) {
            defaultMavenRelativeTargetDirectory = mavenReportsDirectory + "/thucydides";
        } else if (StringUtils.isNotEmpty(mavenBuildDirectory)) {
            defaultMavenRelativeTargetDirectory = mavenBuildDirectory + "/site/thucydides";
        }
        return defaultMavenRelativeTargetDirectory;
    }

    public int getStepDelay() {
        int stepDelay = 0;

        String stepDelayValue = getEnvironmentVariables().getProperty(ThucydidesSystemProperty.STEP_DELAY.getPropertyName());
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            stepDelay = Integer.valueOf(stepDelayValue);
        }
        return stepDelay;

    }

    public int getElementTimeout() {
        int elementTimeout = DEFAULT_ELEMENT_TIMEOUT_SECONDS;

        String stepDelayValue = getEnvironmentVariables().getProperty(ThucydidesSystemProperty.ELEMENT_TIMEOUT.getPropertyName());
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            elementTimeout = Integer.valueOf(stepDelayValue);
        }
        return elementTimeout;

    }

    public boolean getUseUniqueBrowser() {
        boolean uniqueBrowser = false;
        String uniqueBrowserValue = getEnvironmentVariables().getProperty(ThucydidesSystemProperty.UNIQUE_BROWSER.getPropertyName());
        if (uniqueBrowserValue != null) {
            uniqueBrowser = Boolean.valueOf(uniqueBrowserValue);
        }
        return uniqueBrowser;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/site/thucydides', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     * 
     */
    public File getOutputDirectory() {
        if (outputDirectory == null) {
            outputDirectory = loadOutputDirectoryFromSystemProperties();
        }
        return outputDirectory;
    }

    public double getEstimatedAverageStepCount() {
        return getEnvironmentVariables().getPropertyAsInteger(ThucydidesSystemProperty.ESTIMATED_AVERAGE_STEP_COUNT.getPropertyName(),
                DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
    }

    public boolean onlySaveFailingScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(ThucydidesSystemProperty.ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), false);
    }

    public boolean takeVerboseScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(ThucydidesSystemProperty.VERBOSE_SCREENSHOTS.getPropertyName(), false);
    }

    public Optional<TakeScreenshots> getScreenshotLevel() {
        String takeScreenshotsLevel = THUCYDIDES_TAKE_SCREENSHOTS.from(getEnvironmentVariables());
        if (StringUtils.isNotEmpty(takeScreenshotsLevel)) {
            return Optional.of(TakeScreenshots.valueOf(takeScreenshotsLevel.toUpperCase()));
        } else {
            return Optional.absent();
        }
    }

    public boolean storeHtmlSourceCode() {
        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_STORE_HTML_SOURCE, false);
    }

    public void setIfUndefined(String property, String value) {
        if (getEnvironmentVariables().getProperty(property) == null) {
            getEnvironmentVariables().setProperty(property, value);
        }
    }

    /**
     * Override the default base URL manually.
     * Normally only needed for testing.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public int getRestartFrequency() {
        return environmentVariables.getPropertyAsInteger(
                        ThucydidesSystemProperty.RESTART_BROWSER_FREQUENCY.getPropertyName(),0);

    }

    @Override
    public int getCurrentTestCount() {
        return 0;
    }

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl() {
        return environmentVariables.getProperty(BASE_URL.getPropertyName(), defaultBaseUrl);
    }
    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     * 
     * @throws UnsupportedDriverException
     */
    private SupportedWebDriver lookupSupportedDriverTypeFor(final String driverType) {
        SupportedWebDriver driver = null;
        try {
            driver = SupportedWebDriver.getDriverTypeFor(driverType);
        } catch (IllegalArgumentException iae) {
            throwUnsupportedDriverExceptionFor(driverType);
        }
        return driver;
    }

    private void throwUnsupportedDriverExceptionFor(final String driverType) {
        throw new UnsupportedDriverException(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }

}
