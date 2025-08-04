package main.java.manager;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString()); // например "PT1H30M"
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return Duration.parse(json.getAsString());
    }
}