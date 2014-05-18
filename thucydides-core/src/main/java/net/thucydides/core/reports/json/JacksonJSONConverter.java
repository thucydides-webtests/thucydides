package net.thucydides.core.reports.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.model.TestOutcome;

import java.io.IOException;

public class JacksonJSONConverter implements JSONConverter {

    private final ObjectMapper mapper;

    public JacksonJSONConverter() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new TestOutcomeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    }

    @Override
    public String toJson(TestOutcome testOutcome) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testOutcome);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert test outcome to JSON", e);
        }
    }

    @Override
    public TestOutcome fromJson(String jsonString) {
        try {
            return mapper.readValue(jsonString, TestOutcome.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert test outcome from JSON", e);
        }
    }
}
