package com.github.frosxt.jsonconfig.writer;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class JsonWriter {
    private final Writer writer;
    private final Mode mode;
    private final int indentSize;

    private int depth = 0;

    public JsonWriter(final Writer writer, final int indent) {
        this.writer = writer;
        this.indentSize = indent;
        this.mode = (indent > 0) ? Mode.PRETTY : Mode.COMPACT;
    }

    public void write(final JsonNode node) throws IOException {
        if (node instanceof final JsonObject jsonObject) {
            writeObject(jsonObject);
        } else if (node instanceof final JsonArray jsonArray) {
            writeArray((jsonArray));
        } else if (node instanceof final JsonString jsonString) {
            writeString(jsonString);
        } else if (node instanceof JsonNumber) {
            writer.write(node.toString());
        } else if (node instanceof JsonBoolean) {
            writer.write(node.toString());
        } else if (node instanceof JsonNull) {
            writer.write("null");
        }
    }

    private void writeObject(final JsonObject object) throws IOException {
        writer.write('{');
        if (object.size() > 0) {
            if (mode == Mode.PRETTY) {
                writer.write('\n');
                depth++;
            }

            final Iterator<Map.Entry<String, JsonNode>> it = object.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> entry = it.next();
                indent();
                writeString(entry.getKey());
                writer.write(':');
                if (mode == Mode.PRETTY) {
                    writer.write(' ');
                }
                write(entry.getValue());

                if (it.hasNext()) {
                    writer.write(',');
                    if (mode == Mode.PRETTY) {
                        writer.write('\n');
                    }
                }
            }

            if (mode == Mode.PRETTY) {
                writer.write('\n');
                depth--;
                indent();
            }
        }
        writer.write('}');
    }

    private void writeArray(final JsonArray array) throws IOException {
        writer.write('[');
        if (array.size() > 0) {
            if (mode == Mode.PRETTY) {
                writer.write('\n');
                depth++;
            }

            final Iterator<JsonNode> it = array.iterator();
            while (it.hasNext()) {
                final JsonNode node = it.next();
                indent();
                write(node);

                if (it.hasNext()) {
                    writer.write(',');
                    if (mode == Mode.PRETTY) {
                        writer.write('\n');
                    }
                }
            }

            if (mode == Mode.PRETTY) {
                writer.write('\n');
                depth--;
                indent();
            }
        }
        writer.write(']');
    }

    private void writeString(final JsonString string) throws IOException {
        writeString(string.getValue());
    }

    private void writeString(final String value) throws IOException {
        writer.write('"');
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '"':
                    writer.write("\\\"");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                case '\b':
                    writer.write("\\b");
                    break;
                case '\f':
                    writer.write("\\f");
                    break;
                case '\n':
                    writer.write("\\n");
                    break;
                case '\r':
                    writer.write("\\r");
                    break;
                case '\t':
                    writer.write("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        final String hex = Integer.toHexString(c);
                        writer.write("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) {
                            writer.write('0');
                        }
                        writer.write(hex);
                    } else {
                        writer.write(c);
                    }
            }
        }
        writer.write('"');
    }

    private void indent() throws IOException {
        if (mode == Mode.PRETTY) {
            for (int i = 0; i < depth * indentSize; i++) {
                writer.write(' ');
            }
        }
    }

    public enum Mode {
        COMPACT,
        PRETTY
    }
}
