package net.thucydides.junit.runners;

import org.junit.runners.model.FrameworkMethod;

/**
 * Encapsulates a test method potentially annotated with a @Step annotation.
 * We need this to order the test methods in the class, since this can't
 * be done using standard Java reflexion.
 *
 */
public class OrderedTestStepMethod implements Comparable<OrderedTestStepMethod> {
    
    private FrameworkMethod frameworkMethod;
    
    private int order;
    
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private static final int EQUAL = 0;

    public OrderedTestStepMethod(final FrameworkMethod frameworkMethod, final int order) {
        this.frameworkMethod = frameworkMethod;
        this.order = order;
    }

    public int compareTo(final OrderedTestStepMethod that) {

        if (orderDefinedInEither(this, that)) {
            return compareOrderWith(that);
        } else {
            return compareTestNameWith(that);
        }
    }

    private int compareOrderWith(final OrderedTestStepMethod that) {
        if (this.order < that.order) {
            return BEFORE;
        }
        if (this.order > that.order) {
            return AFTER;
        }                   
        return EQUAL;
    }
    
    private int compareTestNameWith(final OrderedTestStepMethod that) {
        String thisName = this.getFrameworkMethod().getName();
        String thatName = that.getFrameworkMethod().getName();
        return thisName.compareTo(thatName);
    }

    private boolean orderDefinedInEither(final OrderedTestStepMethod thisMethod,
                                         final OrderedTestStepMethod thatMethod) {
        return ((thisMethod.order > 0) || (thatMethod.order > 0));
    }

    public FrameworkMethod getFrameworkMethod() {
        return frameworkMethod;
    }

    /**
     * Mainly used for testing.
     */
    @Override
    public String toString() {
        return frameworkMethod.getName() + " (step " + order + ")";
    }
}
