package net.thucydides.core;

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
     * It is activated by default, so that when you run tests from within an IDE they open
     * a Firefox instance with Firebugs. To deactivate, just pass in -Dthucydides.activate.firebugs=false
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
    BATCH_NUMBER("thucydides.batch.number");

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
}
