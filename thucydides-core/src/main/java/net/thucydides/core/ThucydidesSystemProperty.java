package net.thucydides.core;

import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

/**
 * Properties that can be passed to a web driver test to customize its behaviour.
 * This class is mainly for internal use.
 * 
 * @author johnsmart
 *
 */
public enum ThucydidesSystemProperty {

    /**
     * The WebDriver driver - firefox or chrome.
     */
    DRIVER("webdriver.driver"),    
    
    /**
     * The default starting URL for the application, and base URL for relative paths.
     */
    BASE_URL("webdriver.base.url"),

    /**
     * A unique identifier for the project under test, used to record test statistics.
     */
    PROJECT_KEY("thucydides.project.key"),

    /**
     * The home directory for Thucydides output and data files - by default, $USER_HOME/.thucydides
     */
    THUCYDIDES_HOME("thucydides.home"),

    /**
     * The port used by Thucydides for the local statistics database.
     */
    THUCYDIDES_PORT("thucydides.database.port"),

    /**
     * Run the local Thucydides database as an in-memory server, as opposed to a file-based database.
     * This is useful if the Thucydides tests are to be run in parallel on different JVM forks.
     */
    THUCYDIDES_USE_LOCAL_SERVER("thucydides.use.local.server"),

    /**
     * Record test result statistics in a database for reporting purposes.
     */
    RECORD_STATISTICS("thucydides.record.statistics"),

    /**
     * Indicates a directory from which the resources for the HTML reports should be copied.
     * This directory currently needs to be provided in a JAR file.
     */
    REPORT_RESOURCE_PATH("thucydides.report.resources"),

    /**
     * Where should reports be generated.
     */
    OUTPUT_DIRECTORY("thucydides.outputDirectory"),

    /**
     * Should Thucydides only store screenshots for failing steps?
     * This can save disk space and speed up the tests somewhat. Useful for data-driven testing.
     */
    ONLY_SAVE_FAILING_SCREENSHOTS("thucydides.only.save.failing.screenshots"),

    /**
     * Should Thucydides take screenshots for every clicked button and every selected link?
     * If this option is set to true (the default value), Thucydides will record screenshots for any action performed
     * on a WebElementFacade, i.e. any time you use an expression like element(...).click(),
     * findBy(...).click() and so on.
     * This will be overridden if the ONLY_SAVE_FAILING_SCREENSHOTS option is activated.
     */
    VERBOSE_SCREENSHOTS("thucydides.verbose.screenshots"),

    /**
     * Restart the browser every so often during data-driven tests.
     */
    RESTART_BROWSER_FREQUENCY("thucydides.restart.browser.frequency"),

    /**
     * Pause (in ms) between each test step.
     */
    STEP_DELAY("thucycides.step.delay"),

    /**
     * How long should the driver wait for elements not immediately visible.
     */
    ELEMENT_TIMEOUT("thucydides.timeout"),

    /**
     * Don't accept sites using untrusted certificates.
     * By default, Thucydides accepts untrusted certificates - use this to change this behaviour.
     */
    ASSUME_UNTRUSTED_CERTIFICATE_ISSUER("refuse.untrusted.certificates"),

    /**
     * Use the same browser for all tests (the "Highlander" rule)
     */
    UNIQUE_BROWSER("thucydides.use.unique.browser"),

    /**
     * The estimated number of steps in a pending scenario.
     * The estimated number of steps in a pending sceario.
     * This is used for stories where no scenarios have been defined.
     */
    ESTIMATED_AVERAGE_STEP_COUNT("thucydides.estimated.average.step.count"),

    /**
     *  Base URL for the issue tracking system to be referred to in the reports.
     *  If defined, any issues quoted in the form #1234 will be linked to the relevant
     *  issue in the issue tracking system. Works with JIRA, Trac etc.
     */
    ISSUE_TRACKER_URL("thucydides.issue.tracker.url"),

    /**
     * Activate native events in Firefox.
     * This is true by default, but can cause issues with some versions of linux.
     */
    NATIVE_EVENTS("thucydides.native.events"),

    /**
     * If the base JIRA URL is defined, Thucydides will build the issue tracker url using the standard JIRA form.
     */
    JIRA_URL("jira.url"),

    /**
     *  If defined, the JIRA project id will be prepended to issue numbers.
     */
    JIRA_PROJECT("jira.project"),

    /**
     * Base directory in which history files are stored.
     */
    HISTORY_BASE_DIRECTORY("thucydides.history"),

    /**
     *  Redimension the browser to enable larger screenshots.
     */
    SNAPSHOT_HEIGHT("thucydides.browser.height"),
    /**
     *  Redimension the browser to enable larger screenshots.
     */
    SNAPSHOT_WIDTH("thucydides.browser.width"),

    /**
     * Public URL where the Thucydides reports will be displayed.
     * This is mainly for use by plugins.
     */
    PUBLIC_URL("thucydides.public.url"),

    /**
     * Activate the Firebugs plugin for firefox.
     * Useful for debugging, but not very when running the tests on a build server.
     * It is not activated by default.
     */
    ACTIVATE_FIREBUGS("thucydides.activate.firebugs"),

    ACTIVTE_HIGHLIGHTING("thucydides.activate.highlighting"),

    /**
     *  If batch testing is being used, this is the size of the batches being executed.
     */
    BATCH_COUNT("thucydides.batch.count"),

    /**
     * If batch testing is being used, this is the number of the batch being run on this machine.
     */
    BATCH_NUMBER("thucydides.batch.number"),

    /**
     * HTTP Proxy URL configuration for Firefox
     */
    PROXY_URL("thucydides.proxy.http"),

    /**
     * HTTP Proxy port configuration for Firefox
     */
    PROXY_PORT("thucydides.proxy.http_port"),


    /**
     * Extension packages. This is a list of packages that will be scanned for custom TagProvider implementations.
     * To add a custom tag provider, just implement the TagProvider interface and specify the root package for this
     * provider in this parameter.
     */
    EXTENSION_PACKAGES("thucydides.ext.packages"),

    /**
     * Enable JQuery integration.
     * If set to true (the default), JQuery will be injected into any page that does not already have it.
     * You can turn this option off for performance reasons if you are not using JQuery selectors.
     */
    JQUERY_INTEGRATION("thucydides.jquery.integration"),

    SAUCELABS_TARGET_PLATFORM("saucelabs.target.platform"),

    SAUCELABS_DRIVER_VERSION("saucelabs.driver.version"),

    SAUCELABS_TEST_NAME("saucelabs.test.name"),
    /**
     * SauceLabs URL if running the web tests on SauceLabs
     */
    SAUCELABS_URL("saucelabs.url"),

    /**
     * SauceLabs access key - if provided, Thucydides can generate links to the SauceLabs reports that don't require a login.
     */
    SAUCELABS_ACCESS_KEY("saucelabs.access.key"),

    /**
     * SauceLabs user id - if provided with the access key,
     * Thucydides can generate links to the SauceLabs reports that don't require a login.
     */
    SAUCELABS_USER_ID("saucelabs.user.id"),

    /**
     * Override the default implicit timeout value for the Saucelabs driver.
     */
    SAUCELABS_IMPLICIT_TIMEOUT("saucelabs.implicit.timeout"),

    /**
     * Timeout (in seconds) for retrying file I/O.
     * Used in net.thucydides.core.resources.FileResources.copyResourceTo().
     * Sometimes, file I/O fails on Windows machine due to the way Windows handles memory-mapped
     * files (http://stackoverflow.com/questions/3602783/file-access-synchronized-on-java-object).
     * This property, if set, will retry copying the resource till timeout. A default value is used
     * if the property is not set.
     */
     FILE_IO_RETRY_TIMEOUT("thucydides.file.io.retry.timeout"),

    /**
     * Three levels are supported: QUIET, NORMAL and VERBOSE
     */
    LOGGING("thucydides.logging"),

    /**
     * The root package for the tests in a given project.
     * If provided, Thucydides will log information about the total number of tests to be executed,
     * and keep a tally of the executed tests.
     */
    TEST_ROOT_PACKAGE("thucydides.test.root");

    private String propertyName;
    public static final int DEFAULT_HEIGHT = 1000;
    public static final int DEFAULT_WIDTH = 800;

    private ThucydidesSystemProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }


    @Override
    public String toString() {
        return propertyName;
    }

    public String from(EnvironmentVariables environmentVariables) {
        return environmentVariables.getProperty(getPropertyName());
    }

    public String from(EnvironmentVariables environmentVariables, String defaultValue) {
        String value = environmentVariables.getProperty(getPropertyName());
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }
}
