package net.thucydides.core.pages.scheduling;

import net.thucydides.core.pages.PageObject;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Sleeper;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenRunningPolledTests {

    @Mock
    WebDriver driver;

    @Mock
    Sleeper sleeper;

    @Mock
    Clock clock;

    @Mock
    WebDriver.Navigation navigation;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    int counter;
    private ThucydidesFluentWait<WebDriver> waitFor;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        when(driver.navigate()).thenReturn(navigation);

        counter = 0;
    }

    class SlowPage extends PageObject {

        public SlowPage(final WebDriver driver) {
            super(driver);
        }
    }


    private ExpectedCondition<Boolean> weHaveWaitedEnough() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                counter++;
                return counter > 3;
            }
        };
    }

    @Test
    public void if_requested_page_should_be_refreshed_during_wait() {
        SlowPage page = new SlowPage(driver);

        page.waitForRefresh()
            .withTimeoutOf(100).milliseconds()
            .pollingEvery(10).milliseconds()
            .until(weHaveWaitedEnough());

        verify(navigation,times(3)).refresh();
    }

    @Test
    public void normally_page_should_be_not_refreshed_during_wait() {
        SlowPage page = new SlowPage(driver);

        page.waitForCondition()
            .withTimeoutOf(100).milliseconds()
            .pollingEvery(10).milliseconds()
            .until(weHaveWaitedEnough());

        verify(navigation,never()).refresh();
    }

    @Test
    public void page_should_pause_during_wait() throws InterruptedException {

        Clock clock = new org.openqa.selenium.support.ui.SystemClock();
        NormalFluentWait<WebDriver> waitFor = new NormalFluentWait(driver, clock, sleeper);

        waitFor.withTimeoutOf(100).milliseconds()
               .pollingEvery(10).milliseconds()
               .until(weHaveWaitedEnough());

        verify(sleeper, times(3)).sleep(new Duration(10, TimeUnit.MILLISECONDS));
    }


    private ExpectedCondition<Boolean> weSpitTheDummy() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                counter++;
                if (counter > 3) {
                    throw new AssertionError("Oh drat");
                }
                return false;
            }
        };
    }

    private ExpectedCondition<Boolean> weTakeTooLong() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return false;
            }
        };
    }

    @Test(expected = AssertionError.class)
    public void should_propogate_exception_if_test_fails() {
        SlowPage page = new SlowPage(driver);

        page.waitForCondition()
            .withTimeoutOf(100).milliseconds()
            .pollingEvery(10).milliseconds()
            .until(weSpitTheDummy());

    }

    @Test(expected = TimeoutException.class)
    public void should_timeout_if_takes_too_long() {
        SlowPage page = new SlowPage(driver);

        page.waitForCondition()
            .withTimeoutOf(10).milliseconds()
            .pollingEvery(5).milliseconds()
            .until(weTakeTooLong());

    }

}
