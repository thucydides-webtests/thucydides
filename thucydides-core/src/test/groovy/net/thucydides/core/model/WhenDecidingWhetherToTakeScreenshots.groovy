package net.thucydides.core.model

import spock.lang.Specification
import spock.lang.Unroll
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.annotations.Screenshots

class WhenDecidingWhetherToTakeScreenshots extends Specification {

    def configuration = Mock(Configuration)

    @Unroll
    def "taking screenshots"() {

        when:
            configuration.takeVerboseScreenshots() >> takeVerboseScreenshots
            configuration.onlySaveFailingScreenshots() >> onlyTakeFailingverboseScreenshots
            ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        then:
            permissions.areAllowed(screenshotLevel) == shouldBeAllowed

        where:
        onlyTakeFailingverboseScreenshots | takeVerboseScreenshots | screenshotLevel                 | shouldBeAllowed
        true                              | true                    | TakeScreenshots.FOR_EACH_ACTION | false
        true                              | true                    | TakeScreenshots.FOR_EACH_STEP   | false
        true                              | true                    | TakeScreenshots.FOR_FAILURES    | true

        false                             | false                   | TakeScreenshots.FOR_EACH_ACTION | false
        false                             | false                   | TakeScreenshots.FOR_EACH_STEP   | true
        false                             | false                   | TakeScreenshots.FOR_FAILURES    | true

        false                             | true                    | TakeScreenshots.FOR_EACH_ACTION | true
        false                             | true                    | TakeScreenshots.FOR_EACH_STEP   | true
        false                             | true                    | TakeScreenshots.FOR_FAILURES    | true
    }

    def "overriding screenshot configuration using method annotations"() {
        when:
            configuration.takeVerboseScreenshots() >> true
        then:
            checkOnlyTakeOnFailures()
            checkOnEachStep()
            checkDefaultScreenshotPermissions()
    }

    def "overriding only-on-failure screenshot configuration using method annotations"() {
        when:
            configuration.onlySaveFailingScreenshots() >> true
        then:
            checkOverrideOnlyOnFailure()
            checkOverrideOnlyOnFailureWithPerStepMode()
            checkOverrideOnlyOnFailureWithVerboseMode()
    }

    @Screenshots(onlyOnFailures=true)
    void checkOnlyTakeOnFailures() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(forEachStep=true)
    void checkOnEachStep() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots()
    void checkDefaultScreenshotPermissions() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots()
    void checkOverrideOnlyOnFailure() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(forEachStep=true)
    void checkOverrideOnlyOnFailureWithPerStepMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(forEachAction=true)
    void checkOverrideOnlyOnFailureWithVerboseMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }
}
