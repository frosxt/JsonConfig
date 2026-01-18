package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonConfigurations;
import com.github.frosxt.jsonconfig.api.JsonSection;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class ArraySectionTest {
    @Test
    public void testArraySectionAccess() throws Exception {
        JsonConfiguration root = JsonConfigurations.load(new StringReader("{}"));
        root.set("list[0].name", "John");

        // Verify structure
        assertTrue(root.isSection("list[0]"), "list[0] should be a section (JsonObject)");
        assertNotNull(root.getNode("list[0]"), "Node at list[0] should exist");

        // Verify section retrieval
        JsonSection sec = root.getConfigurationSection("list[0]");
        assertNotNull(sec, "Should be able to get section from array index");
        assertEquals("John", sec.getString("name"));

        // Parent check
        assertEquals(root, sec.getParent(), "Parent of list[0] should be root (nearest section)");

        // Canonical format check
        // "list[0]" is preferred over "list.0" for array elements
        assertEquals("list[0]", sec.getCurrentPath());

        // test get() returning Queryable
        Object val = root.get("list[0]");
        assertTrue(val instanceof JsonSection, "get() should return JsonSection for objects in arrays");
        assertEquals("John", ((JsonSection) val).getString("name"));
    }
}
