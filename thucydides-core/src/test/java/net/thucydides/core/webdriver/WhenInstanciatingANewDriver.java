package net.thucydides.core.webdriver;

import net.thucydides.core.webdriver.mocks.TestableWebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class WhenInstanciatingANewDriver {

    private TestableWebDriverFactory webDriverFactory;

    private WebDriver driver;
    @Before
    public void createATestableDriverFactory() {
        webDriverFactory = new TestableWebDriverFactory();
    }


    @After
    public void closeDriver() {
        driver.quit();
    }
    
    @Test
    public void should_support_creating_a_firefox_driver() {
         driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
         assertThat(driver.toString(), containsString("FirefoxDriver"));
    }

    @Test
    public void should_support_creating_a_chrome_driver() {
         driver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
         assertThat(driver.toString(), containsString("ChromeDriver"));
    }
}
