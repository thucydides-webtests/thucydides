package net.thucydides.core.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepDescription;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.TestsRequirements;
import net.thucydides.core.annotations.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.util.NameConverter.humanize;

/**
 *
 */
public final class AnnotatedDescription {

    private final ExecutedStepDescription description;

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedDescription.class);

    public static AnnotatedDescription from(final ExecutedStepDescription description) {
        return new AnnotatedDescription(description);

    }
    private AnnotatedDescription(final ExecutedStepDescription description) {
        this.description = description;
    }

    public List<String> getAnnotatedRequirements() {
        List<String> requirements = new ArrayList<String>();
        try {
            Method testMethod = getTestMethod();
            if (testMethod != null) {
                addRequirementFrom(requirements, testMethod);
                addMultipleRequirementsFrom(requirements, testMethod);
            }
        } catch (SecurityException e) {
            LOGGER.error("Could not access requirements annotation", e);
        }
        return requirements;
    }

    private void addMultipleRequirementsFrom(final List<String> requirements, final Method testMethod) {
        TestsRequirements testRequirements = (TestsRequirements) testMethod.getAnnotation(TestsRequirements.class);
        if (testRequirements != null) {
            requirements.addAll(Arrays.asList(testRequirements.value()));
        }
    }

    private void addRequirementFrom(final List<String> requirements, final Method testMethod) {
        TestsRequirement testsRequirement = (TestsRequirement) testMethod
                .getAnnotation(TestsRequirement.class);
        if (testsRequirement != null) {
            requirements.add(testsRequirement.value());
        }
    }

    public String getAnnotatedDescription() {
        String annotatedDescription = null;
        try {
            Method testMethod = getTestMethod();
            annotatedDescription = getNameFromTestDescriptionAnnotation(testMethod);
        } catch (SecurityException e) {
            LOGGER.error("Could not access description annotation", e);
        }
        return annotatedDescription;
    }

    private String getNameFromTestDescriptionAnnotation(final Method testMethod) {
        StepDescription stepDescription = (StepDescription) testMethod
                .getAnnotation(StepDescription.class);
        String annotatedDescription = null;
        if (stepDescription != null) {
            annotatedDescription = stepDescription.value();
        }
        return annotatedDescription;
    }

    public Method getTestMethod() {
        return methodCalled(withNoArguments(description.getName()), getTestClass());
    }


    private String withNoArguments(final String methodName) {
        int firstSpace = methodName.indexOf(':');
        if (firstSpace > 0) {
            return methodName.substring(0, firstSpace);
        }
        return methodName;
    }

    private Class<?> getTestClass() {
        return description.getStepClass();
    }

    private Method methodCalled(final String methodName, final Class<?> testClass) {
        if (testClass != null) {
            Method[] methods = testClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
        }
        return null;
    }

    public String getAnnotatedTitle() {

        Method testMethod = getTestMethod();
        Title title = (Title) testMethod.getAnnotation(Title.class);
        if (title != null) {
            return title.value();
        }
        return null;
    }

    public String getAnnotatedStepName() {
        Method testMethod = getTestMethod();
        Step step = (Step) testMethod.getAnnotation(Step.class);
        if ((step != null) && (step.value().length() > 0)) {
            return step.value();
        }
        return null;
    }

    public String getName() {
        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        String annotatedTestName = getAnnotatedStepName();
        String annotatedDescription = testDescription.getAnnotatedDescription();
        if (annotatedTestName != null) {
            return annotatedTestName;
        } else if (annotatedDescription != null) {
            return annotatedDescription;
        } else {
            return getHumanizedTestName();
        }
    }

    /**
     * Turns a method into a human-readable title.
     */
    public String getTitle() {

        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        String annotationTitle = testDescription.getAnnotatedTitle();
        if (annotationTitle != null) {
            return humanize(annotationTitle);
        } else {
            String testMethodName = description.getName();
            return humanize(testMethodName);
        }
    }


    /**
     * Turns a classname into a human-readable title.
     */
    private String getHumanizedTestName() {
        String testName = description.getName();
        return humanize(testName);
    }

    public boolean isAGroup() {

        Method testMethod = getTestMethod();
        StepGroup testGroup = (StepGroup) testMethod.getAnnotation(StepGroup.class);
        if (testGroup != null) {
            return true;
        }
        return false;
    }

    public String getGroupName() {
        Method testMethod = getTestMethod();
        StepGroup testGroup = (StepGroup) testMethod.getAnnotation(StepGroup.class);
        if (testGroup != null) {
            return testGroup.value();
        }
        return null;
    }
}
