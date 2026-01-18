package com.github.frosxt.jsonconfig.runtime.copy;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;

/**
 * Deep clones JsonNode trees.
 * 
 * <p>
 * Primitive nodes (JsonString, JsonNumber, JsonBoolean, JsonNull) are returned
 * by reference since they are immutable.
 * </p>
 */
public final class NodeCloner {

    private NodeCloner() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Clones a JsonNode tree.
     * Container nodes (JsonObject, JsonArray) are deep-copied.
     * Primitive nodes are returned by reference (immutable).
     * 
     * @param node the node to clone
     * @return cloned node (or same reference for primitives)
     */
    public static JsonNode cloneNode(final JsonNode node) {
        if (node instanceof JsonObject) {
            final JsonObject obj = new JsonObject();
            for (final java.util.Map.Entry<String, JsonNode> e : ((JsonObject) node).entrySet()) {
                obj.add(e.getKey(), cloneNode(e.getValue()));
            }
            return obj;
        } else if (node instanceof JsonArray) {
            final JsonArray arr = new JsonArray();
            for (final JsonNode child : ((JsonArray) node)) {
                arr.add(cloneNode(child));
            }
            return arr;
        }
        // Primitives are immutable - return by reference
        return node;
    }
}
