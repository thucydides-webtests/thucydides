package net.thucydides.core;

import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

/**
 * Properties that can be passed to a web driver test to customize its behaviour.
 * The properties can be passed as system properties or placed in the 'thucydides.properties' file using a lower-case,
 * period-separated format. For example, WEBDRIVER_DRIVER is passed as -Dwebdriver.driver=firefox
 *
 * @author johnsmart
 *
 */
public enum ThucydidesSystemProperty {

    /**
     * The WebDriver driver - firefox, chrome, iexplorer, htmlunit, safari.
     */
    WEBDRIVER_DRIVER,

    /** A shortcut for 'webdriver.driver'. */
    DRIVER,

    /**
     * If using a provided driver, what type is it.
     * The implementation class needs to be defined in the webdriver.provided.{type} system property.
    */
    WEBDRIVER_PROVIDED_TYPE,

    /**
     * The default starting URL for the application, and base URL for relative paths.
     */
    WEBDRIVER_BASE_URL,

    /**
     * The URL to be used for remote drivers (including a selenium grid hub)
     */
    WEBDRIVER_REMOTE_URL,

    /**
     * What port to run PhantomJS on (used in conjunction with webdriver.remote.url to
     * register with a Selenium hub, e.g. -Dphantomjs.webdriver=5555 -Dwebdriver.remote.url=http://localhost:4444
     */
    PHANTOMJS_WEBDRIVER_PORT,

    /**
     * The driver to be used for remote drivers
     */
    WEBDRIVER_REMOTE_DRIVER,

    WEBDRIVER_REMOTE_BROWSER_VERSION,

    WEBDRIVER_REMOTE_OS,

    /**
     * Path to the Chrome driver, if it is not on the system path.
     */
    WEBDRIVER_CHROME_DRIVER,

    /**
     * A unique identifier for the project under test, used to record test statistics.
     */
    THUCYDIDES_PROJECT_KEY,

    /**
     * What name should appear on the reports
     */
    THUCYDIDES_PROJECT_NAME,

    /**
     * The home directory for Thucydides output and data files - by default, $USER_HOME/.thucydides
     */
    THUCYDIDES_HOME,

    /**
     * Indicates a directory from which the resources for the HTML reports should be copied.
     * This directory currently needs to be provided in a JAR file.
     */
    THUCYDIDES_REPORT_RESOURCES,

    /**
     * Encoding for reports output
     */
    THUCYDIDES_REPORT_ENCODING,

    /**
     * Where should reports be generated (use the system property 'thucydides.outputDirectory').
     */
    THUCYDIDES_OUTPUT_DIRECTORY("thucydides.outputDirectory"),

    /**
     * Should Thucydides only store screenshots for failing steps?
     * This can save disk space and speed up the tests somewhat. Useful for data-driven testing.
     * @deprecated This property is still supported, but thucydides.take.screenshots provides more fine-grained control.
     */
    THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS,

    /**
     * A set of user-defined capabilities to be used to configure the WebDriver driver.
     * Capabilities should be passed in as a space or semi-colon-separated list of key:value pairs, e.g.
     * "build:build-1234; max-duration:300; single-window:true; tags:[tag1,tag2,tag3]"
     */
    THUCYDIDES_DRIVER_CAPABILITIES,

    /**
     * Should Thucydides take screenshots for every clicked button and every selected link?
     * By default, a screenshot will be stored at the start and end of each step.
     * If this option is set to true, Thucydides will record screenshots for any action performed
     * on a WebElementFacade, i.e. any time you use an expression like element(...).click(),
     * findBy(...).click() and so on.
     * This will be overridden if the THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS option is set to true.
     * @deprecated This property is still supported, but thucydides.take.screenshots provides more fine-grained control.
     */
    THUCYDIDES_VERBOSE_SCREENSHOTS,

    /**
     * If set to true, WebElementFacade events and other step actions will be logged to the console.
     */
    THUCYDIDES_VERBOSE_STEPS,

    /**
     *  Fine-grained control over when screenshots are to be taken.
     *  This property accepts the following values:
     *  <ul>
     *      <li>FOR_EACH_ACTION</li>
     *      <li>BEFORE_AND_AFTER_EACH_STEP</li>
     *      <li>AFTER_EACH_STEP</li>
     *      <li>FOR_FAILURES</li>
     *  </ul>
     */
    THUCYDIDES_TAKE_SCREENSHOTS,

    /**
     * Should Thucydides display detailed information in the test result tables.
     * If this is set to true, test result tables will display a breakdown of the steps by result.
     * This is false by default.
     */
    THUCYDIDES_REPORTS_SHOW_STEP_DETAILS,

    /**
     * Show statistics for manual tests in the test reports.
     */
    THUCYDIDES_REPORT_SHOW_MANUAL_TESTS,

    /**
     * Report on releases
     */
    THUCYDIDES_REPORT_SHOW_RELEASES,

    THUCYDIDES_REPORT_SHOW_PROGRESS,

    THUCYDIDES_REPORT_SHOW_HISTORY,

    THUCYDIDES_REPORT_SHOW_TAG_MENUS,

    /**
     * Restart the browser every so often during data-driven tests.
     */
    THUCYDIDES_RESTART_BROWSER_FREQUENCY,

    /**
     * Pause (in ms) between each test step.
     */
    THUCYDIDES_STEP_DELAY,

    /**
     * How long should the driver wait for elements not immediately visible, in seconds.
     */
    THUCYDIDES_TIMEOUT,

    /**
     * Don't accept sites using untrusted certificates.
     * By default, Thucydides accepts untrusted certificates - use this to change this behaviour.
     */
    REFUSE_UNTRUSTED_CERTIFICATES,

    /**
     * Use the same browser for all tests (the "Highlander" rule)
     */
    THUCYDIDES_USE_UNIQUE_BROWSER,

    /**
     * The estimated number of steps in a pending scenario.
     * This is used for stories where no scenarios have been defined.
     */
    THUCYDIDES_ESTIMATED_AVERAGE_STEP_COUNT,

    /**
     * The estimated number of tests in a typical story.
     * Used to estimate functional coverage in the requirements reports.
     */
    THUCYDIDES_ESTIMATED_TESTS_PER_REQUIREMENT,

    /**
     *  Base URL for the issue tracking system to be referred to in the reports.
     *  If defined, any issues quoted in the form #1234 will be linked to the relevant
     *  issue in the issue tracking system. Works with JIRA, Trac etc.
     */
    THUCYDIDES_ISSUE_TRACKER_URL,

    /**
     * Activate native events in Firefox.
     * This is true by default, but can cause issues with some versions of linux.
     */
    THUCYDIDES_NATIVE_EVENTS,

    /**
     * If the base JIRA URL is defined, Thucydides will build the issue tracker url using the standard JIRA form.
     */
    JIRA_URL,

    /**
     *  If defined, the JIRA project id will be prepended to issue numbers.
     */
    JIRA_PROJECT,

    /**
     *  If defined, the JIRA username required to connect to JIRA.
     */
    JIRA_USERNAME,

    /**
     *  If defined, the JIRA password required to connect to JIRA.
     */
    JIRA_PASSWORD,

    /**
     * Base directory in which history files are stored.
     */
    THUCYDIDES_HISTORY,

    /**
     *  Redimension the browser to enable larger screenshots.
     */
    THUCYDIDES_BROWSER_HEIGHT,
    /**
     *  Redimension the browser to enable larger screenshots.
     */
    THUCYDIDES_BROWSER_WIDTH,

    /**
     * If set, resize screenshots to this size to save space.
     */
    THUCYDIDES_RESIZED_IMAGE_WIDTH,

    /**
     * Public URL where the Thucydides reports will be displayed.
     * This is mainly for use by plugins.
     */
    THUCYDIDES_PUBLIC_URL,

    /**
     * Activate the Firebugs plugin for firefox.
     * Useful for debugging, but not very when running the tests on a build server.
     * It is not activated by default.
     */
    THUCYDIDES_ACTIVATE_FIREBUGS,

    /**
     * Enable applets in Firefox.
     * Use the system property 'security.enable_java'.
     * Applets slow down webdriver, so are disabled by default.
     */
    SECURITY_ENABLE_JAVA("security.enable_java"),

    THUCYDIDES_ACTIVATE_HIGHLIGHTING,

    /**
     * Batch strategy to use for parallel batches.
     * Allowed values - DIVIDE_EQUALLY (default) and DIVIDE_BY_TEST_COUNT
     */
    THUCYDIDES_BATCH_STRATEGY,

    /**
     *  A deprecated property that is synonymous with thucydides.batch.size
     */
    THUCYDIDES_BATCH_COUNT,

    /**
     *  If batch testing is being used, this is the size of the batches being executed.
     */
    THUCYDIDES_BATCH_SIZE,

    /**
     * If batch testing is being used, this is the number of the batch being run on this machine.
     */
    THUCYDIDES_BATCH_NUMBER,

    /**
     * HTTP Proxy URL configuration for Firefox and PhantomJS
     */
    THUCYDIDES_PROXY_HTTP,

    /**
     * HTTP Proxy port configuration for Firefox and PhantomJS
     * Use 'thucydides.proxy.http_port'
     */
    THUCYDIDES_PROXY_HTTP_PORT("thucydides.proxy.http_port"),

    /**
     * HTTP Proxy type configuration for Firefox and PhantomJS
     */
    THUCYDIDES_PROXY_TYPE,

    /**
     * HTTP Proxy username configuration for Firefox and PhantomJS
     */
    THUCYDIDES_PROXY_USER,

    /**
     * HTTP Proxy password configuration for Firefox and PhantomJS
     */
    THUCYDIDES_PROXY_PASSWORD,

    /**
     * How long webdriver waits for elements to appear by default, in milliseconds.
     */
    WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT,

    /**
     * Extension packages. This is a list of packages that will be scanned for custom TagProvider implementations.
     * To add a custom tag provider, just implement the TagProvider interface and specify the root package for this
     * provider in this parameter.
     */
    THUCYDIDES_EXT_PACKAGES,

    /**
     * Arguments to be passed to the Chrome driver, separated by commas.
     */
    CHROME_SWITCHES,


    /**
     * Preferences to be passed to the Firefox driver, separated by semi-colons (commas often appear in the preference
     * values.
     */
    FIREFOX_PREFERENCES,

    /**
     * Full path to the Firefox profile to be used with Firefox.
     * You can include Java system properties ${user.dir}, ${user.home} and the Windows environment variables %APPDIR%
     * and %USERPROFILE (assuming these are correctly set in the environment)
     */
    WEBDRIVER_FIREFOX_PROFILE,
    /**
     * Enable JQuery integration.
     * If set to true (the default), JQuery will be injected into any page that does not already have it.
     * You can turn this option off for performance reasons if you are not using JQuery selectors.
     */
    THUCYDIDES_JQUERY_INTEGRATION,

    SAUCELABS_TARGET_PLATFORM,

    SAUCELABS_DRIVER_VERSION,

    SAUCELABS_TEST_NAME,
    /**
     * SauceLabs URL if running the web tests on SauceLabs
     */
    SAUCELABS_URL,

    /**
     * SauceLabs access key - if provided, Thucydides can generate links to the SauceLabs reports that don't require a login.
     */
    SAUCELABS_ACCESS_KEY,

    /**
     * SauceLabs user id - if provided with the access key,
     * Thucydides can generate links to the SauceLabs reports that don't require a login.
     */
    SAUCELABS_USER_ID,

    /**
     * Override the default implicit timeout value for the Saucelabs driver.
     */
    SAUCELABS_IMPLICIT_TIMEOUT,

    /**
     * Saucelabs records screenshots as well as videos by default. Since Thucydides also records screenshots,
     * this feature is disabled by default. It can be reactivated using this system property.
     */
    SAUCELABS_RECORD_SCREENSHOTS,

    /**
     * BrowserStack Hub URL if running the tests on BrowserStack Cloud
     */
    BROWSERSTACK_URL,

    BROWSERSTACK_OS,
    
    BROWSERSTACK_OS_VERSION,

    BROWSERSTACK_BROWSER,

    BROWSERSTACK_BROWSER_VERSION,

    /**
     * BrowserStack mobile device name on which tests should be run
     */
    BROWSERSTACK_DEVICE,

    /**
     * Set the screen orientation of BrowserStack mobile device
     */
    BROWSERSTACK_DEVICE_ORIENTATION,

    /**
     * Specify a name for a logical group of builds on BrowserStack
     */
    BROWSERSTACK_PROJECT,

    /**
     * Specify a name for a logical group of tests on BrowserStack
     */
    BROWSERSTACK_BUILD,

    /**
     * Specify an identifier for the test run on BrowserStack
     */
    BROWSERSTACK_SESSION_NAME,

    /**
     * For Testing against internal/local servers on BrowserStack
     */
    BROWSERSTACK_LOCAL,

    /**
     * Generates screenshots at various steps in tests on BrowserStack
     */
    BROWSERSTACK_DEBUG,

    /**
     * Sets resolution of VM on BrowserStack
     */
    BROWSERSTACK_RESOLUTION,

    BROWSERSTACK_SELENIUM_VERSION,

    /**
     * Disable flash on Internet Explorer on BrowserStack
     */
    BROWSERSTACK_IE_NO_FLASH,

    /**
     * Specify the Internet Explorer webdriver version on BrowserStack
     */
    BROWSERSTACK_IE_DRIVER,

    /**
     *  Enable the popup blocker in Internet Explorer on BrowserStack
     */
    BROWSERSTACK_IE_ENABLE_POPUPS,

    /**
     * Timeout (in seconds) for retrying file I/O.
     * Used in net.thucydides.core.resources.FileResources.copyResourceTo().
     * Sometimes, file I/O fails on Windows machine due to the way Windows handles memory-mapped
     * files (http://stackoverflow.com/questions/3602783/file-access-synchronized-on-java-object).
     * This property, if set, will retry copying the resource till timeout. A default value is used
     * if the property is not set.
     */
     THUCYDIDES_FILE_IO_RETRY_TIMEOUT,

    /**
     * Three levels are supported: QUIET, NORMAL and VERBOSE
     */
    THUCYDIDES_LOGGING,

    /**
     * The root package for the tests in a given project.
     * If provided, Thucydides will log information about the total number of tests to be executed,
     * and keep a tally of the executed tests. It will also use this as the root package when determining the
     * capabilities associated with a test.
     * If you are using the File System Requirements provider, Thucydides will expect this directory structure to exist
     * at the top of the requirements tree. If you want to exclude packages in a requirements definition and start at a
     * lower level in the hierarchy, use the thucydides.requirement.exclusions property.
     * This is also used by the PackageAnnotationBasedTagProvider to know where to look for annotated requirements.
     */
    THUCYDIDES_TEST_ROOT,

    /**
     * Use this property if you need to completely override the location of requirements for the File System Provider.
     */
    THUCYDIDES_REQUIREMENTS_DIR,

    /**
     * By default, Thucydides will read requirements from the directory structure that contains the stories.
     * When other tag and requirements plugins are used, such as the JIRA plugin, this can cause conflicting
     * tags. Set this property to false to deactivate this feature (it is true by default).
     */
    THUCYDIDES_USE_REQUIREMENTS_DIRECTORIES,

    /**
     * Use this property if you need to completely override the location of requirements for the Annotated Provider.
     * This is recommended if you use File System and Annotated provider simultaneously.
     * The default value is stories.
     */
    THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR,

    /**
     * Determine what the lowest level requirement (test cases, feature files, story files, should be
     * called. 'Story' is used by default. 'feature' is a popular alternative.
     */
    THUCYDIDES_LOWEST_REQUIREMENT_TYPE,

  /**
     * The hierarchy of requirement types.
     * This is the list of requirement types to be used when reading requirements from the file system
     * and when organizing the reports. It is a comma-separated list of tags.The default value is: capability, feature
     */
    THUCYDIDES_REQUIREMENT_TYPES,

    /**
     * When deriving requirement types from a path, exclude any values from this comma-separated list.
     */
    THUCYDIDES_REQUIREMENT_EXCLUSIONS,

    /**
     * What tag names identify the release types (e.g. Release, Iteration, Sprint).
     * A comma-separated list. By default, "Release, Iteration"
     */
    THUCYDIDES_RELEASE_TYPES,

    /**
     * Normally, Thucydides uses SmartElementLocatorFactory, an extension of the AjaxElementLocatorFactory
     * when instantiating page objects. This is to ensure that web elements are available and usable before they are used.
     * For alternative behaviour, you can set this value to DisplayedElementLocatorFactory, AjaxElementLocatorFactory or DefaultElementLocatorFactory.
     */
    THUCYDIDES_LOCATOR_FACTORY,
    /**
     * Where Thucydides stores local data.
     */
    THUCYDIDES_DATA_DIR,

    /**
     * Allows you to override the default thucydides.properties location for properties file.
     */
    PROPERTIES,

    /**
     *  The base directory in which requirements are kept. It is assumed that this directory contains sub folders
     *  src/test/resources. If this property is set, the requirements are read from src/test/resources under this folder
     *  instead of the classpath or working directory. If you need to set an independent requirements directory that
     *  does not follow the src/test/resources convention, use thucydides.requirements.dir instead
     *
     *  This property is used to support situations where your working directory
     *  is different from the requirements base dir (for example when building a multi-module project from parent pom with
     *  requirements stored inside a sub-module : See Jira #Thucydides-100)
     */
    THUCYDIDES_TEST_REQUIREMENTS_BASEDIR,


    /**
     * Set to true if you want the HTML source code to be recorded as well as the screenshots.
     * This is not currently used in the reports.
     */
    THUCYDIDES_STORE_HTML_SOURCE,

    /**
     * If set to true, a copy of the original screenshot will be kept when screenshots are scaled for the reports.
     * False by default to conserve disk space.
     */
    THUCYDIDES_KEEP_UNSCALED_SCREENSHOTS,

    /**
     * If provided, only classes and/or methods with tags in this list will be executed. The parameter expects
     * a tag or comma-separated list of tags in the shortened form.
     * For example, -Dtags="iteration:I1" or -Dtags="color:red,flavor:strawberry"
     */
    TAGS,

    /**
     * Add extra columns to the CSV output, obtained from tag values.
     */
    THUCYDIDES_CSV_EXTRA_COLUMNS,

    /**
     * Write the console headings using ascii-art ("ascii", default value) or in normal text ("normal")
     */
    THUCYDIDES_CONSOLE_HEADINGS,

    /**
     * If set to true, Asciidoc formatting will be supported in the narrative texts.
     */
    NARRATIVE_FORMAT,

    /**
     * What format should test results be generated in.
     * By default, this is "json,xml".
     */
    OUTPUT_FORMATS,

    /**
     * Path to PhantomJS executable
     */
    PHANTOMJS_BINARY_PATH,

    /**
     * If set to true, don't format embedded tables in JBehave or Gherkin steps.
     * False by default.
     */
    IGNORE_EMBEDDED_TABLES,

    /**
     * If set, this will display the related tag statistics on the home page.
     * If you are using external requirements, you may not want to display these tags on the dashboard.
     */
    SHOW_RELATED_TAGS,

    /**
     * If set to true (the default value), a story tag will be extracted from the test case or feature file
     * containing the test.
     */
    USE_TEST_CASE_FOR_STORY_TAG,

    /**
     * Display the pie charts on the dashboard by default.
     * If this is set to false, the pie charts will be initially hidden on the dashboard.
     */
    SHOW_PIE_CHARTS,

    /**
     * If set, this will define the list of tag types to appear on the dashboard screens
     */
    DASHBOARD_TAG_LIST,

    /**
     * If set, this will define the list of tag types to be excluded from the dashboard screens
     */
    DASHBOARD_EXCLUDED_TAG_LIST,

    /**
     * Format the JSON test outcomes nicely.
     * "true" or "false", turned off by default.
     */
    JSON_PRETTY_PRINTING,

    /**
     * What charset to use for JSON processing.
     * Defaults to UTF-8
     */
    JSON_CHARSET,

    /**
     * If set to true, the RetryFilteringRunNotifier will be used to attempt to rerun failing tests.
     */
    JUNIT_RETRY_TESTS,

    /**
     * Stack traces are by default decluttered for readability.
     * For example, calls to instrumented code or internal test libraries is removed.
     * This behaviour can be deactivated by setting this property to false.
     */
    SIMPLIFIED_STACK_TRACES,

    /**
     * Keep the Thucydides session data between tests.
     * Normally, the session data is cleared between tests.
     */
    THUCYDIDES_MAINTAIN_SESSION;

    private String propertyName;
    public static final int DEFAULT_HEIGHT = 700;
    public static final int DEFAULT_WIDTH = 960;

    private ThucydidesSystemProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    private ThucydidesSystemProperty() {
        this.propertyName = name().replaceAll("_",".").toLowerCase();
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

    public boolean isDefinedIn(EnvironmentVariables environmentVariables) {
        return StringUtils.isNotEmpty(from(environmentVariables));
    }
}
