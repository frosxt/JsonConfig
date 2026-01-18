package com.github.frosxt.jsonconfig.tree.scalar;

import com.github.frosxt.jsonconfig.tree.JsonNode;

public class JsonString extends JsonNode {
    private final String value;

    public JsonString(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("JsonString value cannot be null");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\""; // Proper escaping in writer
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsonString that = (JsonString) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
