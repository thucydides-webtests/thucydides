package net.thucydides.core.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class DataTableRow {
    private final List<? extends Object> cellValues;
    private TestResult result;

    public DataTableRow(List<? extends Object> cellValues) {
        this.cellValues = ImmutableList.copyOf(cellValues);
        this.result = TestResult.UNDEFINED;
    }

    public List<? extends Object> getValues() {
        return ImmutableList.copyOf(cellValues);
    }

    public List<String> getStringValues() {

        return Lists.transform(cellValues, new Function<Object, String>() {
            @Override
            public String apply(Object o) {
                return o== null ? "" : o.toString();
            }
        });
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
