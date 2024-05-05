package org.foxesworld.engine.config.abs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;

public class ConfigSerializer implements JsonSerializer<Config> {
    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : src.getCONFIG().entrySet()) {
            jsonObject.add(entry.getKey(), context.serialize(entry.getValue()));
        }
        return jsonObject;
    }
}
