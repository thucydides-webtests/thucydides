package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.json.jackson.TestOutcomeModule;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

public class JacksonJSONConverter implements JSONConverter {

    private final ObjectMapper mapper;
    private final EnvironmentVariables environmentVariables;

    @Inject
    public JacksonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new TestOutcomeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    /**
     * For testing purposes.
     */
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public TestOutcome fromJson(File jsonTestOutcome) throws IOException {
        try(Reader reader = newBufferedReader(jsonTestOutcome.toPath(), getCharset())) {
            return mapper.readValue(reader, TestOutcome.class);
        }
    }

    @Override
    public void writeJsonToFile(TestOutcome testOutcome, Path report) throws IOException {
        try (Writer writer =  newBufferedWriter(report, getCharset())) {
            if (usePrettyPrinting()) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, testOutcome);
            } else {
                mapper.writeValue(writer, testOutcome);
                }
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }

    private Charset getCharset() {
        String charsetName = ThucydidesSystemProperty.JSON_CHARSET.from(environmentVariables,"UTF-8");
        return Charset.forName(charsetName);
    }
}
