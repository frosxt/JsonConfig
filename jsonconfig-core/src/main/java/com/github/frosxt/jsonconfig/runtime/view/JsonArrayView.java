package com.github.frosxt.jsonconfig.runtime.view;

import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.runtime.convert.ValueConverter;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * A read-only view over a JsonArray that lazily unwraps elements on access.
 * Avoids the allocation overhead of copying the entire array upfront.
 */
public class JsonArrayView extends AbstractList<Object> implements RandomAccess {
    private final JsonArray array;
    private final JsonOptions options;

    public JsonArrayView(final JsonArray array) {
        this(array, null);
    }

    public JsonArrayView(final JsonArray array, final JsonOptions options) {
        this.array = array;
        this.options = options;
    }

    @Override
    public Object get(final int index) {
        final JsonNode node = array.get(index);
        return ValueConverter.unwrap(node, options);
    }

    @Override
    public int size() {
        return array.size();
    }

    /**
     * Returns the underlying JsonArray.
     * 
     * @return the backing JsonArray
     */
    public JsonArray getBackingArray() {
        return array;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final JsonArrayView other)) {
            return false;
        }
        return array.equals(other.array);
    }

    @Override
    public int hashCode() {
        return array.hashCode();
    }
}
