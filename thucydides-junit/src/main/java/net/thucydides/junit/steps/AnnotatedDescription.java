package net.thucydides.junit.steps;

import static net.thucydides.core.util.NameConverter.humanize;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.StepDescription;
import net.thucydides.junit.annotations.TestsRequirement;
import net.thucydides.junit.annotations.TestsRequirements;
import net.thucydides.junit.annotations.Title;

import org.junit.runner.Description;

/**
 *
 */
public class AnnotatedDescription {
    
    private final Description description;

    public AnnotatedDescription(final Description description) {
        this.description = description;
    }

    public List<String> getAnnotatedRequirements() {
        List<String> requirements = new ArrayList<String>();
        try {
            Method testMethod = getTestMethod();
            addRequirementFrom(requirements, testMethod);
            addMultipleRequirementsFrom(requirements, testMethod);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return requirements;
    }
    
    private void addMultipleRequirementsFrom(final List<String> requirements,final Method testMethod) {
        TestsRequirements testRequirements = (TestsRequirements) testMethod.getAnnotation(TestsRequirements.class);
        if (testRequirements != null) {
            for(String requirement : testRequirements.value()) {
                requirements.add(requirement);
            }
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
            StepDescription stepDescription = (StepDescription) testMethod
                    .getAnnotation(StepDescription.class);
            if (stepDescription != null) {
                annotatedDescription = stepDescription.value();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return annotatedDescription;
    }

    public Method getTestMethod() throws NoSuchMethodException {
        return methodCalled(withNoArguments(description.getMethodName()), getTestClass());
    }


    private String withNoArguments(final String methodName) {
        int firstSpace = methodName.indexOf(":");
        if (firstSpace > 0) {
            return methodName.substring(0, firstSpace);
        }
        return methodName;
    }

    private Class<?> getTestClass() {
        return description.getTestClass();
    }

    private Method methodCalled(final String methodName, final Class<?> testClass) {
        Method[] methods = testClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
    }

    public String getAnnotatedTitle() {
        try {
            Method testMethod = getTestMethod();
            Title title = (Title) testMethod.getAnnotation(Title.class);
            if (title != null) {
                return title.value();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getName() {
        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        String annotatedDescription = testDescription.getAnnotatedDescription();
        if (annotatedDescription != null) {
            return annotatedDescription;
        }
        return getHumanizedTestName();
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
            String testMethodName = description.getMethodName();
            return humanize(testMethodName);
        }
    }


    /**
     * Turns a classname into a human-readable title.
     */
    private String getHumanizedTestName() {

        String testName = description.getMethodName();
        String humanizedName = humanize(testName);
        if (!humanizedName.endsWith(".")) {
            humanizedName = humanizedName + ".";
        }
        return humanizedName;
    }    
}
