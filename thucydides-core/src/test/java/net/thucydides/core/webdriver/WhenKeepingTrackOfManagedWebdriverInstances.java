package net.thucydides.core.webdriver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class WhenKeepingTrackOfManagedWebdriverInstances {

    private WebdriverInstances webdriverInstances;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        webdriverInstances = new WebdriverInstances();
    }

    @Mock
    WebDriver firefoxDriver;

    @Mock
    WebDriver iexplorerDriver;

    @Test
    public void should_be_able_to_register_a_named_driver() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        assertThat(webdriverInstances.getCurrentDriver()).isEqualTo(firefoxDriver);
    }

    @Test
    public void should_be_able_to_close_a_driver() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        webdriverInstances.closeCurrentDriver();
        verify(firefoxDriver).close();
    }

    @Test
    public void should_be_able_to_close_all_drivers() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);
        webdriverInstances.registerDriverCalled("iexplorer").forDriver(iexplorerDriver);

        webdriverInstances.closeAllDrivers();

        verify(firefoxDriver).close();
        verify(iexplorerDriver).close();
    }

    @Test
    public void should_remove_current_driver_when_all_are_closed() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);
        webdriverInstances.registerDriverCalled("iexplorer").forDriver(iexplorerDriver);

        webdriverInstances.closeAllDrivers();

        assertThat(webdriverInstances.getCurrentDriver()).isNull();
    }

    @Test
    public void should_remove_current_driver_when_closed() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        webdriverInstances.closeCurrentDriver();
        assertThat(webdriverInstances.getCurrentDriver()).isNull();
    }

    @Test
    public void should_quit_the_driver_when_the_driver_is_closed() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        webdriverInstances.closeCurrentDriver();
        verify(firefoxDriver).quit();
    }


    @Test
    public void should_not_fail_if_closing_an_inexistant_driver() {
        webdriverInstances.closeCurrentDriver();
    }

    @Test
    public void should_know_when_a_driver_is_registered() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        assertThat(webdriverInstances.driverIsRegisteredFor("firefox")).isTrue();
    }

    @Test
    public void should_know_the_current_driver_in_use() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        assertThat(webdriverInstances.getCurrentDriver()).isEqualTo(firefoxDriver);
    }

    @Test
    public void should_know_the_latest_current_driver_in_use() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);
        webdriverInstances.registerDriverCalled("iexplorer").forDriver(iexplorerDriver);

        assertThat(webdriverInstances.getCurrentDriver()).isEqualTo(iexplorerDriver);
    }

    @Test
    public void should_know_when_a_driver_is_not_registered() {
        webdriverInstances.registerDriverCalled("firefox").forDriver(firefoxDriver);

        assertThat(webdriverInstances.driverIsRegisteredFor("iexplorer")).isFalse();
    }

}
