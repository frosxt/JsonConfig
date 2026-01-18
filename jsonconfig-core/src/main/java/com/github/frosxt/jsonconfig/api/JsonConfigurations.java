package com.github.frosxt.jsonconfig.api;

import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * Static factory for loading JSON configurations.
 */
public final class JsonConfigurations {

    private JsonConfigurations() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Loads configuration from a file.
     * 
     * @param path file path
     * @return loaded configuration
     * @throws IOException if read fails
     */
    public static JsonConfiguration load(final Path path) throws IOException {
        return JsonConfigurationImpl.loadConfiguration(path);
    }

    /**
     * Loads configuration from a file with parse options.
     * 
     * @param path         file path
     * @param parseOptions options for parsing (duplicate keys, number mode)
     * @return loaded configuration
     * @throws IOException if read fails
     */
    public static JsonConfiguration load(final Path path, final JsonParseOptions parseOptions) throws IOException {
        return JsonConfigurationImpl.loadConfiguration(path, parseOptions);
    }

    /**
     * Loads configuration from a reader.
     * 
     * @param reader reader
     * @return loaded configuration
     * @throws IOException if read fails
     */
    public static JsonConfiguration load(final Reader reader) throws IOException {
        return JsonConfigurationImpl.loadConfiguration(reader);
    }

    /**
     * Loads configuration from a reader with parse options.
     * 
     * @param reader       reader
     * @param parseOptions options for parsing (duplicate keys, number mode)
     * @return loaded configuration
     * @throws IOException if read fails
     */
    public static JsonConfiguration load(final Reader reader, final JsonParseOptions parseOptions) throws IOException {
        return JsonConfigurationImpl.loadConfiguration(reader, parseOptions);
    }
}
