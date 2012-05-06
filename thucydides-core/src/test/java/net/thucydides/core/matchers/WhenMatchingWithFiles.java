package net.thucydides.core.matchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static net.thucydides.core.matchers.FileMatchers.exists;
import static net.thucydides.core.util.FileSeparatorUtil.changeSeparatorIfRequired;
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

public class WhenMatchingWithFiles {

    @Test
    public void should_check_if_file_exists() {
        File existingFile = new File(directoryInClasspathCalled("/reports"), "sample-report-1.xml");
        assertThat(existingFile, exists());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_check_if_file_does_not_exist() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(allOf(containsString("Expected: a file at"), containsString(changeSeparatorIfRequired("reports/no-such-report.xml"))));
        
        File existingFile = new File(directoryInClasspathCalled("/reports"), "no-such-report.xml");
        assertThat(existingFile, exists());
 
    }
}
