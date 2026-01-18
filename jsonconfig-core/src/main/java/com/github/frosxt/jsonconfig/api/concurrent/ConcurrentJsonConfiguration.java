package com.github.frosxt.jsonconfig.api.concurrent;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.api.JsonSection;
import com.github.frosxt.jsonconfig.api.merge.MergeStrategy;
import com.github.frosxt.jsonconfig.tree.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A thread-safe implementation of {@link JsonConfiguration}.
 * <p>
 * This class wraps another {@link JsonConfiguration} and protects all access
 * with a {@link ReadWriteLock}.
 * </p>
 */
public class ConcurrentJsonConfiguration implements JsonConfiguration {
    private final JsonConfiguration delegate;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Creates a new concurrent configuration wrapper.
     * 
     * @param delegate the configuration to wrap
     */
    public ConcurrentJsonConfiguration(final JsonConfiguration delegate) {
        this.delegate = delegate;
    }

    private <T> T read(final Supplier<T> action) {
        lock.readLock().lock();
        try {
            return action.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void write(final Runnable action) {
        lock.writeLock().lock();
        try {
            action.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets the options for this configuration.
     * <p>
     * Note: The returned JsonOptions object is the underlying mutable instance.
     * Mutating options is NOT thread-safe and should be done during initialization
     * or external synchronization.
     * </p>
     * 
     * @return mutable options instance
     */
    @Override
    public JsonOptions options() {
        return read(delegate::options);
    }

    @Override
    public void setDefaults(final JsonConfiguration defaults) {
        write(() -> delegate.setDefaults(defaults));
    }

    @Override
    public JsonConfiguration getDefaults() {
        return read(delegate::getDefaults);
    }

    @Override
    public String saveToString() {
        return read(delegate::saveToString); // Save is effectively a read
    }

    @Override
    public void save(final Path path) throws IOException {
        lock.readLock().lock();
        try {
            delegate.save(path);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void merge(final JsonConfiguration other, final MergeStrategy strategy) {
        write(() -> delegate.merge(other, strategy));
    }

    @Override
    public Set<String> getKeys(final boolean deep) {
        return read(() -> delegate.getKeys(deep));
    }

    @Override
    public Map<String, Object> getValues(final boolean deep) {
        return read(() -> delegate.getValues(deep));
    }

    @Override
    public boolean contains(final String path) {
        return read(() -> delegate.contains(path));
    }

    @Override
    public boolean isSet(final String path) {
        return read(() -> delegate.isSet(path));
    }

    @Override
    public String getCurrentPath() {
        return read(delegate::getCurrentPath);
    }

    @Override
    public String getName() {
        return read(delegate::getName);
    }

    @Override
    public JsonConfiguration getRoot() {
        return this;
    }

    @Override
    public JsonSection getParent() {
        return read(delegate::getParent);
    }

    @Override
    public JsonSection getConfigurationSection(final String path) {
        return read(() -> {
            final JsonSection section = delegate.getConfigurationSection(path);
            if (section != null) {
                return new ConcurrentJsonSection(section, lock, this);
            }
            return null;
        });
    }

    @Override
    public JsonSection createSection(final String path) {
        lock.writeLock().lock();
        try {
            final JsonSection section = delegate.createSection(path);
            if (section != null) {
                return new ConcurrentJsonSection(section, lock, this);
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Object get(final String path) {
        return read(() -> delegate.get(path));
    }

    @Override
    public Object get(final String path, final Object def) {
        return read(() -> delegate.get(path, def));
    }

    @Override
    public String getString(final String path) {
        return read(() -> delegate.getString(path));
    }

    @Override
    public String getString(final String path, final String def) {
        return read(() -> delegate.getString(path, def));
    }

    @Override
    public int getInt(final String path) {
        return read(() -> delegate.getInt(path));
    }

    @Override
    public int getInt(final String path, final int def) {
        return read(() -> delegate.getInt(path, def));
    }

    @Override
    public boolean getBoolean(final String path) {
        return read(() -> delegate.getBoolean(path));
    }

    @Override
    public boolean getBoolean(final String path, final boolean def) {
        return read(() -> delegate.getBoolean(path, def));
    }

    @Override
    public double getDouble(final String path) {
        return read(() -> delegate.getDouble(path));
    }

    @Override
    public double getDouble(final String path, final double def) {
        return read(() -> delegate.getDouble(path, def));
    }

    @Override
    public long getLong(final String path) {
        return read(() -> delegate.getLong(path));
    }

    @Override
    public long getLong(final String path, final long def) {
        return read(() -> delegate.getLong(path, def));
    }

    @Override
    public BigInteger getBigInteger(final String path) {
        return read(() -> delegate.getBigInteger(path));
    }

    @Override
    public BigInteger getBigInteger(final String path, final BigInteger def) {
        return read(() -> delegate.getBigInteger(path, def));
    }

    @Override
    public BigDecimal getBigDecimal(final String path) {
        return read(() -> delegate.getBigDecimal(path));
    }

    @Override
    public BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        return read(() -> delegate.getBigDecimal(path, def));
    }

    @Override
    public List<?> getList(final String path) {
        return read(() -> delegate.getList(path));
    }

    @Override
    public List<?> getList(final String path, final List<?> def) {
        return read(() -> delegate.getList(path, def));
    }

    @Override
    public List<String> getStringList(final String path) {
        return read(() -> delegate.getStringList(path));
    }

    @Override
    public List<Integer> getIntegerList(final String path) {
        return read(() -> delegate.getIntegerList(path));
    }

    @Override
    public void set(final String path, final Object value) {
        write(() -> delegate.set(path, value));
    }

    @Override
    public void addDefault(final String path, final Object value) {
        write(() -> delegate.addDefault(path, value));
    }

    @Override
    public boolean isString(final String path) {
        return read(() -> delegate.isString(path));
    }

    @Override
    public boolean isInt(final String path) {
        return read(() -> delegate.isInt(path));
    }

    @Override
    public boolean isBoolean(final String path) {
        return read(() -> delegate.isBoolean(path));
    }

    @Override
    public boolean isDouble(final String path) {
        return read(() -> delegate.isDouble(path));
    }

    @Override
    public boolean isLong(final String path) {
        return read(() -> delegate.isLong(path));
    }

    @Override
    public boolean isList(final String path) {
        return read(() -> delegate.isList(path));
    }

    @Override
    public boolean isSection(final String path) {
        return read(() -> delegate.isSection(path));
    }

    @Override
    public JsonNode getNode(final String path) {
        return read(() -> delegate.getNode(path));
    }
}
