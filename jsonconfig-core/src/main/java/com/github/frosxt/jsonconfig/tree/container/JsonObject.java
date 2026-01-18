package com.github.frosxt.jsonconfig.tree.container;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonNode {
    private final Map<String, JsonNode> members;

    public JsonObject() {
        this.members = new LinkedHashMap<>();
    }

    public JsonObject(final Map<String, JsonNode> members) {
        this.members = new LinkedHashMap<>(members);
    }

    public void add(final String property, JsonNode value) {
        if (value == null) {
            value = JsonNull.INSTANCE;
        }
        members.put(property, value);
    }

    public JsonNode get(final String property) {
        return members.get(property);
    }

    public JsonNode remove(final String property) {
        return members.remove(property);
    }

    public boolean has(final String property) {
        return members.containsKey(property);
    }

    public Set<Map.Entry<String, JsonNode>> entrySet() {
        return members.entrySet();
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public int size() {
        return members.size();
    }

    @Override
    public String toString() {
        return members.toString();
    }
}
