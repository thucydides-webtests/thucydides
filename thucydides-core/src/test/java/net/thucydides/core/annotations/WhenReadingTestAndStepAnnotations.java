package net.thucydides.core.annotations;


import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WhenReadingTestAndStepAnnotations {

    static final class SampleTestCase {
        public void normalTest(){}

        @Title("A title")
        public void normalTestWithTitle(){}

        @Pending
        public void pendingTest(){}

        @Ignore
        public void skippedTest() {}

        @Title("Fixes #MYPROJECT-123 and #MYPROJECT-456")
        public void testWithIssues(){}

    }

    @Test
    public void shouldReadMethodTitles() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                    .getAnnotatedTitleForMethod("normalTestWithTitle"), is("A title"));
    }

    @Test
    public void shouldReadNoAnnotatedIssuesIfNoneFound() {

        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                .getAnnotatedIssuesForMethodTitle("normalTest").isEmpty(), is(true));
    }

    @Test
    public void shouldReadAnnotatedIssues() {

        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                .getAnnotatedIssuesForMethodTitle("testWithIssues"), allOf(hasItem("#MYPROJECT-123"),hasItem("#MYPROJECT-456")));
    }

    @Test
    public void shouldIdentifyPendingSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isPending("pendingTest"), is(true));
    }

    @Test
    public void shouldIdentifyNonPendingSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isPending("normalTest"), is(false));
    }

    @Test
    public void shouldIdentifySkippedSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isIgnored("skippedTest"), is(true));
    }

    @Test
    public void shouldIdentifyNonSkippedSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isIgnored("normalTest"), is(false));
    }

}
