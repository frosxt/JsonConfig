package com.github.frosxt.jsonconfig.runtime.config;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.api.JsonParseOptions;
import com.github.frosxt.jsonconfig.api.merge.MergeStrategy;
import com.github.frosxt.jsonconfig.reader.JsonParser;
import com.github.frosxt.jsonconfig.runtime.merge.MergeService;
import com.github.frosxt.jsonconfig.runtime.section.JsonSectionImpl;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.writer.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfigurationImpl extends JsonSectionImpl implements JsonConfiguration {
    private final JsonOptionsImpl options;
    private JsonConfiguration defaults;

    public JsonConfigurationImpl() {
        super(null, null, "", new JsonObject());
        this.options = new JsonOptionsImpl();
    }

    private JsonConfigurationImpl(final JsonObject root) {
        super(null, null, "", root);
        this.options = new JsonOptionsImpl();
    }

    @Override
    public JsonConfiguration getRoot() {
        return this;
    }

    @Override
    public JsonOptions options() {
        return options;
    }

    @Override
    public void setDefaults(final JsonConfiguration defaults) {
        this.defaults = defaults;
    }

    @Override
    public JsonConfiguration getDefaults() {
        return defaults;
    }

    @Override
    public void addDefault(final String path, final Object value) {
        if (defaults == null) {
            defaults = new JsonConfigurationImpl();
        }
        defaults.set(path, value);
    }

    @Override
    public String saveToString() {
        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(writer, options.indent());
        try {
            if (options.copyDefaults() && defaults != null) {
                final JsonConfigurationImpl tempConfig = new JsonConfigurationImpl();
                tempConfig.merge(defaults, MergeStrategy.OVERWRITE);
                tempConfig.merge(this, MergeStrategy.OVERWRITE);
                jsonWriter.write(tempConfig.value);
            } else {
                jsonWriter.write(value);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write to string", e);
        }
        return writer.toString();
    }

    @Override
    public void merge(final JsonConfiguration other, final MergeStrategy strategy) {
        final JsonNode otherRoot = other.getNode("");
        if (!(otherRoot instanceof JsonObject)) {
            throw new IllegalArgumentException("Cannot merge: other configuration root is not a JsonObject");
        }

        new MergeService().merge(this.value, (JsonObject) otherRoot, strategy);
    }

    @Override
    public void save(final Path path) throws IOException {
        final String data = saveToString();
        Files.writeString(path, data, StandardCharsets.UTF_8);
    }

    public static JsonConfiguration loadConfiguration(final Path path) throws IOException {
        return loadConfiguration(path, null);
    }

    public static JsonConfiguration loadConfiguration(final Path path, final JsonParseOptions parseOptions) throws IOException {
        try (final Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return loadConfiguration(reader, parseOptions);
        }
    }

    public static JsonConfiguration loadConfiguration(final Reader reader) throws IOException {
        return loadConfiguration(reader, null);
    }

    public static JsonConfiguration loadConfiguration(final Reader reader, final JsonParseOptions parseOptions)
            throws IOException {
        final JsonParser parser = new JsonParser(reader, parseOptions);
        final JsonNode node = parser.parse();

        if (node instanceof final JsonObject jsonObject) {
            return new JsonConfigurationImpl(jsonObject);
        } else {
            throw new IOException("Root of configuration must be a JSON object, found: " + node.getClass().getSimpleName());
        }
    }
}
