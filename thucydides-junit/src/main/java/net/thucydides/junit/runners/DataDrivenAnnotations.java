package net.thucydides.junit.runners;

import net.thucydides.core.csv.CSVTestDataSource;
import net.thucydides.core.csv.TestDataSource;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

class DataDrivenAnnotations {

    public static DataDrivenAnnotations forClass(final TestClass testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    private final TestClass testClass;

    DataDrivenAnnotations(final TestClass testClass) {
        this.testClass = testClass;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getParametersList() throws Throwable {
        return (List<Object[]>) getTestDataMethod().getMethod().invoke(null);
    }

    public FrameworkMethod getTestDataMethod() throws Exception {
        FrameworkMethod method = findTestDataMethod();
        if (method == null) {
            throw new IllegalArgumentException("No public static @TestDataSource method on class "
                    + testClass.getName());
        }
        return method;
    }

    private FrameworkMethod findTestDataMethod() {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(TestData.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }
        return null;
    }

    private String findTestDataSource() {
        return findUseTestDataFromAnnotation().value();
    }

    private UseTestDataFrom findUseTestDataFromAnnotation() {
        return testClass.getJavaClass().getAnnotation(UseTestDataFrom.class);
    }

    public boolean hasTestDataDefined() {
        return (findTestDataMethod() != null);
    }

    public boolean hasTestDataSourceDefined() {
        return (findUseTestDataFromAnnotation() != null) && (findTestDataSource() != null);
    }

    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz) throws IOException {
        TestDataSource testdata = new CSVTestDataSource(findTestDataSource(), findTestDataSeparator());
        return testdata.getDataAsInstancesOf(clazz);
    }

    private char findTestDataSeparator() {
        return findUseTestDataFromAnnotation().separator();
    }
}
