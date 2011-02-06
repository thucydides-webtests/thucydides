package net.thucydides.junit.runners.mocks;

import java.io.File;

import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.runners.model.InitializationError;
import org.openqa.selenium.TakesScreenshot;
import static org.mockito.Mockito.mock;

/**
 * A partially-mocked version of the Thucydides runner
 * It behaves exactly as a real Thucydides runner, but allows you to override
 * the WebDriverFactory in order to inject a mock object and see what methods
 * are being invoked. It also mocks out the photographer.
 * 
 * @author johnsmart
 *
 */
public class MockThucydidesRunner extends ThucydidesRunner {

    private Photographer mockPhotographer = mock(Photographer.class);

    public MockThucydidesRunner(Class<?> klass,
                                WebDriverFactory factory) throws InitializationError {
        super(klass);
        setWebDriverFactory(factory);  
    }
    
    @Override
    protected Photographer getPhotographerFor(TakesScreenshot driver, File outputDirectory) {
        return mockPhotographer;
    }

}
