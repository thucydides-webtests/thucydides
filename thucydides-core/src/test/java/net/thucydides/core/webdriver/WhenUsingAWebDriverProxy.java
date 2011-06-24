package net.thucydides.core.webdriver;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.PagesEventListener;
import net.thucydides.core.webdriver.mocks.MockWebDriver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class WhenUsingAWebDriverProxy {

    @Mock
    FirefoxDriver mockFirefoxDriver;

    @Mock
    HtmlUnitDriver mockHtmlUnitDriver;

    @Mock
    Pages pages;

    TestableWebDriverFacade webDriverFacade;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        webDriverFacade = new TestableWebDriverFacade(mockFirefoxDriver, true);
    }

    @Test
    public void the_webdriver_proxy_looks_and_feels_like_a_webdriver() {
        WebDriver driver = (WebDriver) WebdriverProxyFactory.getFactory().proxyFor(MockWebDriver.class);

        assertThat(driver, is(notNullValue()));
        assertThat(WebDriver.class.isAssignableFrom(driver.getClass()), is(true));
    }

    @Test
    public void the_proxied_webdriver_should_be_accessible_if_required() {
        WebDriver driver = (WebDriver) WebdriverProxyFactory.getFactory().proxyFor(MockWebDriver.class);

        MockWebDriver proxiedDriver = (MockWebDriver) ((WebDriverFacade) driver).getProxiedDriver();

        assertThat(proxiedDriver, is(notNullValue()));
        assertThat(MockWebDriver.class.isAssignableFrom(proxiedDriver.getClass()), is(true));
    }


    private class TestableWebDriverFacade extends WebDriverFacade {

        private final WebDriver mockDriver;
        private final boolean driverInstanciated;
        public TestableWebDriverFacade(WebDriver mockDriver, boolean driverInstanciated) {
            super(FirefoxDriver.class);
            this.mockDriver = mockDriver;
            this.driverInstanciated = driverInstanciated;

        }

        @Override
        public WebDriver getDriverInstance() {
            return mockDriver;
        }

        @Override
        protected WebDriver newProxyDriver() {
            return mockDriver;
        }

        @Override
        protected boolean proxyInstanciated() {
            return driverInstanciated;
        }
    }

    @Test
    public void the_webdriver_proxy_should_handle_get() {
        webDriverFacade.get("http://www.google.com");
        verify(mockFirefoxDriver).get("http://www.google.com");
    }

    @Test
    public void the_webdriver_proxy_should_quit_driver_when_reset() {
        webDriverFacade.get("http://www.google.com");
        WebDriver originalDriver = webDriverFacade.getDriverInstance();

        webDriverFacade.reset();

        verify(mockFirefoxDriver).quit();
    }

    @Test
    public void the_webdriver_proxy_should_remove_proxied_driver_when_reset() {
        webDriverFacade.get("http://www.google.com");
        WebDriver originalDriver = webDriverFacade.getDriverInstance();

        webDriverFacade.reset();

        assertThat(webDriverFacade.proxiedWebDriver, is(nullValue()));
    }

    @Test
    public void the_webdriver_proxy_should_handle_find_element() {
        webDriverFacade.findElement(By.id("q"));
        verify(mockFirefoxDriver).findElement(By.id("q"));
    }

    @Test
    public void the_webdriver_proxy_should_handle_find_elements() {
        webDriverFacade.findElements(By.id("q"));
        verify(mockFirefoxDriver).findElements(By.id("q"));
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_screenshot() {
        webDriverFacade.getScreenshotAs(OutputType.FILE);
        verify(mockFirefoxDriver).getScreenshotAs(OutputType.FILE);
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_current_url() {
        webDriverFacade.getCurrentUrl();
        verify(mockFirefoxDriver).getCurrentUrl();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_page_source() {
        webDriverFacade.getPageSource();
        verify(mockFirefoxDriver).getPageSource();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_title() {
        webDriverFacade.getTitle();
        verify(mockFirefoxDriver).getTitle();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_window_handle() {
        webDriverFacade.getWindowHandle();
        verify(mockFirefoxDriver).getWindowHandle();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_window_handles() {
        webDriverFacade.getWindowHandles();
        verify(mockFirefoxDriver).getWindowHandles();
    }

    @Test
    public void the_webdriver_proxy_should_handle_navigate() {
        webDriverFacade.navigate();
        verify(mockFirefoxDriver).navigate();
    }

    @Test
    public void the_webdriver_proxy_should_handle_switchTo() {
        webDriverFacade.switchTo();
        verify(mockFirefoxDriver).switchTo();
    }

    @Test
    public void the_webdriver_proxy_should_handle_quit_if_a_proxied_driver_exists() {
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.quit();
        verify(mockFirefoxDriver).quit();
    }

    @Test
    public void the_webdriver_proxy_should_not_call_quit_if_a_proxied_driver_doesnt_exist() {
        WebDriverFacade webDriverFacade = new TestableWebDriverFacade(mockFirefoxDriver, false);
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.quit();
        verify(mockFirefoxDriver, never()).quit();
    }

    @Test
    public void the_webdriver_proxy_should_handle_close_if_a_proxied_driver_exists() {
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.close();
        verify(mockFirefoxDriver).close();
    }

    @Test
    public void the_webdriver_proxy_should_not_call_close_if_a_proxied_driver_doesnt_exist() {
        WebDriverFacade webDriverFacade = new TestableWebDriverFacade(mockFirefoxDriver, false);
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.close();
        verify(mockFirefoxDriver, never()).close();
    }

    @Test
    public void the_webdriver_proxy_should_handle_manage() {
        webDriverFacade.manage();
        verify(mockFirefoxDriver).manage();
    }

    @Test
    public void the_webdriver_proxy_should_not_instanciate_the_webdriver_instance_until_a_method_is_invoked() {

        TestableWebDriverFacade webDriverFacade = new TestableWebDriverFacade(mockFirefoxDriver, true);

        webDriverFacade.get("http://www.google.com");
        webDriverFacade.findElement(By.id("q"));
        webDriverFacade.getScreenshotAs(OutputType.FILE);

        verify(mockFirefoxDriver).get("http://www.google.com");
        verify(mockFirefoxDriver).findElement(By.id("q"));
        verify(mockFirefoxDriver).getScreenshotAs(OutputType.FILE);

    }


    @Mock
    ThucydidesWebDriverEventListener eventListener;

    @Test
    public void when_a_listener_is_registered_the_webdriver_proxy_should_notify_the_listener_when_the_browser_is_opened() {

        WebDriverFacade webDriverFacade = new WebDriverFacade(MockWebDriver.class);
        WebdriverProxyFactory.getFactory().registerListener(eventListener);

        webDriverFacade.get("http://www.google.com");

        verify(eventListener).driverCreatedIn(any(WebDriver.class));
    }

    @Test
    public void a_test_should_be_able_to_override_the_facade_with_a_mock_driver_for_a_pages_object() {

        WebdriverProxyFactory.getFactory().useMockDriver(mockFirefoxDriver);

        Pages pages = new Pages();
        WebDriverFacade webDriverFacade = new WebDriverFacade(MockWebDriver.class);
        pages.setDriver(webDriverFacade);

        PagesEventListener pagesEventListener = new PagesEventListener(pages);
        WebdriverProxyFactory.getFactory().registerListener(pagesEventListener);

        webDriverFacade.get("http://www.google.com");

        WebdriverProxyFactory.getFactory().clearMockDriver();

        WebDriver proxiedWebDriver = ((WebDriverFacade) pages.getDriver()).getProxiedDriver();
        assertThat(proxiedWebDriver, is((WebDriver)mockFirefoxDriver));
    }

    @Test
    public void when_a_page_listener_is_registered_the_webdriver_proxy_should_update_the_page_object_with_the_driver() {

        Pages pages = new Pages();
        WebDriverFacade webDriverFacade = new WebDriverFacade(MockWebDriver.class);
        pages.setDriver(webDriverFacade);

        PagesEventListener pagesEventListener = new PagesEventListener(pages);
        WebdriverProxyFactory.getFactory().registerListener(pagesEventListener);

        webDriverFacade.get("http://www.google.com");

        assertThat(pages.getDriver(), is((WebDriver)webDriverFacade));
    }

    @Test
    public void the_webdriver_proxy_should_do_nothing_if_screenshots_are_not_supported() {

        TestableWebDriverFacade webDriverFacade = new TestableWebDriverFacade(mockHtmlUnitDriver, true);

        File screenshot = webDriverFacade.getScreenshotAs(OutputType.FILE);

        assertThat(screenshot, is(nullValue()));

    }




}
