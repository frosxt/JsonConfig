package com.github.frosxt.jsonconfig.path;

import com.github.frosxt.jsonconfig.path.syntax.PathTokenizer;

import java.util.List;
import java.util.Objects;

/**
 * Represents a compiled JSON path.
 */
public class JsonPath {
    private final String originalPath;
    private final List<String> segments;

    public JsonPath(final String originalPath, final List<String> segments) {
        this.originalPath = originalPath;
        this.segments = List.copyOf(segments);
    }

    public static JsonPath compile(final String path) {
        return PathTokenizer.tokenize(path);
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public List<String> getSegments() {
        return segments;
    }

    public int size() {
        return segments.size();
    }

    public String getSegment(final int index) {
        return segments.get(index);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final JsonPath jsonPath = (JsonPath) o;
        return Objects.equals(originalPath, jsonPath.originalPath) && Objects.equals(segments, jsonPath.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalPath, segments);
    }

    @Override
    public String toString() {
        return originalPath;
    }
}
