package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.merge.MergeStrategy;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MergeTest {

    @Test
    public void testMergeOverwrite() {
        final JsonConfiguration config1 = new JsonConfigurationImpl();
        config1.set("a", 1);
        config1.set("b", 2);

        final JsonConfiguration config2 = new JsonConfigurationImpl();
        config2.set("a", 10); // Should overwrite
        config2.set("c", 3);

        config1.merge(config2, MergeStrategy.OVERWRITE);

        assertEquals(10, config1.getInt("a"));
        assertEquals(2, config1.getInt("b"));
        assertEquals(3, config1.getInt("c"));
    }

    @Test
    public void testDeepMerge() {
        final JsonConfiguration config1 = new JsonConfigurationImpl();
        config1.set("section.a", 1);

        final JsonConfiguration config2 = new JsonConfigurationImpl();
        config2.set("section.b", 2);
        config2.set("other", 3);

        config1.merge(config2, MergeStrategy.DEEP_MERGE_OBJECTS);

        assertEquals(1, config1.getInt("section.a"));
        assertEquals(2, config1.getInt("section.b"));
        assertEquals(3, config1.getInt("other"));
    }

    @Test
    public void testConcatArrays() {
        final JsonConfiguration config1 = new JsonConfigurationImpl();
        config1.set("list", java.util.List.of("a"));

        final JsonConfiguration config2 = new JsonConfigurationImpl();
        config2.set("list", java.util.List.of("b"));

        config1.merge(config2, MergeStrategy.CONCAT_ARRAYS);

        final java.util.List<String> list = config1.getStringList("list");
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }
}
