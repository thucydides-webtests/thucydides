package net.thucydides.core.reports.json;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

public class GsonJSONConverter implements JSONConverter {

    private Gson gson;

    public GsonJSONConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(TestOutcome.class, new TestOutcomeSerializer());
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        builder.registerTypeAdapter(Throwable.class, new ThrowableClassAdapter());
        gson = builder.create();
    }

    @Override
    public void writeJsonToFile(TestOutcome testOutcome, Path report) throws IOException {
        try(JsonWriter jsonWriter = new JsonWriter(newBufferedWriter(report, Charset.defaultCharset()))) {
            gson.toJson(testOutcome, TypeToken.of(TestOutcome.class).getType(), jsonWriter);
        }
    }

    @Override
    public TestOutcome fromJson(File jsonTestOutcome) throws IOException {
        try(Reader reader = newBufferedReader(jsonTestOutcome.toPath(), Charset.defaultCharset())) {
            return gson.fromJson(reader, TestOutcome.class);
        }
    }
}
