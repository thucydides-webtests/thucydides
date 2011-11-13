package net.thucydides.core.webdriver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class WhenObtainingAWebdriverInstance {

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    FirefoxDriver firefoxDriver;

    @Mock
    HtmlUnitDriver htmlUnitDriver;

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
        protected WebDriver newWebdriverInstance(Class<? extends WebDriver> webdriverClass) {
            if (webdriverClass == FirefoxDriver.class) {
                return firefoxDriver;
            } else if (webdriverClass == ChromeDriver.class) {
                return chromeDriver;
            } else if (webdriverClass == HtmlUnitDriver.class) {
                return htmlUnitDriver;
            } else {
                throw new AssertionError("Unsupported webdriver class " + webdriverClass);
            }
        }
    }

    class InvalidWebDriverClass extends FirefoxDriver {
        InvalidWebDriverClass() throws IllegalAccessException {
            throw new IllegalAccessException();
        }
    }

    @Test(expected = UnsupportedDriverException.class)
    public void should_refuse_to_instanciate_an_illegal_driver_class() {
        WebDriverFactory factory = new WebDriverFactory();

        factory.newWebdriverInstance(InvalidWebDriverClass.class);
    }

    class InstantiationExceptionWebDriverClass extends FirefoxDriver {
        InstantiationExceptionWebDriverClass() throws InstantiationException {
            throw new InstantiationException();
        }
    }

    @Test(expected = UnsupportedDriverException.class)
    public void should_refuse_to_instanciate_an_invalid_driver_class() {
        WebDriverFactory factory = new WebDriverFactory();

        factory.newWebdriverInstance(InstantiationExceptionWebDriverClass.class);
    }


    @Test
    public void the_factory_works_with_chrome() {
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
        assertThat(driver,instanceOf(ChromeDriver.class));
    }

    @Test
    public void the_factory_knows_what_class_the_chrome_driver_uses() {
        Class driverClass = webDriverFactory.getClassFor(SupportedWebDriver.CHROME);
        assertThat(driverClass.getName(), is(ChromeDriver.class.getName()));
    }

    @Test
    public void the_factory_works_with_firefox() {
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
        assertThat(driver,instanceOf(FirefoxDriver.class));
    }
    
    @Test
    public void the_factory_works_with_htmlunit() {
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.HTMLUNIT);
        assertThat(driver,instanceOf(HtmlUnitDriver.class));
    }

    @Test
     public void the_factory_knows_what_class_the_firefox_driver_uses() {
         Class driverClass = webDriverFactory.getClassFor(SupportedWebDriver.FIREFOX);
         assertThat(driverClass.getName(), is(FirefoxDriver.class.getName()));
     }

    @Test
     public void the_factory_knows_what_class_the_htmlunit_driver_uses() {
         Class driverClass = webDriverFactory.getClassFor(SupportedWebDriver.HTMLUNIT);
         assertThat(driverClass.getName(), is(HtmlUnitDriver.class.getName()));
     }

    @Test(expected=IllegalArgumentException.class)
    public void the_driver_type_is_mandatory() {
        webDriverFactory.newInstanceOf(null);
    }


    @Test(expected=IllegalArgumentException.class)
    public void the_driver_type_is_mandatory_to_obtain_the_driver_class_too() {
        webDriverFactory.newInstanceOf(null);
    }

    @Test
    public void the_factory_can_provide_a_list_of_supported_drivers() {
        String supportedDrivers = SupportedWebDriver.listOfSupportedDrivers();
        assertThat(supportedDrivers, allOf(containsString("FIREFOX"), containsString("CHROME")));
    }



}
