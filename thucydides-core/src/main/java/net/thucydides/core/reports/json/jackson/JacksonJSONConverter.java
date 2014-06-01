package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class JacksonJSONConverter implements JSONConverter {

    private final ObjectMapper mapper;
    private final ObjectReader reader;
    private final ObjectWriter writer;
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

        reader = mapper.reader(TestOutcome.class);
        writer = mapper.writerWithType(TestOutcome.class);


    }

    /**
     * For testing purposes.
     */
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public TestOutcome fromJson(File jsonTestOutcome) throws IOException {
        return reader.readValue(jsonTestOutcome);
    }

    @Override
    public void writeJsonToFile(TestOutcome testOutcome, Path report) throws IOException {
        if (usePrettyPrinting()) {
            writer.withDefaultPrettyPrinter().writeValue(report.toFile(), testOutcome);
        } else {
            writer.writeValue(report.toFile(), testOutcome);
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }
}
