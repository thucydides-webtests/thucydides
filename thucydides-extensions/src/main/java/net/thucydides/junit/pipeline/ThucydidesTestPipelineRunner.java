package net.thucydides.junit.pipeline;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ThucydidesTestPipelineRunner extends BlockJUnit4ClassRunner {
    public ThucydidesTestPipelineRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
}
