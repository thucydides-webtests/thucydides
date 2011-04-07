package net.thucydides.junit.steps;

import java.util.List;

import net.thucydides.core.model.ScenarioSteps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.internals.PagesAnnotatedField;
import net.thucydides.junit.internals.StepsAnnotatedField;

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
    public static void injectScenarioStepsInto(final Object testCase, final StepFactory stepFactory) {
        List<StepsAnnotatedField> stepsFields = StepsAnnotatedField.findMandatoryAnnotatedFields(testCase.getClass());
        instanciateScenarioStepFields(testCase, stepFactory, stepsFields);
     }

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

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    public static void injectAnnotatedPagesObjectInto(final Object testCase, final Pages pages) {
       PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
       if (pagesField != null) {
           pages.setDefaultBaseUrl(pagesField.getDefaultBaseUrl());
           pagesField.setValue(testCase, pages);
       }
    }

}
