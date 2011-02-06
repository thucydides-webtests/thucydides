package net.thucydides.junit.runners.mocks;

import java.io.File;

import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.runners.WebDriverFactory;

import org.junit.runners.model.InitializationError;
import org.openqa.selenium.TakesScreenshot;
import org.thucydides.core.screenshots.Photographer;
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
