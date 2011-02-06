package net.thucydides.core.webdriver;

import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class WhenObtainingAWebdriverInstance {

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    FirefoxDriver firefoxDriver;

    WebDriverFactory webDriverFactory;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void createNewWebDriverFactoryForTesting() {
        webDriverFactory = new TestableWebDriverFactory();
    }
    
    private class TestableWebDriverFactory extends WebDriverFactory {
        @Override
        protected WebDriver newChromeDriver() {
            return chromeDriver;
        }

        @Override
        protected WebDriver newFirefoxDriver() {
            return firefoxDriver;
        }

    }
    
    @Test
    public void the_factory_works_with_chrome() {
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
        assertThat(driver,instanceOf(ChromeDriver.class));
    }

    @Test
    public void the_factory_works_with_firefox() {
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
        assertThat(driver,instanceOf(FirefoxDriver.class));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void the_driver_type_is_mandatory() {
        webDriverFactory.newInstanceOf(null);
    }
    
    @Test
    public void the_factory_can_provide_a_list_of_supported_drivers() {
        String supportedDrivers = SupportedWebDriver.listOfSupportedDrivers();
        assertThat(supportedDrivers, is("FIREFOX, CHROME"));
    }
    
}
