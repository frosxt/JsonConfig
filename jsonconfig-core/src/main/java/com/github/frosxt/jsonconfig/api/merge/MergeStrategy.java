package com.github.frosxt.jsonconfig.api.merge;

public enum MergeStrategy {
    /**
     * Overwrites existing values with the new values.
     * Arrays and objects are completely replaced.
     */
    OVERWRITE,

    /**
     * Deep merges duplicate objects.
     * Scalar values and lists are overwritten.
     */
    DEEP_MERGE_OBJECTS,

    /**
     * Concatenates arrays if they exist at the same path.
     * Objects are deep merged, scalars are overwritten.
     */
    CONCAT_ARRAYS
}
