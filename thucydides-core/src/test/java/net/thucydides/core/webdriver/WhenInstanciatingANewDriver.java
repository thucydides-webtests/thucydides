package net.thucydides.core.webdriver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewDriver {

    private WebDriverFactory webDriverFactory;

    private WebDriver driver;

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    InternetExplorerDriver ieDriver;

    @Mock
    FirefoxProfile profile;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class)).thenReturn(firefoxDriver);
        when(webdriverInstanceFactory.newInstanceOf(ChromeDriver.class)).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newInstanceOf(InternetExplorerDriver.class)).thenReturn(ieDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(FirefoxDriver.class), any(FirefoxProfile.class))).thenReturn(firefoxDriver);

        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory);
    }

    @Test
    public void should_support_creating_a_firefox_driver() {
         driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
         assertThat(driver, instanceOf(FirefoxDriver.class));
    }

    @Test
    public void should_support_creating_a_chrome_driver() {
         driver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
         assertThat(driver, instanceOf(ChromeDriver.class));
    }

    @Test
    public void should_support_creating_an_internet_explorer_driver() {
         driver = webDriverFactory.newInstanceOf(SupportedWebDriver.IEXPLORER);
         assertThat(driver, instanceOf(InternetExplorerDriver.class));
    }


}
