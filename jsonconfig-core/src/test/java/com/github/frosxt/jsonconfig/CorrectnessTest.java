package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrectnessTest {

    @Test
    public void testSetArrayElement() {
        final JsonConfiguration config = new JsonConfigurationImpl();

        // Test setting array element
        config.set("list[0]", "foo");
        config.set("list[1]", "bar");

        final List<String> list = config.getStringList("list");
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("bar", list.get(1));
    }

    @Test
    public void testDeepSetInArray() {
        final JsonConfiguration config = new JsonConfigurationImpl();

        // Test setting deep object inside array
        config.set("list[0].name", "John");
        config.set("list[0].age", 30);

        assertEquals("John", config.getString("list[0].name"));
        assertEquals(30, config.getInt("list[0].age"));
    }
}
