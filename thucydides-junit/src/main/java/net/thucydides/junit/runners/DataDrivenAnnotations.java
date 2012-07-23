package net.thucydides.junit.runners;

import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.csv.CSVTestDataSource;
import net.thucydides.core.csv.TestDataSource;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class DataDrivenAnnotations {

    private final EnvironmentVariables environmentVariables;

    private final Pattern DATASOURCE_PATH_SEPARATORS = Pattern.compile("[:;,]");

    public static DataDrivenAnnotations forClass(final Class testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    public static DataDrivenAnnotations forClass(final TestClass testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    private final TestClass testClass;

    DataDrivenAnnotations(final Class testClass) {
        this(new TestClass(testClass));
    }

    DataDrivenAnnotations(final TestClass testClass) {
        this(testClass, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    DataDrivenAnnotations(final TestClass testClass, EnvironmentVariables environmentVariables) {
        this.testClass = testClass;
        this.environmentVariables = environmentVariables;
    }

    DataDrivenAnnotations usingEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new DataDrivenAnnotations(this.testClass, environmentVariables);
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

    @SuppressWarnings("MalformedRegex")
    private String findTestDataSource() {
        String paths = findTestDataSourcePaths();
        Iterator pathElements = Splitter.on(DATASOURCE_PATH_SEPARATORS).split(paths).iterator();
        while(pathElements.hasNext()) {
            String path = (String) pathElements.next();
            if (CSVTestDataSource.validTestDataPath(path)) {
                return path;
            }
        }
        throw new IllegalArgumentException("No test data file found for path: " + paths);

    }

    private boolean fileExistsFor(String path) {
        return new File(path).exists();
    }

    private String findTestDataSourcePaths() {
        String testDataSource = findUseTestDataFromAnnotation().value();
        String homeDir = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");
        String dataDir = ThucydidesSystemProperty.DATA_DIRECTORY.from(environmentVariables);
        testDataSource = StringUtils.replace(testDataSource, "$HOME", homeDir);
        testDataSource = StringUtils.replace(testDataSource, "${HOME}", homeDir);
        testDataSource = StringUtils.replace(testDataSource, "$USERDIR", userDir);
        testDataSource = StringUtils.replace(testDataSource, "${USERDIR}", userDir);
        if (StringUtils.isNotEmpty(dataDir)) {
            testDataSource = StringUtils.replace(testDataSource, "$DATADIR", dataDir);
            testDataSource = StringUtils.replace(testDataSource, "${DATADIR}", dataDir);
        }

        return testDataSource;
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
    
    public int countDataEntries() throws IOException {
        TestDataSource testdata = new CSVTestDataSource(findTestDataSource(), findTestDataSeparator());
        return testdata.getData().size();
    }

    private char findTestDataSeparator() {
        return findUseTestDataFromAnnotation().separator();
    }

    public boolean hasTestSpecificTestDataDefined() {
        throw new UnsupportedOperationException("Not implemented yet");

    }
}
