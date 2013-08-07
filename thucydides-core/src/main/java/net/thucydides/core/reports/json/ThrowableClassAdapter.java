package net.thucydides.core.reports.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

class ThrowableClassAdapter implements JsonSerializer<Throwable> {

    @Override
    public JsonElement serialize(Throwable src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("class", new JsonPrimitive(src.getClass().getName()));
        if (src.getMessage() != null) {
            jsonObject.add("message", new JsonPrimitive(src.getMessage()));
        }
        return jsonObject;
    }
}