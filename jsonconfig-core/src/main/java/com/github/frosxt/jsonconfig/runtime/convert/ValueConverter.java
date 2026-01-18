package com.github.frosxt.jsonconfig.runtime.convert;

import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.runtime.view.JsonArrayView;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts between JsonNode and Java objects.
 */
public final class ValueConverter {

    private ValueConverter() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Wraps a Java object as a JsonNode.
     */
    public static JsonNode wrap(final Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }

        if (value instanceof final JsonNode jsonNode) {
            return jsonNode;
        }

        if (value instanceof final String string) {
            return new JsonString(string);
        }

        if (value instanceof final Number number) {
            return new JsonNumber(number);
        }

        if (value instanceof final Boolean bool) {
            return new JsonBoolean(bool);
        }

        if (value instanceof Map) {
            final JsonObject obj = new JsonObject();
            for (final Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
                if (e.getKey() == null) {
                    throw new IllegalArgumentException("Map keys cannot be null");
                }
                obj.add(e.getKey().toString(), wrap(e.getValue()));
            }
            return obj;
        }

        if (value instanceof List) {
            final JsonArray arr = new JsonArray();
            for (final Object o : (List<?>) value) {
                arr.add(wrap(o));
            }
            return arr;
        }

        return new JsonString(value.toString());
    }

    /**
     * Unwraps a JsonNode to a Java object.
     * Uses lazyListViews option if available.
     */
    public static Object unwrap(final JsonNode node, final JsonOptions options) {
        if (node == null || node instanceof JsonNull) {
            return null;
        }

        if (node instanceof final JsonString jsonString) {
            return jsonString.getValue();
        }

        if (node instanceof final JsonBoolean jsonBoolean) {
            return jsonBoolean.getValue();
        }

        if (node instanceof final JsonNumber jsonNumber) {
            return jsonNumber.getValue();
        }

        if (node instanceof final JsonArray jsonArray) {
            if (options != null && options.lazyListViews()) {
                return new JsonArrayView(jsonArray, options);
            }
            final List<Object> list = new ArrayList<>();
            for (final JsonNode child : jsonArray) {
                list.add(unwrap(child, options));
            }
            return list;
        }

        if (node instanceof final JsonObject jsonObject) {
            return jsonObject;
        }

        return null;
    }

    /**
     * Unwraps a JsonNode to a Java object.
     */
    public static Object unwrap(final JsonNode node) {
        return unwrap(node, null);
    }
}
