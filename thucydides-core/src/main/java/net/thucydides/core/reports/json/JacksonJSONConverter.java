package net.thucydides.core.reports.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.jackson.TestOutcomeModule;

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

    public JacksonJSONConverter() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new TestOutcomeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * For testing purposes.
     */
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public TestOutcome fromJson(File jsonTestOutcome) throws IOException {
        try(Reader reader = newBufferedReader(jsonTestOutcome.toPath(), Charset.defaultCharset())) {
            return mapper.readValue(reader, TestOutcome.class);
        }
    }

    @Override
    public void writeJsonToFile(TestOutcome testOutcome, Path report) throws IOException {
        try (Writer writer =  newBufferedWriter(report, Charset.defaultCharset())) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, testOutcome);
        }
    }
}
