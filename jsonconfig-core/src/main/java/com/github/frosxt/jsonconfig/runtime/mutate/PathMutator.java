package com.github.frosxt.jsonconfig.runtime.mutate;

import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.path.JsonPath;
import com.github.frosxt.jsonconfig.path.cache.PathCache;
import com.github.frosxt.jsonconfig.runtime.convert.ValueConverter;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;

import java.util.List;

/**
 * Handles deep path mutation with support for:
 * - Intermediate container creation
 * - Array index notation
 * - Null removal policy (Spigot-like)
 */
public final class PathMutator {

    private PathMutator() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Sets a value at the given path, creating intermediate containers as needed.
     * If value is null and NullHandlingPolicy is REMOVE, the key is removed.
     * 
     * @param root    the root JsonObject
     * @param path    the path to set
     * @param value   the value to set (null triggers removal in REMOVE policy)
     * @param options configuration options
     */
    public static void set(final JsonObject root, final String path, final Object value, final JsonOptions options) {
        if (root == null) {
            return;
        }

        final JsonOptions.PathCacheMode mode = (options != null) ? options.pathCacheMode()
                : JsonOptions.PathCacheMode.NONE;
        final JsonPath jPath = PathCache.get(path, mode);
        final List<String> segments = jPath.getSegments();

        if (segments.isEmpty()) {
            return;
        }

        final boolean removeOnNull = (options == null)
                || options.nullHandlingPolicy() == JsonOptions.NullHandlingPolicy.REMOVE;

        if (value == null && removeOnNull) {
            removeRecursive(root, segments, 0);
        } else {
            setRecursive(root, segments, 0, ValueConverter.wrap(value), options);
        }
    }

    private static void setRecursive(final JsonNode current, final List<String> segments, final int index,
            final JsonNode valueToSet, final JsonOptions options) {
        if (index >= segments.size()) {
            return;
        }

        final String key = segments.get(index);
        final boolean isLast = index == segments.size() - 1;

        if (current instanceof final JsonObject jsonObject) {
            if (isLast) {
                jsonObject.add(key, valueToSet);
            } else {
                JsonNode next = jsonObject.get(key);
                if (!isContainer(next)) {
                    next = inferContainer(segments, index + 1);
                    jsonObject.add(key, next);
                }
                setRecursive(next, segments, index + 1, valueToSet, options);
            }
        } else if (current instanceof final JsonArray jsonArray) {
            try {
                final int arrayIndex = Integer.parseInt(key);

                while (jsonArray.size() <= arrayIndex) {
                    jsonArray.add(JsonNull.INSTANCE);
                }

                if (isLast) {
                    jsonArray.set(arrayIndex, valueToSet);
                } else {
                    JsonNode next = jsonArray.get(arrayIndex);
                    if (!isContainer(next)) {
                        next = inferContainer(segments, index + 1);
                        jsonArray.set(arrayIndex, next);
                    }
                    setRecursive(next, segments, index + 1, valueToSet, options);
                }
            } catch (final NumberFormatException e) {
                // Invalid array index - ignore
            }
        }
    }

    private static void removeRecursive(final JsonNode current, final List<String> segments, final int index) {
        if (index >= segments.size()) {
            return;
        }

        final String key = segments.get(index);
        final boolean isLast = index == segments.size() - 1;

        if (current instanceof final JsonObject jsonObject) {
            if (isLast) {
                jsonObject.remove(key);
            } else {
                final JsonNode next = jsonObject.get(key);
                if (next != null) {
                    removeRecursive(next, segments, index + 1);
                }
            }
        } else if (current instanceof final JsonArray jsonArray) {
            try {
                final int arrayIndex = Integer.parseInt(key);

                if (arrayIndex < jsonArray.size()) {
                    if (isLast) {
                        jsonArray.set(arrayIndex, JsonNull.INSTANCE);
                    } else {
                        removeRecursive(jsonArray.get(arrayIndex), segments, index + 1);
                    }
                }
            } catch (final NumberFormatException e) {
                // Invalid array index - ignore
            }
        }
    }

    /**
     * Infers whether to create an array or object based on the next segment.
     */
    private static JsonNode inferContainer(final List<String> segments, final int nextIndex) {
        if (nextIndex < segments.size()) {
            try {
                Integer.parseInt(segments.get(nextIndex));
                return new JsonArray();
            } catch (final NumberFormatException ignored) {
                // Ignore
            }
        }
        return new JsonObject();
    }

    private static boolean isContainer(final JsonNode node) {
        return node instanceof JsonObject || node instanceof JsonArray;
    }
}
