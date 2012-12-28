package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class DataTableRow {
    private final List<String> cellValues;
    private TestResult result;

    public DataTableRow(List<String> cellValues) {
        this.cellValues = ImmutableList.copyOf(cellValues);
        this.result = TestResult.UNDEFINED;
    }

    public List<String> getValues() {
        return ImmutableList.copyOf(cellValues);
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public void updateResult(TestResult newResult) {
        if (newResult == TestResult.UNDEFINED) {
            setResult(newResult);
        } else {
            TestResultList testResults = TestResultList.of(this.result, newResult);
            setResult(testResults.getOverallResult());
        }
    }
}
