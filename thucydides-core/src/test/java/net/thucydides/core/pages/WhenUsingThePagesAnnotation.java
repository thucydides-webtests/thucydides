package net.thucydides.core.pages;


import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;
import net.thucydides.core.steps.PagesAnnotatedField;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenUsingThePagesAnnotation {

    @Test
    public void the_ManagedPages_annotation_should_identify_the_pages_field() {
        SimpleScenario testCase = new SimpleScenario();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
        assertThat(pagesField, is(not(nullValue())));
    }

    @Test
    public void the_ManagedPages_annotation_should_define_the_default_url() {
        SimpleScenario testCase = new SimpleScenario();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
        assertThat(pagesField.getDefaultBaseUrl(), is("http://www.google.com"));
    }


    @Test
    public void should_be_able_to_inject_a_Pages_object_into_a_test_case() {
        SimpleScenario testCase = new SimpleScenario();
        Pages pages = new Pages();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());

        pagesField.setValue(testCase, pages);
        assertThat(testCase.getPages(), is(pages));
    }

    class SimpleUnannotatedScenario {
        public Pages pages;
    }

    @Test(expected = InvalidManagedPagesFieldException.class)
    public void should_throw_exception_if_no_annotation_pages_object_found() {
        SimpleUnannotatedScenario testCase = new SimpleUnannotatedScenario();
        PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());

    }

    class SimpleBadlyannotatedScenario {
        @ManagedPages(defaultUrl = "http://www.google.com")
        public String pages;
    }

    @Test(expected = InvalidManagedPagesFieldException.class)
    public void should_throw_exception_if_pages_object_is_not_a_Pages_instance() {
        SimpleBadlyannotatedScenario testCase = new SimpleBadlyannotatedScenario();
        PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());

    }

}
