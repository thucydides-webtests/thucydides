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
     * The WebDriver driver - firefox, chrome, iexplorer, htmlunit, safari.
     */
    DRIVER("webdriver.driver"),

    /**
     * If using a provided driver, what type is it.
     * The implementation class needs to be defined in the webdriver.provided.{type} system property.
    */
    PROVIDED_DRIVER_TYPE("webdriver.provided.type"),

    /**
     * The default starting URL for the application, and base URL for relative paths.
     */
    BASE_URL("webdriver.base.url"),

    /**
     * The URL to be used for remote drivers (including a selenium grid hub)
     */
    REMOTE_URL("webdriver.remote.url"),

    /**
     * What port to run PhantomJS on (used in conjunction with webdriver.remote.url to
     * register with a Selenium hub, e.g. -Dphantomjs.webdriver=5555 -Dwebdriver.remote.url=http://localhost:4444
     */
    PHANTOMJS_WEBDRIVER_PORT("phantomjs.webdriver.port"),

    /**
     * The driver to be used for remote drivers
     */
    REMOTE_DRIVER("webdriver.remote.driver"),

    REMOTE_BROWSER_VERSION("webdriver.remote.browser.version"),

    REMOTE_OS("webdriver.remote.os"),

    /**
     * Path to the Chrome driver, if it is not on the system path.
     */
    CHROME_DRIVER_PATH("webdriver.chrome.driver"),

    /**
     * A unique identifier for the project under test, used to record test statistics.
     */
    PROJECT_KEY("thucydides.project.key"),

    /**
     * What name should appear on the reports
     */
    PROJECT_NAME("thucydides.project.name"),

    /**
     * The home directory for Thucydides output and data files - by default, $USER_HOME/.thucydides
     */
    THUCYDIDES_HOME("thucydides.home"),

    /**
     * Additional options to be added to the JDBC connection for the default file-based database.
     */
    THUCYDIDES_DATABASE_JDBC_OPTIONS("thucydides.database.jdbc.options"),

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
     * Encoding for reports output
     */
    REPORT_ENCODING("thucydides.report.encoding"),

    /**
     * Where should reports be generated.
     */
    OUTPUT_DIRECTORY("thucydides.outputDirectory"),

    /**
     * Should Thucydides only store screenshots for failing steps?
     * This can save disk space and speed up the tests somewhat. Useful for data-driven testing.
     * @Deprecated This property is still supported, but thucydides.take.screenshots provides more fine-grained control.
     */
    ONLY_SAVE_FAILING_SCREENSHOTS("thucydides.only.save.failing.screenshots"),

    /**
     * A set of user-defined capabilities to be used to configure the WebDriver driver.
     * Capabilities should be passed in as a space or semi-colon-separated list of key:value pairs, e.g.
     * "build:build-1234; max-duration:300; single-window:true; tags:[tag1,tag2,tag3]"
     */
    DRIVER_CAPABILITIES("thucydides.driver.capabilities"),

    /**
     * Should Thucydides take screenshots for every clicked button and every selected link?
     * By default, a screenshot will be stored at the start and end of each step.
     * If this option is set to true, Thucydides will record screenshots for any action performed
     * on a WebElementFacade, i.e. any time you use an expression like element(...).click(),
     * findBy(...).click() and so on.
     * This will be overridden if the ONLY_SAVE_FAILING_SCREENSHOTS option is set to true.
     * @Deprecated This property is still supported, but thucydides.take.screenshots provides more fine-grained control.
     */
    VERBOSE_SCREENSHOTS("thucydides.verbose.screenshots"),

    /**
     * If set to true, WebElementFacade events and other step actions will be logged to the console.
     */
    VERBOSE_STEPS("thucydides.verbose.steps"),

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
    THUCYDIDES_TAKE_SCREENSHOTS("thucydides.take.screenshots"),

    /**
     * Should Thucydides display detailed information in the test result tables.
     * If this is set to true, test result tables will display a breakdown of the steps by result.
     * This is false by default.
     */
    SHOW_STEP_DETAILS("thucydides.reports.show.step.details"),

    /**
     * Show statistics for manual tests in the test reports.
     */
    SHOW_MANUAL_TESTS("thucydides.report.show.manual.tests"),

    /**
     * Report on releases
     */
    SHOW_RELEASES("thucydides.report.show.releases"),

    SHOW_PROGRESS("thucydides.report.show.progress"),

    SHOW_HISTORY("thucydides.report.show.history"),

    SHOW_TAG_MENUS("thucydides.report.show.tags"),

    /**
     * Restart the browser every so often during data-driven tests.
     */
    RESTART_BROWSER_FREQUENCY("thucydides.restart.browser.frequency"),

    /**
     * Pause (in ms) between each test step.
     */
    STEP_DELAY("thucycides.step.delay"),

    /**
     * How long should the driver wait for elements not immediately visible, in seconds.
     */
    ELEMENT_TIMEOUT("thucydides.timeout"),

    /**
     * Don't accept sites using untrusted certificates.
     * By default, Thucydides accepts untrusted certificates - use this to change this behaviour.
     */
    REFUSE_UNTRUSTED_CERTIFICATES("refuse.untrusted.certificates"),

    /**
     * Use the same browser for all tests (the "Highlander" rule)
     */
    UNIQUE_BROWSER("thucydides.use.unique.browser"),

    /**
     * The estimated number of steps in a pending scenario.
     * This is used for stories where no scenarios have been defined.
     */
    ESTIMATED_AVERAGE_STEP_COUNT("thucydides.estimated.average.step.count"),

    /**
     * The estimated number of tests in a typical story.
     * Used to estimate functional coverage in the requirements reports.
     */
    ESTIMATED_TESTS_PER_REQUIREMENT("thucydides.estimated.tests.per.requirement"),

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
     *  If defined, the JIRA username required to connect to JIRA.
     */
    JIRA_USERNAME("jira.username"),

    /**
     *  If defined, the JIRA password required to connect to JIRA.
     */
    JIRA_PASSWORD("jira.password"),

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
     * If set, resize screenshots to this size to save space.
     */
    RESIZED_WIDTH("thucydides.resized.image.width"),

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

    /**
     * Enable applets in Firefox.
     * Applets slow down webdriver, so are disabled by default.
     */
    SECURITY_ENABLE_JAVA("security.enable_java"),

    ACTIVTE_HIGHLIGHTING("thucydides.activate.highlighting"),

    /**
     * Batch strategy to use for parallel batches.
     * Allowed values - DIVIDE_EQUALLY (default) and DIVIDE_BY_TEST_COUNT
     */
    BATCH_STRATEGY("thucydides.batch.strategy"),

    /**
     *  A deprecated property that is synonymous with thucydides.batch.size
     */
    BATCH_COUNT("thucydides.batch.count"),

    /**
     *  If batch testing is being used, this is the size of the batches being executed.
     */
    BATCH_SIZE("thucydides.batch.size"),

    /**
     * If batch testing is being used, this is the number of the batch being run on this machine.
     */
    BATCH_NUMBER("thucydides.batch.number"),

    /**
     * HTTP Proxy URL configuration for Firefox and PhantomJS
     */
    PROXY_URL("thucydides.proxy.http"),

    /**
     * HTTP Proxy port configuration for Firefox and PhantomJS
     */
    PROXY_PORT("thucydides.proxy.http_port"),

    /**
     * HTTP Proxy type configuration for Firefox and PhantomJS
     */
    PROXY_TYPE("thucydides.proxy.type"),

    /**
     * HTTP Proxy username configuration for Firefox and PhantomJS
     */
    PROXY_USER("thucydides.proxy.user"),

    /**
     * HTTP Proxy password configuration for Firefox and PhantomJS
     */
    PROXY_PASSWORD("thucydides.proxy.password"),

    /**
     * How long webdriver waits for elements to appear by default, in milliseconds.
     */
    TIMEOUTS_IMPLICIT_WAIT("webdriver.timeouts.implicitlywait"),

    /**
     * Extension packages. This is a list of packages that will be scanned for custom TagProvider implementations.
     * To add a custom tag provider, just implement the TagProvider interface and specify the root package for this
     * provider in this parameter.
     */
    EXTENSION_PACKAGES("thucydides.ext.packages"),

    /**
     * Arguments to be passed to the Chrome driver, separated by commas.
     */
    CHROME_SWITCHES("chrome.switches"),


    /**
     * Preferences to be passed to the Firefox driver, separated by semi-colons (commas often appear in the preference
     * values.
     */
    FIREFOX_PREFERENCES("firefox.preferences"),

    /**
     * Full path to the Firefox profile to be used with Firefox.
     * You can include Java system properties ${user.dir}, ${user.home} and the Windows environment variables %APPDIR%
     * and %USERPROFILE (assuming these are correctly set in the environment)
     */
    FIREFOX_PROFILE("webdriver.firefox.profile"),
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
     * Saucelabs records screenshots as well as videos by default. Since Thucydides also records screenshots,
     * this feature is disabled by default. It can be reactivated using this system property.
     */
    SAUCELABS_RECORD_SCREENSHOTS("saucelabs.record.screenshots"),

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
     * and keep a tally of the executed tests. It will also use this as the root package when determining the
     * capabilities associated with a test.
     * If you are using the File System Requirements provider, Thucydides will expect this directory structure to exist
     * at the top of the requirements tree. If you want to exclude packages in a requirements definition and start at a
     * lower level in the hierarchy, use the thucydides.requirement.exclusions property.
     * This is also used by the AnnotationBasedTagProvider to know where to look for annotated requirements.
     */
    THUCYDIDES_TEST_ROOT("thucydides.test.root"),

    /**
     * Use this property if you need to completely override the location of requirements for the File System Provider.
     */
    REQUIREMENTS_DIRECTORY("thucydides.requirements.dir"),

    /**
     * By default, Thucydides will read requirements from the directory structure that contains the stories.
     * When other tag and requirements plugins are used, such as the JIRA plugin, this can cause conflicting
     * tags. Set this property to false to deactivate this feature (it is true by default).
     */
    USE_REQUIREMENTS_DIRECTORY("thucydides.use.requirements.directories"),

    /**
     * Use this property if you need to completely override the location of requirements for the Annotated Provider.
     * This is recommended if you use File System and Annotated provider simultaneously.
     * The default value is stories.
     */
    ANNOTATED_REQUIREMENTS_DIRECTORY("thucydides.annotated.requirements.dir"),

    /**
     * Determine what the lowest level requirement (test cases, feature files, story files, should be
     * called. 'Story' is used by default. 'feature' is a popular alternative.
     */
    LOWEST_REQUIREMENT_TYPE("thucydides.lowest.requirement.type"),

  /**
     * The hierarchy of requirement types.
     * This is the list of requirement types to be used when reading requirements from the file system
     * and when organizing the reports. It is a comma-separated list of tags.The default value is: capability, feature
     */
    REQUIREMENT_TYPES("thucydides.requirement.types"),

    /**
     * When deriving requirement types from a path, exclude any values from this comma-separated list.
     */
    REQUIREMENT_EXCLUSIONS("thucydides.requirement.exclusions"),

    /**
     * What tag names identify the release types (e.g. Release, Iteration, Sprint).
     * A comma-separated list. By default, "Release, Iteration"
     */
    RELEASE_TYPES("thucydides.release.types"),

    /**
     * Normally, Thucydides uses SmartElementLocatorFactory, an extension of the AjaxElementLocatorFactory
     * when instantiating page objects. This is to ensure that web elements are available and usable before they are used.
     * For alternative behaviour, you can set this value to DisplayedElementLocatorFactory, AjaxElementLocatorFactory or DefaultElementLocatorFactory.
     */
    LOCATOR_FACTORY("thucydides.locator.factory"),
    /**
     * The hierarchy of capability types.
     * This is the list of capability types to be used when reading capabilities from the file system
     * and when organizing the reports. It is a comma-separated list of tags.The default value is: capability, feature
     */
    DATA_DIRECTORY("thucydides.data.dir"),

    /**
     * Allows you to override the default thucydides.properties location for properties file.
     */
    PROPERTIES("properties"),

    STATISTICS_DRIVER("thucydides.statistics.driver_class"),
    STATISTICS_URL("thucydides.statistics.url"),
    STATISTICS_USERNAME("thucydides.statistics.username"),
    STATISTICS_PASSWORD("thucydides.statistics.password"),
    STATISTICS_DIALECT("thucydides.statistics.dialect"),

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
    TEST_REQUIREMENTS_ROOT("thucydides.test.requirements.basedir"),


    /**
     * Set to true if you want the HTML source code to be recorded as well as the screenshots.
     * This is not currently used in the reports.
     */
    THUCYDIDES_STORE_HTML_SOURCE("thucydides.store.html.source"),

    /**
     * If set to true, a copy of the original screenshot will be kept when screenshots are scaled for the reports.
     * False by default to conserve disk space.
     */
    THUCYDIDES_KEEP_UNSCALED_SCREENSHOTS("thucydides.keep.unscaled.screenshots"),

    /**
     * If provided, only classes and/or methods with tags in this list will be executed. The parameter expects
     * a tag or comma-separated list of tags in the shortened form.
     * For example, -Dtags="iteration:I1" or -Dtags="color:red,flavor:strawberry"
     */
    TAGS("tags"),

    /**
     * Add extra columns to the CSV output, obtained from tag values.
     */
    THUCYDIDES_EXTRA_COLUMNS("thucydides.csv.extra.columns"),

    /**
     * Write the console headings using ascii-art ("ascii", default value) or in normal text ("normal")
     */
    THUCYDIDES_CONSOLE_HEADINGS("thucydides.console.headings"),

    /**
     * If set to true, Asciidoc formatting will be supported in the narrative texts.
     */
    THUCYDIDES_NARRATIVE_FORMAT("narrative.format"),

    /**
     * Internal report format used when loading test outcome reports during aggregation - json or xml (xml by default)
     */
    THUCYDIDES_REPORT_FORMAT("thucydides.report.format"),

    /**
     * Path to PhantomJS executable
     */
    PHANTOMJS_PATH("phantomjs.binary.path"),

    /**
     * If set to true, don't format embedded tables in JBehave or Gherkin steps.
     * False by default.
     */
    IGNORE_EMBEDDED_TABLES("ignore.embedded.tables"),

    /**
     * If set, this will display the related tag statistics on the home page.
     * If you are using external requirements, you may not want to display these tags on the dashboard.
     */
    SHOW_RELATED_TAGS("show.related.tags"),

    /**
     * Display the pie charts on the dashboard by default.
     * If this is set to false, the pie charts will be initially hidden on the dashboard.
     */
    SHOW_PIE_CHARTS("show.pie.charts"),

    /**
     * If set, this will define the list of tag types to appear on the dashboard screens
     */
    DASHBOARD_TAG_LIST("dashboard.tag.list"),

    /**
     * If set, this will define the list of tag types to be excluded from the dashboard screens
     */
    DASHBOARD_EXCLUDED_TAG_LIST("dashboard.excluded.tag.list"),

    /**
     * Keep the Thucydides session data between tests.
     * Normally, the session data is cleared between tests.
     */
    MAINTAIN_SESSION("thucydides.maintain.session");

    private String propertyName;
    public static final int DEFAULT_HEIGHT = 700;
    public static final int DEFAULT_WIDTH = 960;

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

    public boolean isDefinedIn(EnvironmentVariables environmentVariables) {
        return StringUtils.isNotEmpty(from(environmentVariables));
    }
}
