package net.thucydides.core.annotations;


import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WhenReadingTestAndStepAnnotations {

    static final class TestSteps {
        public void normalStep(){}

        @Title("A title")
        public void normalStepWithTitle(){}

        @Pending
        public void pendingStep(){}

        @Ignore
        public void skippedStep() {}
    }

    @Test
    public void shouldReadStepTitles() {
        assertThat(TestAnnotations.forClass(TestSteps.class)
                    .getAnnotatedTitleForMethod("normalStepWithTitle"), is("A title"));
    }

    @Test
    public void shouldIdentifyPendingSteps() {
        assertThat(TestAnnotations.forClass(TestSteps.class).isPending("pendingStep"), is(true));
    }

    @Test
    public void shouldIdentifyNonPendingSteps() {
        assertThat(TestAnnotations.forClass(TestSteps.class).isPending("normalStep"), is(false));
    }

    @Test
    public void shouldIdentifySkippedSteps() {
        assertThat(TestAnnotations.forClass(TestSteps.class).isIgnored("skippedStep"), is(true));
    }

    @Test
    public void shouldIdentifyNonSkippedSteps() {
        assertThat(TestAnnotations.forClass(TestSteps.class).isIgnored("normalStep"), is(false));
    }
}
