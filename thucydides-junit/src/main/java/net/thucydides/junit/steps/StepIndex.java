package net.thucydides.junit.steps;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.StepProvider;
import net.thucydides.junit.annotations.Step;

public abstract class StepIndex {

    public List<Class<? extends ScenarioSteps>> getStepClasses() {
        Field stepProviderField = getStepProviderField();
        List<Class<? extends ScenarioSteps>> stepProviders = new ArrayList<Class<? extends ScenarioSteps>>();
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
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(StepProvider.class)) {
                return field;
            }
        }
        throw new IllegalArgumentException("No step provider field found.");
    }

}
