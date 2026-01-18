package com.github.frosxt.jsonconfig.api;

/**
 * Configuration options for {@link JsonConfiguration}.
 */
public interface JsonOptions {
    /**
     * Checks if the configuration should copy defaults.
     * 
     * @return true if defaults are copied
     */
    boolean copyDefaults();

    /**
     * Sets whether to copy defaults.
     * 
     * @param value true to copy defaults
     * @return this options instance
     */
    JsonOptions copyDefaults(boolean value);

    /**
     * Gets the indentation size for pretty printing.
     * 
     * @return number of spaces
     */
    int indent();

    /**
     * Sets the indentation size for pretty printing.
     * 
     * @param spaces number of spaces
     * @return this options instance
     */
    JsonOptions indent(int spaces);

    /**
     * Duplicate key handling policy.
     * <p>
     * Note: This setting primarily affects parsing. Changing it after loading
     * will not retroactively affect the configuration.
     * </p>
     */
    enum DuplicateKeyPolicy {
        ERROR, LAST_WINS, FIRST_WINS, COLLECT
    }

    /**
     * Gets the duplicate key policy.
     * 
     * @return policy
     */
    DuplicateKeyPolicy duplicateKeyPolicy();

    /**
     * Sets the duplicate key policy.
     * <p>
     * Note: This should be set before parsing (e.g. via {@link JsonParseOptions}).
     * Changing it on an existing configuration will not affect already parsed data.
     * </p>
     * 
     * @param policy policy
     * @return this options instance
     */
    JsonOptions duplicateKeyPolicy(DuplicateKeyPolicy policy);

    /**
     * Number handling mode.
     * <p>
     * Note: This setting primarily affects parsing. Changing it after loading
     * will not retroactively affect existing numbers.
     * </p>
     */
    enum NumberMode {
        EXACT, FAST_DOUBLE
    }

    /**
     * Gets the number mode.
     * 
     * @return mode
     */
    NumberMode numberMode();

    /**
     * Sets the number mode.
     * 
     * @param mode mode
     * @return this options instance
     */
    JsonOptions numberMode(NumberMode mode);

    /**
     * Path cache mode.
     */
    enum PathCacheMode {
        NONE, LRU, WEAK, THREAD_LOCAL
    }

    /**
     * Gets the path cache mode.
     * 
     * @return mode
     */
    PathCacheMode pathCacheMode();

    /**
     * Sets the path cache mode.
     * 
     * @param mode mode
     * @return this options instance
     */
    JsonOptions pathCacheMode(PathCacheMode mode);

    /**
     * Null handling policy for set operations.
     */
    enum NullHandlingPolicy {
        /** set(path, null) removes the key (Spigot-like default). */
        REMOVE,
        /** set(path, null) sets the value to explicit JSON null. */
        SET_NULL
    }

    /**
     * Gets the null handling policy.
     * 
     * @return policy
     */
    NullHandlingPolicy nullHandlingPolicy();

    /**
     * Sets the null handling policy.
     * 
     * @param policy policy
     * @return this options instance
     */
    JsonOptions nullHandlingPolicy(NullHandlingPolicy policy);

    /**
     * Whether to return lazy list views instead of copying arrays.
     * 
     * @return true if lazy views are enabled
     */
    boolean lazyListViews();

    /**
     * Sets whether to return lazy list views.
     * 
     * @param value true to enable lazy views
     * @return this options instance
     */
    JsonOptions lazyListViews(boolean value);
}
