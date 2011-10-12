package net.thucydides.core.annotations;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;

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
