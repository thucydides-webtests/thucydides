package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.WebDriver;

public class WhenInstanciatingANewDriver {

    private TestableWebDriverFactory webDriverFactory;
    
    @Before
    public void createATestableDriverFactory() {
        webDriverFactory = new TestableWebDriverFactory();
    }
    
    @Test
    public void should_support_creating_a_firefox_driver() {
         WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
         assertThat(driver.toString(), containsString("FirefoxDriver"));
    }

    @Test
    public void should_support_creating_a_chrome_driver() {
         WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
         assertThat(driver.toString(), containsString("ChromeDriver"));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void should_throw_exception_with_helpful_message_if_driver_unsupported() {
         thrown.expect(IllegalArgumentException.class);
         thrown.expectMessage("Driver type cannot be null");
         webDriverFactory.newInstanceOf(null);
    }
    
}
