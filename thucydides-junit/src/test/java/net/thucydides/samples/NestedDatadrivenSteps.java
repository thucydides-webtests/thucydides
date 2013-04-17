package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import org.junit.Test;

import static net.thucydides.core.steps.StepData.withTestDataFrom;


public class NestedDatadrivenSteps {

    @Steps
    public SampleScenarioSteps steps;


    @Test
    public void run_data_driven_tests() throws Throwable {
        withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step();
    }
}
