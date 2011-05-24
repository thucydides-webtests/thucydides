package net.thucydides.core.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.TestsRequirements;
import net.thucydides.core.annotations.Title;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.util.NameConverter.humanize;

/**
 *  Test steps and step groups can be described by various annotations.
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
        Method testMethod = getTestMethod();
        addRequirementFrom(requirements, testMethod);
        addMultipleRequirementsFrom(requirements, testMethod);
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
        Method methodFound = null;

        if (testClass != null) {
            Method[] methods = testClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    methodFound = method;
                }
            }
        }
        if (methodFound == null) {
            throw new IllegalArgumentException("No test method called " + methodName + " was found in " + testClass);
        }
        return methodFound;
    }

    public String getAnnotatedTitle() {

        Method testMethod = getTestMethod();
        Title title = (Title) testMethod.getAnnotation(Title.class);
        if (title != null) {
            return title.value();
        }
        return null;
    }

    private String getAnnotatedStepName() {
        return getNameFromStepAnnotationIn(getTestMethod());
    }

    private String getNameFromStepAnnotationIn(Method testMethod) {
        Step step = (Step) testMethod.getAnnotation(Step.class);

        if ((step != null) && (!StringUtils.isEmpty(step.value()))) {
            return step.value();
        }
        return null;
    }

    public String getName() {
        if (noClassIsDefined()) {
          return description.getName();
        } else if (isAGroup()) {
            return groupName();
        } else {
            return stepName();
        }
    }

    private boolean noClassIsDefined() {
        return description.getStepClass() == null;
    }

    private String groupName() {
        String annotatedGroupName = getGroupName();
        if (!StringUtils.isEmpty(annotatedGroupName)) {
            return annotatedGroupName;
        } else {
            return stepName();
        }
    }

    private String stepName() {
        String annotationTitle = getAnnotatedTitle();
        if (!StringUtils.isEmpty(annotationTitle)) {
            return annotationTitle;
        }

        String annotatedStepName = getAnnotatedStepName();
        if (!StringUtils.isEmpty(annotatedStepName)) {
            return annotatedStepName;
        }

        return humanize(description.getName());
    }

    public boolean isAGroup() {

        Method testMethod = getTestMethod();
        StepGroup testGroup = (StepGroup) testMethod.getAnnotation(StepGroup.class);
        return (testGroup != null);
    }

    private String getGroupName() {
        Method testMethod = getTestMethod();
        StepGroup testGroup = (StepGroup) testMethod.getAnnotation(StepGroup.class);
        return testGroup.value();
    }
}
