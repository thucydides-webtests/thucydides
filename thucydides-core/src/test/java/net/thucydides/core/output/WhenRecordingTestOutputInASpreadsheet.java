package net.thucydides.core.output;

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static net.thucydides.core.matchers.BeanMatchers.checkThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRecordingTestOutputInASpreadsheet {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    
    @Test
    public void should_record_a_test_result_in_a_designated_excel_spreadsheet() throws Exception {

        File outputDir = temporaryFolder.newFolder();
        File outputFile = new File(outputDir, "testresults.xls");
        ResultsOutput output = new SpreadsheetResultsOutput(outputFile, ImmutableList.of("A","B","C"));

        String expectedValue = "$10";
        String actualValue1 = "$10";
        String actualValue2 = "$11";

        output.recordResult(checkThat(expectedValue, is(actualValue1)),
                ImmutableList.of("a","b","c"));

        output.recordResult(checkThat(expectedValue, is(actualValue2)),
                ImmutableList.of("d","e","f"));

        assertThat(outputFile.exists(), is(true));
    }   
}
