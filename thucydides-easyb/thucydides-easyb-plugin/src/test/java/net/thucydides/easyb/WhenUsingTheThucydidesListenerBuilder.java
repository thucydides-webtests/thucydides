package net.thucydides.easyb;


import net.thucydides.core.steps.StepListener;
import org.easyb.domain.Behavior;
import org.easyb.listener.ExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheThucydidesListenerBuilder {

    @Mock
    private StepListener stepListener;

    @Mock
    private Behavior behavior;

    private ThucydidesListenerBuilder listenerBuilder;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ThucydidesListenerBuilder.resetListener();
        listenerBuilder = new ThucydidesListenerBuilder();
    }

    @Test
    public void the_listener_builder_provides_a_thucydides_listener_instance() {
        ThucydidesListenerBuilder.setCurrentStepListener(stepListener);
        ExecutionListener listener = listenerBuilder.get();

        assertThat(listener, instanceOf(ThucydidesExecutionListener.class));

    }

    @Test(expected = IllegalStateException.class)
    public void the_current_listener_must_be_set_first() {

        ExecutionListener listener = listenerBuilder.get();

        assertThat(listener, instanceOf(ThucydidesExecutionListener.class));
    }

    @Test
    public void the_thucydides_listener_instance_is_unique_for_the_current_thread() {
        ThucydidesListenerBuilder.setCurrentStepListener(stepListener);
        ExecutionListener listener1 = listenerBuilder.get();

        ThucydidesListenerBuilder listenerBuilder2 = new ThucydidesListenerBuilder();
        ExecutionListener listener2 = listenerBuilder2.get();

        assertThat(listener1, is(listener2));

    }

}
