package net.thucydides.core.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;

public class WhenReadingResourcesFromTheClasspath {

    @Test
    public void should_return_a_list_of_resources_on_the_classpage() {
        Pattern pattern = Pattern.compile(".*");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.isEmpty(), is(false));
    }

    @Test
    public void should_return_a_list_of_resources_in_a_given_package() {
        Pattern pattern = Pattern.compile(".*/resourcelist/.*");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.size(), is(2));
        assertThat(resources, hasItems(endsWith("resourcelist/sample.css"),endsWith("resourcelist/sample.xsl")));
    }
    
    @Test
    public void should_return_a_list_of_resources_in_a_given_package_even_from_a_dependency() {
        Pattern pattern = Pattern.compile(".*/findElement.js");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.isEmpty(), is(false));
    }
    
}
