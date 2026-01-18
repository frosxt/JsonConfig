package com.github.frosxt.jsonconfig.runtime.access;

import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.path.cache.PathCache;
import com.github.frosxt.jsonconfig.path.eval.PathResolver;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;

public final class PathAccess {

    private PathAccess() {
    }

    public static JsonNode getNode(final JsonObject root, final String path, final JsonOptions options) {
        if (root == null || path == null || path.isEmpty()) {
            return null;
        }

        final JsonOptions.PathCacheMode mode = (options != null) ? options.pathCacheMode() : JsonOptions.PathCacheMode.NONE;

        return PathResolver.get(root, PathCache.get(path, mode));
    }
}
