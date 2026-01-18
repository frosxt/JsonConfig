package com.github.frosxt.jsonconfig.api;

import com.github.frosxt.jsonconfig.tree.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a section of the JSON configuration.
 */
public interface JsonSection {
    /**
     * Gets the set of keys in this section.
     * 
     * @param deep if true, includes keys from children
     * @return set of keys
     */
    Set<String> getKeys(boolean deep);

    /**
     * Gets a map of values in this section.
     * 
     * @param deep if true, includes values from children
     * @return map of values
     */
    Map<String, Object> getValues(boolean deep);

    /**
     * Checks if the path exists in the configuration.
     * 
     * @param path path to check
     * @return true if exists
     */
    boolean contains(String path);

    /**
     * Checks if the path is set (exists and is not null) in the actual
     * configuration.
     * This does NOT check defaults - a path that exists only in defaults returns
     * false.
     * Use {@link #contains(String)} to check existence including defaults.
     * 
     * @param path path to check
     * @return true if set in actual config (not defaults) and value is not null
     */
    boolean isSet(String path);

    /**
     * Gets the path of this section relative to the root.
     * 
     * @return path string
     */
    String getCurrentPath();

    /**
     * Gets the name of this section.
     * 
     * @return name
     */
    String getName();

    /**
     * Gets the root configuration.
     * 
     * @return root
     */
    JsonConfiguration getRoot();

    /**
     * Gets the parent section.
     * <p>
     * This corresponds to the structural parent section used to retrieve this view.
     * </p>
     * 
     * @return parent section or null if root
     */
    JsonSection getParent();

    /**
     * Gets a subsection.
     * 
     * @param path path to subsection
     * @return subsection or null if not found/not a section
     */
    JsonSection getConfigurationSection(String path);

    /**
     * Creates a new empty section at the specified path.
     * 
     * @param path path to create
     * @return created section
     */
    JsonSection createSection(String path);

    /**
     * Gets the value at the specified path.
     * 
     * @param path path to value
     * @return value or null if not found
     */
    Object get(String path);

    /**
     * Gets the value at the specified path, returning a default if not found.
     * 
     * @param path path to value
     * @param def  default value
     * @return value or default
     */
    Object get(String path, Object def);

    /**
     * Gets the requested String by path.
     * 
     * @param path path to string
     * @return string value or null
     */
    String getString(String path);

    /**
     * Gets the requested String by path, returning a default if not found.
     * 
     * @param path path to string
     * @param def  default value
     * @return string value or default
     */
    String getString(String path, String def);

    /**
     * Gets the requested int by path.
     * 
     * @param path path to int
     * @return int value or 0 if not found
     */
    int getInt(String path);

    /**
     * Gets the requested int by path, returning a default if not found.
     * 
     * @param path path to int
     * @param def  default value
     * @return int value or default
     */
    int getInt(String path, int def);

    /**
     * Gets the requested boolean by path.
     * 
     * @param path path to boolean
     * @return boolean value or false if not found
     */
    boolean getBoolean(String path);

    /**
     * Gets the requested boolean by path, returning a default if not found.
     * 
     * @param path path to boolean
     * @param def  default value
     * @return boolean value or default
     */
    boolean getBoolean(String path, boolean def);

    /**
     * Gets the requested double by path.
     * 
     * @param path path to double
     * @return double value or 0.0 if not found
     */
    double getDouble(String path);

    /**
     * Gets the requested double by path, returning a default if not found.
     * 
     * @param path path to double
     * @param def  default value
     * @return double value or default
     */
    double getDouble(String path, double def);

    /**
     * Gets the requested long by path.
     * 
     * @param path path to long
     * @return long value or 0L if not found
     */
    long getLong(String path);

    /**
     * Gets the requested long by path, returning a default if not found.
     * 
     * @param path path to long
     * @param def  default value
     * @return long value or default
     */
    long getLong(String path, long def);

    /**
     * Gets the requested BigInteger by path.
     * 
     * @param path path to BigInteger
     * @return BigInteger value or null (or 0 equivalent) if not found, depending on
     *         impl
     */
    BigInteger getBigInteger(String path);

    /**
     * Gets the requested BigInteger by path, returning a default if not found.
     * 
     * @param path path to BigInteger
     * @param def  default value
     * @return BigInteger value or default
     */
    BigInteger getBigInteger(String path, BigInteger def);

    /**
     * Gets the requested BigDecimal by path.
     * 
     * @param path path to BigDecimal
     * @return BigDecimal value or null (or 0.0 equivalent) if not found
     */
    BigDecimal getBigDecimal(String path);

    /**
     * Gets the requested BigDecimal by path, returning a default if not found.
     * 
     * @param path path to BigDecimal
     * @param def  default value
     * @return BigDecimal value or default
     */
    BigDecimal getBigDecimal(String path, BigDecimal def);

    /**
     * Gets the requested List by path.
     * 
     * @param path path to List
     * @return List or null if not found
     */
    List<?> getList(String path);

    /**
     * Gets the requested List by path, returning a default if not found.
     * 
     * @param path path to List
     * @param def  default value
     * @return List or default
     */
    List<?> getList(String path, List<?> def);

    /**
     * Gets a list of strings by path.
     * 
     * @param path path to string list
     * @return list of strings, empty if not found
     */
    List<String> getStringList(String path);

    /**
     * Gets a list of integers by path.
     * 
     * @param path path to integer list
     * @return list of integers, empty if not found
     */
    List<Integer> getIntegerList(String path);

    /**
     * Sets the value at the specified path.
     * 
     * @param path  path to set
     * @param value value to set, or null to remove (depending on options)
     */
    void set(String path, Object value);

    /**
     * Add a default value.
     * 
     * @param path  path
     * @param value value
     */
    void addDefault(String path, Object value);

    /**
     * Checks if the value at the specified path is a String.
     * 
     * @param path path to check
     * @return true if string
     */
    boolean isString(String path);

    /**
     * Checks if the value at the specified path is an int.
     * 
     * @param path path to check
     * @return true if int
     */
    boolean isInt(String path);

    /**
     * Checks if the value at the specified path is a boolean.
     * 
     * @param path path to check
     * @return true if boolean
     */
    boolean isBoolean(String path);

    /**
     * Checks if the value at the specified path is a double.
     * 
     * @param path path to check
     * @return true if double
     */
    boolean isDouble(String path);

    /**
     * Checks if the value at the specified path is a long.
     * 
     * @param path path to check
     * @return true if long
     */
    boolean isLong(String path);

    /**
     * Checks if the value at the specified path is a List.
     * 
     * @param path path to check
     * @return true if list
     */
    boolean isList(String path);

    /**
     * Checks if the value at the specified path is a Section (JSON Object).
     * 
     * @param path path to check
     * @return true if section
     */
    boolean isSection(String path);

    /**
     * Gets the raw JsonNode at the path.
     * 
     * @param path path
     * @return JsonNode or null
     */
    JsonNode getNode(String path);
}
