package com.github.frosxt.jsonconfig.runtime.section;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonSection;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;

/**
 * Factory for creating JsonSection instances with consistent identity
 * semantics.
 */
public final class SectionFactory {

    private SectionFactory() {
    }

    /**
     * Creates a new section view.
     * 
     * @param root         the root configuration
     * @param parent       the parent section
     * @param relativePath the path relative to parent (used for fullPath
     *                     construction)
     * @param value        the underlying JsonObject
     * @return a new JsonSectionImpl
     */
    public static JsonSection create(final JsonConfiguration root, final JsonSection parent, final String relativePath,
            final JsonObject value) {
        return new JsonSectionImpl(root, parent, relativePath, value);
    }
}
