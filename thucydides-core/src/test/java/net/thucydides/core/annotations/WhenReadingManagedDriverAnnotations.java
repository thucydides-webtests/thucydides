package net.thucydides.core.annotations;


import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class WhenReadingManagedDriverAnnotations {

    static final class SampleTestCase {

        public void normalTest(){}

        @Managed
        WebDriver webDriver;

    }

    static final class SampleTestCaseWithNoManagedField {

        public void normalTest(){}

    }

    @Before
    public void initMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindManagedDriverField() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCase.class);

        assertThat(managedField, is(not(nullValue())));
    }

    @Test (expected = InvalidManagedWebDriverFieldException.class)
    public void shouldRaiseExceptionIfNoManagedFieldFound() {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseWithNoManagedField.class);
    }

    static final class SampleTestCaseUsingUniqueSession {

        public void normalTest(){}

        @Managed(uniqueSession = true)
        WebDriver webDriver;

    }

    @Test
    public void shouldKnowWhenAUniqueBrowserSessionHasBeenRequested() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingUniqueSession.class);

        assertThat(managedField.isUniqueSession(), is(true));
    }
}
