package net.thucydides.easyb;

import org.easyb.listener.ExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheThucydidesListenerBuilder {

    private ThucydidesListenerBuilder listenerBuilder;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ThucydidesListenerBuilder.resetListener();
        listenerBuilder = new ThucydidesListenerBuilder();
    }

    @Test
    public void the_listener_builder_provides_a_thucydides_listener_instance() {
        ExecutionListener listener = listenerBuilder.get();

        assertThat(listener, instanceOf(ThucydidesExecutionListener.class));

    }

    @Test
    public void the_thucydides_listener_instance_is_unique_for_the_current_thread() {
        ExecutionListener listener1 = listenerBuilder.get();

        ThucydidesListenerBuilder listenerBuilder2 = new ThucydidesListenerBuilder();
        ExecutionListener listener2 = listenerBuilder2.get();

        assertThat(listener1, is(listener2));

    }

}
