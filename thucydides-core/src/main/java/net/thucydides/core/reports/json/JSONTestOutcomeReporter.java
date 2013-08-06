package net.thucydides.core.reports.json;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class JSONTestOutcomeReporter implements AcceptanceTestReporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JSONTestOutcomeReporter.class);

    private File outputDirectory;

    private transient String qualifier;

    private Gson gson;

    public JSONTestOutcomeReporter() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(TestOutcome.class, new TestOutcomeSerializer());
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        builder.registerTypeAdapter(Throwable.class, new ThrowableClassAdapter());
        gson = builder.create();
    }

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public File generateReportFor(TestOutcome testOutcome,
                                  TestOutcomes allTestOutcomes) throws IOException {
        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);
        Preconditions.checkNotNull(outputDirectory);
        String json = gson.toJson(storedTestOutcome);
        String reportFilename = reportFor(storedTestOutcome);
        File report = new File(getOutputDirectory(), reportFilename);
        OutputStream outputStream = new FileOutputStream(report);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
        writer.write(json);
        writer.flush();
        writer.close();
        outputStream.close();
        return report;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(
                ReportType.JSON);
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public void setResourceDirectory(String resourceDirectoryPath) {
    }

    public Optional<TestOutcome> loadReportFrom(final File reportFile)
            throws IOException {
        try {
            String jsonString = Files.toString(reportFile, Charset.forName("UTF-8"));
            TestOutcome fromJson = gson.fromJson(jsonString, TestOutcome.class);
            return Optional.of(fromJson);
        } catch (Exception e) {
            LOGGER.error("Cannot load class ", e);
            return Optional.absent();
        }
    }
}