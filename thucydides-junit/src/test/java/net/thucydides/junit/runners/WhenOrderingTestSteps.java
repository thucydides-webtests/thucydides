package net.thucydides.junit.runners;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.thucydides.junit.hamcrest.ThucydidesMatchers.lessThan;
import static net.thucydides.junit.hamcrest.ThucydidesMatchers.greaterThan;

public class WhenOrderingTestSteps {
    
    @Test
    public void methods_should_be_ordered_by_step_number() {
        
        FrameworkMethod step1Method = mock(FrameworkMethod.class);
        when(step1Method.getName()).thenReturn("a test");

        FrameworkMethod step2Method = mock(FrameworkMethod.class);
        when(step2Method.getName()).thenReturn("a test");
        
        OrderedTestStepMethod step1 = new OrderedTestStepMethod(step1Method, 1);
        OrderedTestStepMethod step2 = new OrderedTestStepMethod(step2Method, 2);
        
        assertThat(step1, lessThan(step2));
        assertThat(step2, greaterThan(step1));
    }
    
    @Test
    public void if_no_order_is_defined_methods_should_be_in_alphabetical_order() {
        
        FrameworkMethod step1Method = mock(FrameworkMethod.class);
        when(step1Method.getName()).thenReturn("Step 1 - do this");

        FrameworkMethod step2Method = mock(FrameworkMethod.class);
        when(step2Method.getName()).thenReturn("Step 2 : do that");
        
        OrderedTestStepMethod step1 = new OrderedTestStepMethod(step1Method, 1);
        OrderedTestStepMethod step2 = new OrderedTestStepMethod(step2Method, 2);
        
        assertThat(step1, lessThan(step2));
        assertThat(step2, greaterThan(step1));
    }
    
}
