package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * A description goes here.
 * User: john
 * Date: 12/05/2014
 * Time: 5:10 PM
 */
public interface JSONConverter {
    TestOutcome fromJson(File jsonTestOutcome) throws IOException;
    void writeJsonToFile(TestOutcome storedTestOutcome, Path report) throws IOException;
}
