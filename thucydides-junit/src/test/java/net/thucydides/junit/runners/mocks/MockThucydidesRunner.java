package net.thucydides.junit.runners.mocks;

import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.runners.WebDriverFactory;

import org.junit.runners.model.InitializationError;

/**
 * A partially-mocked version of the Thucydides runner
 * It behaves exactly as a real Thucydides runner, but allows you to override
 * the WebDriverFactory in order to inject a mock object and see what methods
 * are being invoked.
 * 
 * @author johnsmart
 *
 */
public class MockThucydidesRunner extends ThucydidesRunner {

    public MockThucydidesRunner(Class<?> klass,
                                WebDriverFactory factory) throws InitializationError {
        super(klass);
        setWebDriverFactory(factory);        
    }

}
