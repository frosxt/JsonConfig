package com.github.frosxt.jsonconfig.runtime.defaults;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationFactory;

/**
 * Service for managing default values in configurations.
 */
public final class DefaultsService {

    private DefaultsService() {
    }

    /**
     * Adds a default value at the specified path.
     * Creates the defaults configuration if it doesn't exist.
     * 
     * @param root     the root configuration
     * @param fullPath the absolute path for the default
     * @param value    the default value
     */
    public static void addDefault(final JsonConfiguration root, final String fullPath, final Object value) {
        if (root == null) {
            return;
        }

        JsonConfiguration defaults = root.getDefaults();
        if (defaults == null) {
            defaults = JsonConfigurationFactory.createEmpty();
            root.setDefaults(defaults);
        }

        defaults.set(fullPath, value);
    }
}
