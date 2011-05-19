package net.thucydides.junit.runners;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Instanciating new Webdriver instances. When using the Thucydides test runner,
 * new WebDriver driver instances are instanciated based on system properties.
 * 
 * @author johnsmart
 * 
 */
public class WhenInstanciatingANewTestRunner  extends AbstractTestStepRunnerTest {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void the_default_output_directory_should_follow_the_maven_convention() throws InitializationError {

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class, mockBrowserFactory);

        File outputDirectory = runner.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR + "thucydides"));
    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void system_should_complain_if_we_use_an_unsupported_driver()
            throws InitializationError {
        
        thrown.expect(UnsupportedDriverException.class);
        thrown.expectMessage(JUnitMatchers.containsString("htmlunit is not a supported browser. Supported driver values are:"));
        
        System.setProperty("webdriver.driver", "htmlunit");

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class, mockBrowserFactory);

       runner.run(new RunNotifier());
    }


    @Test
    public void iexplored_is_not_currently_a_supported_driver()
            throws InitializationError {
        
        thrown.expect(UnsupportedDriverException.class);
        thrown.expectMessage(JUnitMatchers.containsString("iexplorer is not a supported browser. Supported driver values are:"));
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class, mockBrowserFactory);

        System.setProperty("webdriver.driver", "iexplorer");
        runner.run(new RunNotifier());
    }    


    
    @Test
    public void the_output_directory_can_be_defined_by_a_system_property() throws InitializationError {

        System.setProperty("thucydides.outputDirectory", "target" + FILE_SEPARATOR
        											   + "reports" + FILE_SEPARATOR
        											   + "thucydides");

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class, mockBrowserFactory);

        File outputDirectory = runner.getOutputDirectory();
        
        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR 
												   + "reports" + FILE_SEPARATOR
												   + "thucydides"));

    }    

    @Test
    public void the_output_directory_can_be_defined_by_a_system_property_using_any_standard_separators() throws InitializationError {

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class, mockBrowserFactory);
        
        System.setProperty("thucydides.outputDirectory", "target/reports/thucydides");

        File outputDirectory = runner.getOutputDirectory();
        
        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR 
												   + "reports" + FILE_SEPARATOR
												   + "thucydides"));

    }

}
