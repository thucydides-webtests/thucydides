package net.thucydides.core.webdriver;

import com.google.common.collect.Lists;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewDriver {

    private WebDriverFactory webDriverFactory;

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    ChromeDriver chromeDriver;

    EnvironmentVariables environmentVariables;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(webdriverInstanceFactory.newChromeDriver(any(ChromeOptions.class))).thenReturn(chromeDriver);

        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
    }

    @Captor
    ArgumentCaptor<ChromeOptions> chromeOptionsArgument;

    @Test
    public void should_pass_chrome_switches_when_creating_a_chrome_driver() throws Exception {
        environmentVariables.setProperty("chrome.switches","--homepage=about:blank,--no-first-run");

        webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);

        verify(webdriverInstanceFactory).newChromeDriver(chromeOptionsArgument.capture());
        assertThat(argumentsFrom(chromeOptionsArgument), hasItems("--homepage=about:blank", "--no-first-run"));
    }

    private List<String> argumentsFrom(ArgumentCaptor<ChromeOptions> chromeOptionsArgument) throws IOException, JSONException {
        JSONArray argumentsPassed = (JSONArray) chromeOptionsArgument.getValue().toJson().get("args");
        List<String> arguments = Lists.newArrayList();
        for(int i = 0; i < argumentsPassed.length(); i++) {
            arguments.add((String)argumentsPassed.get(i));
        }
        return arguments;
    }

}
