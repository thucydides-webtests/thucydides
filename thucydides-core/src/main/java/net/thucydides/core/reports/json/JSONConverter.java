package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

import java.io.*;
import java.nio.file.Path;

public interface JSONConverter {
    TestOutcome fromJson(InputStream inputStream) throws IOException;
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
