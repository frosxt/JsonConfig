package com.github.frosxt.jsonconfig.runtime.config;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;

/**
 * Factory for creating JsonConfiguration instances.
 */
public final class JsonConfigurationFactory {

    private JsonConfigurationFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Creates a new empty JsonConfiguration.
     * 
     * @return a new empty configuration
     */
    public static JsonConfiguration createEmpty() {
        return new JsonConfigurationImpl();
    }
}
