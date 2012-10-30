package net.thucydides.core.steps;

import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The Step Index class is a way to allow the dynamic discovery of what test steps are available.
 * This assumes you are writing your Page Objects and ScenarioStep classes in a separate artifact,
 * which unit tests can then invoke. This is the most flexible way of implementing the page objects
 * and steps.
 * 
 * To use this class, override it and add a public array of classes containing the list of scenario step
 * classes your test API provides. Then annotate this field using the '@StepProvider' annotation,
 * as shown here:
 * <pre><code>
    public class MyWebSiteStepIndex extends StepIndex {

        @StepProvider
        public Class<?>[] stepClasses = {AddWidgetScenarioSteps.class, SearchWidgetsScenarioSteps.class};

    }
 * </code></pre>
 * 
 * Users can then discover the scenario classes as shown here:
 * <pre><code>
 *      List<Method> stepMethods = index.getStepsFor(scenarioClass);
 * </code></pre>
 * 
 * They can also discover the scenario step methods as shown here:
 * <pre><code>
 *      StepIndex index = new MyWebSiteStepIndex();
        List stepClasses = index.getStepClasses();
 * </code></pre>
 * 
 * @author johnsmart
 *
 */
public abstract class StepIndex {

    public List<Class<? extends ScenarioSteps>> getStepClasses() {
        Field stepProviderField = getStepProviderField();
        List<Class<? extends ScenarioSteps>> stepProviders;
        try {
            Class<?>[] providerFieldValue = (Class<?>[]) stepProviderField.get(this);
            stepProviders = getStepProvidersFrom(providerFieldValue);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No step provider field found.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("No step provider field found.", e);
        }
        return stepProviders;
    }

    public List<Method> getStepsFor(final Class<? extends ScenarioSteps> scenarioClass) {
        List<Method> steps = new ArrayList<Method>();
        Method[] methods = scenarioClass.getMethods();
        for(Method method : methods) {
            if (method.isAnnotationPresent(Step.class)) {
                steps.add(method);
            }
        }
        return steps;
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends ScenarioSteps>> getStepProvidersFrom(final Class<?>[] providerFieldValue) {
        List<Class<? extends ScenarioSteps>> stepProviders = new ArrayList<Class<? extends ScenarioSteps>>();
        for(Class<?> providerClass : providerFieldValue) {
            if (ScenarioSteps.class.isAssignableFrom(providerClass)) {
                stepProviders.add((Class<? extends ScenarioSteps>) providerClass);
            } else {
                throw new IllegalArgumentException(providerClass + " needs to extend ScenarioSteps");
            }
                 
        }
        return stepProviders;
    }

    private Field getStepProviderField() {
        for (Field field : Fields.of(this.getClass()).allFields()) {
            if (field.isAnnotationPresent(StepProvider.class)) {
                return field;
            }
        }
        throw new IllegalArgumentException("No step provider field found.");
    }

}
