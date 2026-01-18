package com.github.frosxt.jsonconfig.tree;

import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

/**
 * Represents a node in the JSON tree.
 */
public abstract class JsonNode {

    /**
     * Checks if this node is an object.
     */
    public boolean isObject() {
        return this instanceof JsonObject;
    }

    /**
     * Checks if this node is an array.
     */
    public boolean isArray() {
        return this instanceof JsonArray;
    }

    /**
     * Checks if this node is a string.
     */
    public boolean isString() {
        return this instanceof JsonString;
    }

    /**
     * Checks if this node is a number.
     */
    public boolean isNumber() {
        return this instanceof JsonNumber;
    }

    /**
     * Checks if this node is a boolean.
     */
    public boolean isBoolean() {
        return this instanceof JsonBoolean;
    }

    /**
     * Checks if this node is null.
     */
    public boolean isNull() {
        return this instanceof JsonNull;
    }
}
