package com.github.frosxt.jsonconfig.gson;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Map;

public class GsonAdapter {

    private GsonAdapter() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static JsonNode toJConfig(final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return JsonNull.INSTANCE;
        }

        if (element.isJsonPrimitive()) {
            final JsonPrimitive p = element.getAsJsonPrimitive();
            if (p.isBoolean()) {
                return p.getAsBoolean() ? JsonBoolean.TRUE : JsonBoolean.FALSE;
            } else if (p.isNumber()) {
                return new JsonNumber(p.getAsNumber());
            } else {
                return new JsonString(p.getAsString());
            }
        }

        if (element.isJsonArray()) {
            final JsonArray arr = new JsonArray();
            for (final JsonElement child : element.getAsJsonArray()) {
                arr.add(toJConfig(child));
            }
            return arr;
        }

        if (element.isJsonObject()) {
            final JsonObject obj = new JsonObject();
            for (final Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                obj.add(entry.getKey(), toJConfig(entry.getValue()));
            }
            return obj;
        }

        throw new IllegalArgumentException("Unknown GSON element: " + element.getClass().getName());
    }

    public static JsonElement toGson(final JsonNode node) {
        switch (node) {
            case null -> {
                return com.google.gson.JsonNull.INSTANCE;
            }
            case final JsonNull jsonNull -> {
                return com.google.gson.JsonNull.INSTANCE;
            }
            case final JsonBoolean jsonBoolean -> {
                return new JsonPrimitive(jsonBoolean.getValue());
            }
            case final JsonString jsonString -> {
                return new JsonPrimitive(jsonString.getValue());
            }
            case final JsonNumber jsonNumber -> {
                return new JsonPrimitive(jsonNumber.getValue());
            }
            case final JsonArray jsonNodes -> {
                final com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                for (final JsonNode child : jsonNodes) {
                    arr.add(toGson(child));
                }
                return arr;
            }
            case final JsonObject jsonObject -> {
                final com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
                for (final Map.Entry<String, JsonNode> entry : jsonObject
                        .entrySet()) {
                    obj.add(entry.getKey(), toGson(entry.getValue()));
                }
                return obj;
            }
            default -> {
            }
        }

        throw new IllegalArgumentException("Unknown JsonNode type: " + node.getClass().getName());
    }
}
