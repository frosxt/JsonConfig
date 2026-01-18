package com.github.frosxt.jsonconfig.api;

/**
 * Options that affect JSON parsing behavior.
 * Separate from runtime options since these must be set before parsing.
 */
public class JsonParseOptions {
    private JsonOptions.DuplicateKeyPolicy duplicateKeyPolicy = JsonOptions.DuplicateKeyPolicy.ERROR;
    private JsonOptions.NumberMode numberMode = JsonOptions.NumberMode.EXACT;

    public JsonOptions.DuplicateKeyPolicy duplicateKeyPolicy() {
        return duplicateKeyPolicy;
    }

    public JsonParseOptions duplicateKeyPolicy(final JsonOptions.DuplicateKeyPolicy policy) {
        this.duplicateKeyPolicy = policy;
        return this;
    }

    public JsonOptions.NumberMode numberMode() {
        return numberMode;
    }

    public JsonParseOptions numberMode(final JsonOptions.NumberMode mode) {
        this.numberMode = mode;
        return this;
    }
}
