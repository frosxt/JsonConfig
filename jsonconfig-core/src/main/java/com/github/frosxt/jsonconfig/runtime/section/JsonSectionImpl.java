package com.github.frosxt.jsonconfig.runtime.section;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.api.JsonSection;
import com.github.frosxt.jsonconfig.path.syntax.PathEscaper;

import com.github.frosxt.jsonconfig.runtime.access.PathAccess;
import com.github.frosxt.jsonconfig.runtime.convert.ValueConverter;
import com.github.frosxt.jsonconfig.runtime.defaults.DefaultsService;
import com.github.frosxt.jsonconfig.runtime.mutate.PathMutator;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonSectionImpl implements JsonSection {
    protected final JsonConfiguration root;
    protected final JsonSection parent;
    protected final String relativePath;
    protected final String fullPath;
    protected final JsonObject value;

    public JsonSectionImpl(final JsonConfiguration root, final JsonSection parent, final String relativePath,
            final JsonObject value) {
        this.root = root;
        this.parent = parent;
        this.relativePath = relativePath != null ? relativePath : "";
        this.value = value;
        this.fullPath = createPath(parent, this.relativePath);
    }

    private static String createPath(final JsonSection parent, final String relativePath) {
        if (parent == null) {
            return "";
        }

        final String parentPath = parent.getCurrentPath();
        if (parentPath == null || parentPath.isEmpty()) {
            return relativePath;
        }

        if (relativePath == null || relativePath.isEmpty()) {
            return parentPath;
        }

        return parentPath + "." + relativePath;
    }

    @Override
    public Set<String> getKeys(final boolean deep) {
        final Set<String> result = new LinkedHashSet<>();
        if (value != null) {
            getKeysRecursive(value, "", deep, result);
        }

        if (getRoot() != null && getRoot().options().copyDefaults()) {
            final JsonConfiguration defaults = getRoot().getDefaults();
            if (defaults != null) {
                final JsonSection defaultSection = defaults.getConfigurationSection(getCurrentPath());
                if (defaultSection != null) {
                    result.addAll(defaultSection.getKeys(deep));
                } else if (getCurrentPath().isEmpty()) {
                    result.addAll(defaults.getKeys(deep));
                }
            }
        }
        return result;
    }

    private void getKeysRecursive(final JsonObject current, final String currentPath, final boolean deep,
            final Set<String> keys) {
        for (final Map.Entry<String, JsonNode> entry : current.entrySet()) {
            final String key = entry.getKey();
            final String fullKey = PathEscaper.buildPath(currentPath, key);
            keys.add(fullKey);

            if (deep && entry.getValue() instanceof final JsonObject jsonObject) {
                getKeysRecursive(jsonObject, fullKey, deep, keys);
            }
        }
    }

    @Override
    public Map<String, Object> getValues(final boolean deep) {
        final Map<String, Object> result = new LinkedHashMap<>();

        if (getRoot() != null && getRoot().options().copyDefaults()) {
            final JsonConfiguration defaults = getRoot().getDefaults();
            if (defaults != null) {
                final JsonSection defaultSection = defaults.getConfigurationSection(getCurrentPath());
                if (defaultSection != null) {
                    result.putAll(defaultSection.getValues(deep));
                } else if (getCurrentPath().isEmpty()) {
                    result.putAll(defaults.getValues(deep));
                }
            }
        }

        if (value != null) {
            getValuesRecursive(this, value, "", deep, result);
        }

        return result;
    }

    private void getValuesRecursive(final JsonSection currentSection, final JsonObject current,
            final String currentPath, final boolean deep, final Map<String, Object> values) {
        for (final Map.Entry<String, JsonNode> entry : current.entrySet()) {
            final String key = entry.getKey();
            final String fullKey = PathEscaper.buildPath(currentPath, key);
            final JsonNode node = entry.getValue();

            if (node instanceof final JsonObject jsonObject) {
                // Return JsonSection view for object values (Spigot-like)
                // Use escaped key segment for relative path, but fullKey for map key
                final JsonSection childSection = SectionFactory.create(getRoot(), currentSection,
                        PathEscaper.escapeSegment(key), jsonObject);
                values.put(fullKey, childSection);
                if (deep) {
                    getValuesRecursive(childSection, jsonObject, fullKey, deep, values);
                }
            } else {
                values.put(fullKey, ValueConverter.unwrap(node, options()));
            }
        }
    }

    @Override
    public boolean contains(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        if (node != null) {
            return true;
        }

        final JsonConfiguration rootConfig = getRoot();
        if (rootConfig != null) {
            final JsonConfiguration defaults = rootConfig.getDefaults();
            if (defaults != null) {
                final String effectivePath = fullPath.isEmpty() ? path : fullPath + "." + path;
                return defaults.getNode(effectivePath) != null;
            }
        }

        return false;
    }

    @Override
    public boolean isSet(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        return node != null && !(node instanceof JsonNull);
    }

    @Override
    public String getCurrentPath() {
        return fullPath;
    }

    @Override
    public String getName() {
        if (relativePath == null || relativePath.isEmpty()) {
            return "";
        }

        final var segments = com.github.frosxt.jsonconfig.path.cache.PathCache.get(relativePath,
                options() != null ? options().pathCacheMode() : JsonOptions.PathCacheMode.NONE).getSegments();
        return segments.isEmpty() ? relativePath : segments.getLast();
    }

    @Override
    public JsonConfiguration getRoot() {
        return root;
    }

    @Override
    public JsonSection getParent() {
        return parent;
    }

    @Override
    public JsonSection getConfigurationSection(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (path.isEmpty()) {
            return this;
        }

        JsonSection currentSection = this;
        JsonNode currentNode = this.value;
        final StringBuilder pathBuilder = new StringBuilder();

        final List<String> segments = com.github.frosxt.jsonconfig.path.cache.PathCache.get(path,
                options() != null ? options().pathCacheMode() : JsonOptions.PathCacheMode.NONE).getSegments();

        for (final String segment : segments) {
            final boolean isArray = (currentNode instanceof JsonArray);

            if (isArray) {
                pathBuilder.append('[').append(segment).append(']');
            } else {
                if (!pathBuilder.isEmpty()) {
                    pathBuilder.append('.');
                }
                pathBuilder.append(PathEscaper.escapeSegment(segment));
            }

            switch (currentNode) {
                case final JsonObject jsonObject -> currentNode = jsonObject.get(segment);
                case final JsonArray jsonArray -> {
                    try {
                        final int index = Integer.parseInt(segment);
                        if (index >= 0 && index < jsonArray.size()) {
                            currentNode = jsonArray.get(index);
                        } else {
                            return null;
                        }
                    } catch (final NumberFormatException e) {
                        return null;
                    }
                }
                default -> {
                    return null;
                }
            }

            if (currentNode == null) {
                return null;
            }

            if (currentNode instanceof final JsonObject jsonObject) {
                currentSection = SectionFactory.create(getRoot(), currentSection, pathBuilder.toString(),
                        jsonObject);
                pathBuilder.setLength(0);
            }
        }

        return (currentNode instanceof JsonObject) ? currentSection : null;
    }

    @Override
    public JsonSection createSection(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (path.isEmpty()) {
            return this;
        }

        final JsonObject newSection = new JsonObject();
        PathMutator.set(value, path, newSection, options());

        return getConfigurationSection(path);
    }

    @Override
    public Object get(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (path.isEmpty()) {
            return this;
        }

        final JsonNode node = getNode(path);
        if (node == null || node instanceof JsonNull) {
            final JsonConfiguration rootConfig = getRoot();
            if (rootConfig != null) {
                final JsonConfiguration defaults = rootConfig.getDefaults();
                if (defaults != null) {
                    final String effectivePath = fullPath.isEmpty() ? path : fullPath + "." + path;
                    final JsonNode defNode = defaults.getNode(effectivePath);
                    if (defNode != null && !(defNode instanceof JsonNull)) {
                        if (defNode instanceof JsonObject) {
                            return null;
                        }
                        return ValueConverter.unwrap(defNode, options());
                    }
                }
            }
            return null;
        }

        if (node instanceof JsonObject) {
            return getConfigurationSection(path);
        }

        return ValueConverter.unwrap(node, options());
    }

    @Override
    public JsonNode getNode(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (path.isEmpty()) {
            return value;
        }
        return PathAccess.getNode(value, path, options());
    }

    @Override
    public Object get(final String path, final Object def) {
        final Object val = get(path);
        return (val != null) ? val : def;
    }

    @Override
    public void set(final String path, final Object value) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (path.isEmpty()) {
            throw new IllegalArgumentException("Cannot set empty path");
        }

        PathMutator.set(this.value, path, value, options());
    }

    protected JsonOptions options() {
        return getRoot() != null ? getRoot().options() : null;
    }

    @Override
    public String getString(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        if (node instanceof final JsonString s) {
            return s.getValue();
        }

        if (node instanceof JsonNull) {
            return null;
        }

        final Object val = get(path);
        return (val != null) ? val.toString() : null;
    }

    @Override
    public String getString(final String path, final String def) {
        final String val = getString(path);
        return (val != null) ? val : def;
    }

    @Override
    public int getInt(final String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(final String path, final int def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        final JsonNode node = getNode(path);
        if (node instanceof final JsonNumber n) {
            return n.intValue();
        }

        final Object val = get(path);
        if (val instanceof final Number number) {
            return number.intValue();
        }

        return def;
    }

    @Override
    public boolean getBoolean(final String path) {
        return getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(final String path, final boolean def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        final JsonNode node = getNode(path);
        if (node instanceof final JsonBoolean b) {
            return b.getValue();
        }

        final Object val = get(path);
        if (val instanceof final Boolean bool) {
            return bool;
        }

        return def;
    }

    @Override
    public double getDouble(final String path) {
        return getDouble(path, 0.0);
    }

    @Override
    public double getDouble(final String path, final double def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        if (node instanceof final JsonNumber n) {
            return n.doubleValue();
        }

        final Object val = get(path);
        if (val instanceof final Number number) {
            return number.doubleValue();
        }

        return def;
    }

    @Override
    public long getLong(final String path) {
        return getLong(path, 0);
    }

    @Override
    public long getLong(final String path, final long def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        if (node instanceof final JsonNumber n) {
            return n.longValue();
        }

        final Object val = get(path);
        if (val instanceof final Number number) {
            return number.longValue();
        }
        return def;
    }

    @Override
    public BigInteger getBigInteger(final String path) {
        return getBigInteger(path, null);
    }

    @Override
    public BigInteger getBigInteger(final String path, final BigInteger def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final JsonNode node = getNode(path);
        if (node instanceof final JsonNumber n) {
            return n.bigIntegerValue();
        }

        final Object val = get(path);
        if (val instanceof final BigInteger bigInteger) {
            return bigInteger;
        }

        if (val instanceof final Number number) {
            return BigInteger.valueOf(number.longValue());
        }

        return def;
    }

    @Override
    public BigDecimal getBigDecimal(final String path) {
        return getBigDecimal(path, null);
    }

    @Override
    public BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        final JsonNode node = getNode(path);
        if (node instanceof final JsonNumber n) {
            return n.bigDecimalValue();
        }

        final Object val = get(path);
        if (val instanceof final BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (val instanceof final Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        return def;
    }

    @Override
    public List<?> getList(final String path) {
        return getList(path, null);
    }

    @Override
    public List<?> getList(final String path, final List<?> def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final Object val = get(path);
        if (val instanceof final List<?> list) {
            return list;
        }

        return def;
    }

    @Override
    public List<String> getStringList(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }

        final List<String> result = new ArrayList<>();
        for (final Object o : list) {
            if (o instanceof final String string) {
                result.add(string);
            } else if (o != null) {
                result.add(o.toString());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getIntegerList(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }

        final List<Integer> result = new ArrayList<>();
        for (final Object o : list) {
            if (o instanceof final Number number) {
                result.add(number.intValue());
            }
        }
        return result;
    }

    @Override
    public void addDefault(final String path, final Object value) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        final String effectivePath = fullPath.isEmpty() ? path : fullPath + "." + path;
        DefaultsService.addDefault(getRoot(), effectivePath, value);
    }

    @Override
    public boolean isString(final String path) {
        return getNode(path) instanceof JsonString;
    }

    @Override
    public boolean isInt(final String path) {
        return getNode(path) instanceof JsonNumber;
    }

    @Override
    public boolean isBoolean(final String path) {
        return getNode(path) instanceof JsonBoolean;
    }

    @Override
    public boolean isDouble(final String path) {
        return getNode(path) instanceof JsonNumber;
    }

    @Override
    public boolean isLong(final String path) {
        return getNode(path) instanceof JsonNumber;
    }

    @Override
    public boolean isList(final String path) {
        return getNode(path) instanceof JsonArray;
    }

    @Override
    public boolean isSection(final String path) {
        return getNode(path) instanceof JsonObject;
    }
}
