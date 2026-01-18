package com.github.frosxt.jsonconfig.api;

import com.github.frosxt.jsonconfig.api.merge.MergeStrategy;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents the root configuration object.
 */
public interface JsonConfiguration extends JsonSection {
    /**
     * Gets the options for this configuration.
     * 
     * @return options
     */
    JsonOptions options();

    /**
     * Sets the defaults for this configuration.
     * 
     * @param defaults defaults configuration
     */
    void setDefaults(JsonConfiguration defaults);

    /**
     * Gets the defaults configuration.
     * 
     * @return defaults or null
     */
    JsonConfiguration getDefaults();

    /**
     * Saves the configuration to a string.
     * 
     * @return json string
     */
    String saveToString();

    /**
     * Saves the configuration to a file.
     * 
     * @param path file path
     * @throws IOException if write fails
     */
    void save(Path path) throws IOException;

    /**
     * Merges another configuration into this one.
     * 
     * @param other    configuration to merge
     * @param strategy merge strategy
     */
    void merge(JsonConfiguration other, MergeStrategy strategy);
}
