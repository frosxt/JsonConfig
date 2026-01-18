package com.github.frosxt.jsonconfig.tree.container;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonNode implements Iterable<JsonNode> {
    private final List<JsonNode> elements;

    public JsonArray() {
        this.elements = new ArrayList<>();
    }

    public JsonArray(final List<JsonNode> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public void add(JsonNode element) {
        if (element == null) {
            element = JsonNull.INSTANCE;
        }
        elements.add(element);
    }

    public void add(final String string) {
        add(new JsonString(string));
    }

    public void add(final Number number) {
        add(new JsonNumber(number));
    }

    public void add(final Boolean bool) {
        add(new JsonBoolean(bool));
    }

    public JsonNode get(final int index) {
        return elements.get(index);
    }

    public void set(final int index, JsonNode element) {
        if (element == null) {
            element = JsonNull.INSTANCE;
        }
        elements.set(index, element);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
