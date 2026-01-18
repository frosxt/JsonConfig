package com.github.frosxt.jsonconfig.path.syntax;

import com.github.frosxt.jsonconfig.path.JsonPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes path strings into segments.
 */
public class PathTokenizer {

    private PathTokenizer() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static JsonPath tokenize(final String path) {
        if (path == null || path.isEmpty()) {
            return new JsonPath(path, List.of());
        }

        final List<String> segments = new ArrayList<>();
        final StringBuilder buffer = new StringBuilder();
        boolean inQuote = false;
        boolean inBracket = false;
        boolean escaped = false;

        for (int i = 0; i < path.length(); i++) {
            final char c = path.charAt(i);

            if (escaped) {
                buffer.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\' && !inBracket) {
                escaped = true;
                continue;
            }

            if (inQuote) {
                if (c == '"') {
                    inQuote = false;
                } else {
                    buffer.append(c);
                }
            } else {
                if (c == '"' && inBracket) {
                    inQuote = true;
                } else if (c == '.') {
                    if (!inBracket) {
                        if (!buffer.isEmpty()) {
                            segments.add(buffer.toString());
                            buffer.setLength(0);
                        }
                    } else {
                        buffer.append(c);
                    }
                } else if (c == '[') {
                    if (!buffer.isEmpty()) {
                        segments.add(buffer.toString());
                        buffer.setLength(0);
                    }
                    inBracket = true;
                } else if (c == ']') {
                    if (inBracket) {
                        if (!buffer.isEmpty()) {
                            segments.add(buffer.toString());
                            buffer.setLength(0);
                        }
                        inBracket = false;
                    } else {
                        buffer.append(c);
                    }
                } else {
                    buffer.append(c);
                }
            }
        }

        // Handle trailing escaped char (incomplete escape)
        if (escaped) {
            buffer.append('\\');
        }

        if (!buffer.isEmpty()) {
            segments.add(buffer.toString());
        }

        return new JsonPath(path, segments);
    }
}
