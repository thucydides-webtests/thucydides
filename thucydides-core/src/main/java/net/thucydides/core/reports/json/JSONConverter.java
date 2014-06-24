package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

import java.io.*;
import java.nio.file.Path;

/**
 * A description goes here.
 * User: john
 * Date: 12/05/2014
 * Time: 5:10 PM
 */
public interface JSONConverter {
    TestOutcome fromJson(InputStream inputStream) throws IOException;
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
