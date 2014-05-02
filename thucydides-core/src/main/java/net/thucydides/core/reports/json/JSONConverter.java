package net.thucydides.core.reports.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.model.TestOutcome;

public class JSONConverter {

    private Gson gson;

    public JSONConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(TestOutcome.class, new TestOutcomeSerializer());
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        builder.registerTypeAdapter(Throwable.class, new ThrowableClassAdapter());
        gson = builder.create();
    }

    public String toJson(TestOutcome testOutcome) {
        return gson.toJson(testOutcome);
    }

    public TestOutcome fromJson(String jsonString) {
        return gson.fromJson(jsonString, TestOutcome.class);
    }
}
