package net.thucydides.ant.util;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class WhenPreparingResourcePaths {

    PathProcessor pathProcessor = new PathProcessor();

    @Test
    public void shouldNotModifyUnprefixedPaths() {
        assertThat(pathProcessor.normalize("some/path")).isEqualTo("some/path");
    }

    @Test
    public void shouldConvertClasspathResourceToRealPath() {
        assertThat(pathProcessor.normalize("classpath:test-outcomes")).matches(".*target[/\\\\]test-classes[/\\\\]test-outcomes");
    }
}
