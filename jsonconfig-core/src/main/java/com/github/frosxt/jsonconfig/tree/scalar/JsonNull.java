package com.github.frosxt.jsonconfig.tree.scalar;

import com.github.frosxt.jsonconfig.tree.JsonNode;

public class JsonNull extends JsonNode {
    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
    }

    @Override
    public String toString() {
        return "null";
    }
}
