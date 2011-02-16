package net.thucydides.junit.runners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.thucydides.junit.annotations.Step;

import org.junit.runners.model.FrameworkMethod;

/**
 * A utility class used to sort test methods.
 * Test methods in an acceptance test should be executed in a predicatable order.
 * Since Java does not support reliable ordering of methods in the class metadata,
 * we need to do somtehing else. Test methods can be ordered either using the Step
 * annotation, or, failing that, in alphabetical order.
 * 
 * @author johnsmart
 *
 */
public final class OrderedTestSteps {

    private OrderedTestSteps() {
    }
    
    /**
     * Sort a list of JUnit tests either using the Test annotation, or in alphabetical order.
     */
    public static List<FrameworkMethod> sort(final List<FrameworkMethod> unorderedTests) {
        // TODO : Rewrite all this cleanly
        List<OrderedTestStepMethod> orderedTests = new ArrayList<OrderedTestStepMethod>();
        for(FrameworkMethod testMethod : unorderedTests) {
            OrderedTestStepMethod orderedTest = null;
            Step step = testMethod.getAnnotation(Step.class);
            if (step != null) {
                orderedTest = new OrderedTestStepMethod(testMethod,step.value());
            } else {
                orderedTest = new OrderedTestStepMethod(testMethod,0);
            }
            orderedTests.add(orderedTest);
        }
        return orderedFrameworkMethodsIn(orderedTests);
    }

    private static List<FrameworkMethod> orderedFrameworkMethodsIn(final List<OrderedTestStepMethod> orderedTests) {
        Collections.sort(orderedTests);
        
        List<FrameworkMethod> orderedFramework = new ArrayList<FrameworkMethod>();
        
        for(OrderedTestStepMethod orderedMethod : orderedTests) {
            orderedFramework.add(orderedMethod.getFrameworkMethod());
        }
        
        return orderedFramework;
    }
        
    
}
