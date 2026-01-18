package com.github.frosxt.jsonconfig.runtime.config;

import com.github.frosxt.jsonconfig.api.JsonOptions;

/**
 * Default implementation of JsonOptions for runtime configuration.
 */
public class JsonOptionsImpl implements JsonOptions {
    private boolean copyDefaults = false;
    private int indent = 2;
    private DuplicateKeyPolicy duplicateKeyPolicy = DuplicateKeyPolicy.ERROR;
    private NumberMode numberMode = NumberMode.EXACT;
    private PathCacheMode pathCacheMode = PathCacheMode.NONE;
    private NullHandlingPolicy nullHandlingPolicy = NullHandlingPolicy.REMOVE;
    private boolean lazyListViews = false;

    @Override
    public boolean copyDefaults() {
        return copyDefaults;
    }

    @Override
    public JsonOptions copyDefaults(final boolean value) {
        this.copyDefaults = value;
        return this;
    }

    @Override
    public int indent() {
        return indent;
    }

    @Override
    public JsonOptions indent(final int spaces) {
        if (spaces < 0) {
            throw new IllegalArgumentException("Indent cannot be negative");
        }

        this.indent = spaces;
        return this;
    }

    @Override
    public DuplicateKeyPolicy duplicateKeyPolicy() {
        return duplicateKeyPolicy;
    }

    @Override
    public JsonOptions duplicateKeyPolicy(final DuplicateKeyPolicy policy) {
        this.duplicateKeyPolicy = policy;
        return this;
    }

    @Override
    public NumberMode numberMode() {
        return numberMode;
    }

    @Override
    public JsonOptions numberMode(final NumberMode mode) {
        this.numberMode = mode;
        return this;
    }

    @Override
    public PathCacheMode pathCacheMode() {
        return pathCacheMode;
    }

    @Override
    public JsonOptions pathCacheMode(final PathCacheMode mode) {
        this.pathCacheMode = mode;
        return this;
    }

    @Override
    public NullHandlingPolicy nullHandlingPolicy() {
        return nullHandlingPolicy;
    }

    @Override
    public JsonOptions nullHandlingPolicy(final NullHandlingPolicy policy) {
        this.nullHandlingPolicy = policy;
        return this;
    }

    @Override
    public boolean lazyListViews() {
        return lazyListViews;
    }

    @Override
    public JsonOptions lazyListViews(final boolean value) {
        this.lazyListViews = value;
        return this;
    }
}
