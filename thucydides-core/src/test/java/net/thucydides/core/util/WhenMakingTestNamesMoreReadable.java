package net.thucydides.core.util;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;

public class WhenMakingTestNamesMoreReadable {
    
    @Test
    public void camel_cased_test_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("ATestClassName"), is("A test class name"));
    }
    
    @Test
    public void already_readable_titles_should_not_be_modified() {
        assertThat(NameConverter.humanize("This is a COOL test"), is("This is a COOL test"));
    }

    @Test
    public void test_names_with_parameters_should_only_modify_the_name() {
        assertThat(NameConverter.humanize("aTestMethod: ABC def bGd"), is("A test method: ABC def bGd"));
    }

    @Test
    public void camelCase_method_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("aTestMethod"), is("A test method"));
    }

    @Test
    public void underscored_test_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("a_test_method"), is("A test method"));
    }

    @Test
    public void human_test_names_should_be_converted_to_underscore_filenames() {
        assertThat(NameConverter.underscore("A test method"), is("a_test_method"));
    }
}
