package net.thucydides.core.output;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import net.thucydides.core.matchers.SimpleValueMatcher;

import java.io.IOException;
import java.util.List;

public interface ResultsOutput {
    void recordResult(SimpleValueMatcher check, List<String> columnValues) throws IOException, WriteException, BiffException;
}
