package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonConfigurations;
import com.github.frosxt.jsonconfig.api.JsonSection;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class CoreVerificationTest {

    @Test
    public void testParseAndAccess() throws Exception {
        final String json = "{\n" +
                "  \"server\": {\n" +
                "    \"port\": 8080,\n" +
                "    \"name\": \"TestServer\"\n" +
                "  },\n" +
                "  \"users\": [ \"Alice\", \"Bob\" ],\n" +
                "  \"features\": {\n" +
                "    \"enabled\": true\n" +
                "  }\n" +
                "}";

        final JsonConfiguration config = JsonConfigurations.load(new StringReader(json));

        assertEquals(8080, config.getInt("server.port"));
        assertEquals("TestServer", config.getString("server.name"));
        assertTrue(config.getBoolean("features.enabled"));
        assertEquals(2, config.getStringList("users").size());
        assertEquals("Alice", config.getStringList("users").get(0));

        // Test section
        final JsonSection server = config.getConfigurationSection("server");
        assertNotNull(server);
        assertEquals(8080, server.getInt("port"));

        // Test missing
        assertNull(config.getString("missing"));
        assertEquals("default", config.getString("missing", "default"));
    }

    @Test
    public void testMutationAndDeepSet() throws Exception {
        final JsonConfiguration config = new JsonConfigurationImpl();

        config.set("a.b.c", 100);
        assertEquals(100, config.getInt("a.b.c"));

        assertTrue(config.isSection("a"));
        assertTrue(config.isSection("a.b"));

        final String saved = config.saveToString();
        assertTrue(saved.contains("\"c\": 100")); // Default is pretty print
    }

    @Test
    public void testDefaults() throws Exception {
        final JsonConfiguration config = new JsonConfigurationImpl();
        final JsonConfiguration defaults = new JsonConfigurationImpl();
        defaults.set("defaultKey", "defaultValue");

        config.setDefaults(defaults);

        assertEquals("defaultValue", config.getString("defaultKey"));
        assertNull(config.getString("defaultKeyAA")); // Should be null
    }

    @Test
    public void testPrettyPrint() throws Exception {
        final JsonConfiguration config = new JsonConfigurationImpl();
        config.options().indent(2);
        config.set("a", 1);

        final String saved = config.saveToString();
        assertTrue(saved.contains("  \"a\": 1")); // Check indentation
    }
}
