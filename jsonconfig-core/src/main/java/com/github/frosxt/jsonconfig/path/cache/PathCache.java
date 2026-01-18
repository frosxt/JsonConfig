package com.github.frosxt.jsonconfig.path.cache;

import com.github.frosxt.jsonconfig.api.JsonOptions.PathCacheMode;
import com.github.frosxt.jsonconfig.path.JsonPath;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class PathCache {
    private static final int MAX_ENTRIES = 1000;

    private static final Map<String, JsonPath> LRU_CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_ENTRIES, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(final Map.Entry<String, JsonPath> eldest) {
                    return size() > MAX_ENTRIES;
                }
            });

    private static final Map<String, JsonPath> WEAK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private static final ThreadLocal<Map<String, JsonPath>> THREAD_CACHE = ThreadLocal
            .withInitial(() -> new LinkedHashMap<>(MAX_ENTRIES, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(final Map.Entry<String, JsonPath> eldest) {
                    return size() > MAX_ENTRIES;
                }
            });

    private PathCache() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static JsonPath get(final String path, final PathCacheMode mode) {
        if (mode == null || mode == PathCacheMode.NONE) {
            return JsonPath.compile(path);
        }

        return switch (mode) {
            case LRU -> {
                synchronized (LRU_CACHE) {
                    yield LRU_CACHE.computeIfAbsent(path, JsonPath::compile);
                }
            }
            case WEAK -> {
                synchronized (WEAK_CACHE) {
                    yield WEAK_CACHE.computeIfAbsent(path, JsonPath::compile);
                }
            }
            case THREAD_LOCAL -> THREAD_CACHE.get().computeIfAbsent(path, JsonPath::compile);
            default -> JsonPath.compile(path);
        };
    }
}
