package net.thucydides.junit.runners;

import java.util.List;

import org.openqa.selenium.WebDriver;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.junit.internals.ManagedWebDriverAnnotatedField;
import net.thucydides.junit.internals.PagesAnnotatedField;
import net.thucydides.junit.internals.StepsAnnotatedField;
import net.thucydides.junit.steps.StepFactory;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class TestCaseAnnotations {
    
    private TestCaseAnnotations() {}
    
    
    public static void checkThatTestCaseIsCorrectlyAnnotated(final  Class<?> testCase) {
        checkThatManagedFieldIsDefinedIn(testCase);
        checkThatStepsFieldIsDefinedIn(testCase);
        checkThatPagesFieldIsDefinedIn(testCase);
    }
    /**
     * There must be a WebDriver field in the test case annotated with the Managed annotation.
     */
    private static void checkThatManagedFieldIsDefinedIn(final  Class<?> testCase) {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testCase);
    }

    /**
     * There must be a ScenarioSteps field in the test case annotated with the Steps annotation.
     */
    private static void checkThatStepsFieldIsDefinedIn(final  Class<?> testCase) {
        StepsAnnotatedField.findMandatoryAnnotatedFields(testCase);
    }

    /**
     * There must be a Pages field in the test case annotated with the ManagedPages annotation.
     */
    private static void checkThatPagesFieldIsDefinedIn(final  Class<?> testCase) {
        PagesAnnotatedField.findFirstAnnotatedField(testCase);
    }

    
    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    public static void injectDriverInto(final Object testCase, final WebDriver driver) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        webDriverField.setValue(testCase, driver);
    }

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
            List<StepsAnnotatedField> stepsFields) {
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
