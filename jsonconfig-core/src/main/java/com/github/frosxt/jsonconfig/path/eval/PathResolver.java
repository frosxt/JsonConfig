package com.github.frosxt.jsonconfig.path.eval;

import com.github.frosxt.jsonconfig.path.JsonPath;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;

import java.util.List;

public class PathResolver {

    private PathResolver() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static JsonNode get(final JsonNode root, final JsonPath path) {
        JsonNode current = root;
        final List<String> segments = path.getSegments();

        for (final String segment : segments) {
            if (current instanceof final JsonObject jsonObject) {
                current = jsonObject.get(segment);
            } else if (current instanceof final JsonArray jsonArray) {
                try {
                    final int index = Integer.parseInt(segment);
                    if (index >= 0 && index < jsonArray.size()) {
                        current = jsonArray.get(index);
                    } else {
                        return null;
                    }
                } catch (final NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }
}
