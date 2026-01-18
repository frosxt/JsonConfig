package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.api.JsonSection;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Spigot compliance: escaped dots, empty paths, and null handling.
 */
public class SpigotComplianceTest {

    @Test
    public void testEscapedDotKeys() {
        final JsonConfiguration config = new JsonConfigurationImpl();

        // Set a key with escaped dot
        config.set("a\\.b.c", 1);

        assertEquals(1, config.getInt("a\\.b.c"));

        // Verify structure: "a.b" is a single key, "c" is nested
        final JsonSection section = config.getConfigurationSection("a\\.b");
        assertNotNull(section);
        assertEquals(1, section.getInt("c"));
    }

    @Test
    public void testEmptyPathSemantics() {
        final JsonConfiguration config = new JsonConfigurationImpl();
        config.set("x", 1);

        // getConfigurationSection("") should return this
        assertSame(config, config.getConfigurationSection(""));

        // get("") should return this
        assertSame(config, config.get(""));

        // createSection("") should return this
        assertSame(config, config.createSection(""));
    }

    @Test
    public void testNullHandlingPolicyRemove() {
        final JsonConfiguration config = new JsonConfigurationImpl();
        // Default policy is REMOVE

        config.set("x", "value");
        assertTrue(config.contains("x"));
        assertTrue(config.isSet("x"));

        config.set("x", null);
        assertFalse(config.contains("x"));
        assertFalse(config.isSet("x"));
    }

    @Test
    public void testNullHandlingPolicySetNull() {
        final JsonConfiguration config = new JsonConfigurationImpl();
        config.options().nullHandlingPolicy(JsonOptions.NullHandlingPolicy.SET_NULL);

        config.set("x", "value");
        assertTrue(config.contains("x"));
        assertTrue(config.isSet("x"));

        config.set("x", null);
        // With SET_NULL, key exists but value is null
        assertTrue(config.contains("x")); // Node exists
        assertFalse(config.isSet("x")); // But isSet is false (null value)
    }

    @Test
    public void testContainsWithDefaults() {
        final JsonConfiguration config = new JsonConfigurationImpl();
        final JsonConfiguration defaults = new JsonConfigurationImpl();
        defaults.set("defaultKey", "value");
        config.setDefaults(defaults);

        assertTrue(config.contains("defaultKey"));
        assertFalse(config.isSet("defaultKey")); // Not locally set
    }
}
