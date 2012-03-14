package net.thucydides.core.steps;


import com.google.inject.Inject;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLoggingListener implements StepListener {

    // STAR WARS
    private static final String BANNER =
            "\n\n-------------------------------------------------------------------------------------------------------\n" +
                    ".___________. __    __   __    __    ______ ____    ____  _______   __   _______   _______     _______.\n" +
                    "|           ||  |  |  | |  |  |  |  /      |\\   \\  /   / |       \\ |  | |       \\ |   ____|   /       |\n" +
                    "`---|  |----`|  |__|  | |  |  |  | |  ,----' \\   \\/   /  |  .--.  ||  | |  .--.  ||  |__     |   (----`\n" +
                    "    |  |     |   __   | |  |  |  | |  |       \\_    _/   |  |  |  ||  | |  |  |  ||   __|     \\   \\    \n" +
                    "    |  |     |  |  |  | |  `--'  | |  `----.    |  |     |  '--'  ||  | |  '--'  ||  |____.----)   |   \n" +
                    "    |__|     |__|  |__|  \\______/   \\______|    |__|     |_______/ |__| |_______/ |_______|_______/    \n" +
                    "                                                                                                       \n" +
                    "-------------------------------------------------------------------------------------------------------\n";

    // Standard
    private static final String TEST_STARTED =

            "\n  _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                    " |_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                    "   | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                    "   | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                    "   |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n" +
                    "                                                                   \n";

    private static final String TEST_PASSED =
            "\n        __    _____ _____ ____ _____   ____   _    ____  ____  _____ ____  \n" +
                    "  _     \\ \\  |_   _| ____/ ___|_   _| |  _ \\ / \\  / ___|/ ___|| ____|  _ \\ \n" +
                    " (_)_____| |   | | |  _| \\___ \\ | |   | |_) / _ \\ \\___ \\\\___ \\|  _| | | | |\n" +
                    "  _|_____| |   | | | |___ ___) || |   |  __/ ___ \\ ___) |___) | |___| |_| |\n" +
                    " (_)     | |   |_| |_____|____/ |_|   |_| /_/   \\_\\____/|____/|_____|____/ \n" +
                    "        /_/                                                                \n";

    private static final String TEST_FAILED =
            "\n           __  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
                    "  _       / / |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
                    " (_)_____| |    | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
                    "  _|_____| |    | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
                    " (_)     | |    |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
                    "          \\_\\                                                            \n";

    private static final String TEST_SKIPPED =
            "\n            __  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                    "  _        / / |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                    " (_)_____ / /    | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                    "  _|_____/ /     | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                    " (_)    /_/      |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n" +
                    "                                                                                \n";

    private static final String FAILURE =
            "\n  _____ _    ___ _     _   _ ____  _____ \n" +
                    " |  ___/ \\  |_ _| |   | | | |  _ \\| ____|\n" +
                    " | |_ / _ \\  | || |   | | | | |_) |  _|  \n" +
                    " |  _/ ___ \\ | || |___| |_| |  _ <| |___ \n" +
                    " |_|/_/   \\_\\___|_____|\\___/|_| \\_\\_____|\n" +
                    "                                         \n";

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final LoggingLevel loggingLevel;

    @Inject
    public ConsoleLoggingListener(EnvironmentVariables environmentVariables) {
        this.logger = LoggerFactory.getLogger(Thucydides.class);
        this.environmentVariables = environmentVariables;
        this.loggingLevel = getLoggingLevel();
        logBanner();
    }

    private void logBanner() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(BANNER);
        }
    }

    private boolean loggingLevelIsAtLeast(LoggingLevel minimumLoggingLevel) {
        return (loggingLevel.compareTo(minimumLoggingLevel) >= 0);
    }

    private LoggingLevel getLoggingLevel() {
        String logLevel = ThucydidesSystemProperty.LOGGING.from(environmentVariables,
                LoggingLevel.NORMAL.name());

        return LoggingLevel.valueOf(logLevel);
    }

    @Override
    public void testSuiteStarted(Class<?> storyClass) {
    }

    @Override
    public void testSuiteStarted(Story story) {
    }

    @Override
    public void testSuiteFinished() {
    }

    public void testStarted(String description) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(TEST_STARTED + "\nTEST: " + description + underline(TEST_STARTED));
        }
    }

    private String underline(String banner) {
        StringBuilder underline = new StringBuilder();
        int endOfLine = banner.indexOf('\n', 1);
        if (endOfLine >= 0) {
            underline.append(StringUtils.repeat('-', endOfLine));
        } else {
            underline.append(StringUtils.repeat('-', banner.length()));
        }
        return "\n" + underline.toString();
    }

    public void testFinished(TestOutcome result) {
        if (result.isFailure()) {
            logFailure(result);
        } else if (result.isPending()) {
            logPending(result);
        } else if (result.isSkipped()) {
            logSkipped(result);
        } else if (result.isSuccess()) {
            logSuccess(result);
        }
    }

    private void logFailure(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(TEST_FAILED + "\nTEST: " + result.getTitle() + " failed" + underline(TEST_FAILED));
        }
    }

    private void logPending(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(TEST_SKIPPED + "\nTEST: " + result.getTitle() + " is pending" + underline(TEST_SKIPPED));

        }
    }

    private void logSkipped(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(TEST_SKIPPED + "\nTEST: " + result.getTitle() + " is skipped" + underline(TEST_SKIPPED));
        }
    }

    private void logSuccess(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(TEST_PASSED + "\nTEST: " + result.getTitle() + " passed" + underline(TEST_PASSED));
        }
    }

    public void stepStarted(ExecutedStepDescription description) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("STARTING STEP " + description.getTitle());
        }
    }

    @Override
    public void skippedStepStarted(ExecutedStepDescription description) {
        stepStarted(description);
    }

    public void stepFinished() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("FINISHING STEP");
        }
    }

    public void stepFailed(StepFailure failure) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("STEP FAILED: " + failure.getMessage());
        }
    }

    @Override
    public void lastStepFailed(StepFailure failure) {
    }

    public void stepIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("IGNORING STEP");
        }
    }

    @Override
    public void stepIgnored(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("IGNORING STEP + (" + message + ")");
        }
    }

    public void stepPending() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("PENDING STEP");
        }
    }

    @Override
    public void stepPending(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            logger.info("PENDING STEP + (" + message + ")");
        }
    }


    public void testFailed(Throwable cause) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info(FAILURE + "\n" + cause.getMessage());
            underline(FAILURE);
        }
    }

    public void testIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            logger.info("TEST IGNORED");
        }
    }

    @Override
    public void notifyScreenChange() {
    }
}
