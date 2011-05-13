package net.thucydides.core.steps;

import java.util.List;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class StepAnnotations {
    
    private StepAnnotations() {}

    /**
     * Instantiates the step scenario fields in a test case.
     */
    public static void injectNestedScenarioStepsInto(final ScenarioSteps scenarioSteps,
                                                     final StepFactory stepFactory,
                                                     final Class<? extends ScenarioSteps> scenarioStepsClass) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findOptionalAnnotatedFields(scenarioStepsClass);
        instanciateScenarioStepFields(scenarioSteps, stepFactory, stepsFields);
     }


    private static void instanciateScenarioStepFields(
            final Object testCaseOrSteps, final StepFactory stepFactory,
            final List<StepsAnnotatedField> stepsFields) {
        for(StepsAnnotatedField stepsField : stepsFields) {
               Class<? extends ScenarioSteps> scenarioStepsClass = stepsField.getFieldClass();
               ScenarioSteps steps = (ScenarioSteps) stepFactory.newSteps(scenarioStepsClass);  
               injectNestedScenarioStepsInto(steps, stepFactory, scenarioStepsClass);
               stepsField.setValue(testCaseOrSteps, steps);
           }
    }

}
