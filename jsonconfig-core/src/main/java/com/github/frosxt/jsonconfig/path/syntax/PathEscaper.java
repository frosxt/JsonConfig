package com.github.frosxt.jsonconfig.path.syntax;

/**
 * Utility for escaping path segments to allow round-trip usage of keys
 * containing special characters.
 */
public final class PathEscaper {

    private PathEscaper() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Escapes a path segment for use in dot notation.
     * Escapes: backslash, dot, and brackets.
     * 
     * @param segment the raw key name
     * @return escaped segment safe for dot notation paths
     */
    public static String escapeSegment(final String segment) {
        if (segment == null) {
            return null;
        }

        boolean needsEscape = false;
        for (int i = 0; i < segment.length() && !needsEscape; i++) {
            final char c = segment.charAt(i);
            needsEscape = (c == '\\' || c == '.' || c == '[' || c == ']');
        }
        if (!needsEscape) {
            return segment;
        }

        final StringBuilder sb = new StringBuilder(segment.length() + 4);
        for (int i = 0; i < segment.length(); i++) {
            final char c = segment.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '.' -> sb.append("\\.");
                case '[' -> sb.append("\\[");
                case ']' -> sb.append("\\]");
                default -> sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Builds a full path from a prefix and a key, escaping the key.
     * 
     * @param prefix current path prefix (empty string for root)
     * @param key    the key to append
     * @return combined path with escaped key
     */
    public static String buildPath(final String prefix, final String key) {
        final String escaped = escapeSegment(key);
        if (prefix == null || prefix.isEmpty()) {
            return escaped;
        }

        return prefix + "." + escaped;
    }
}
