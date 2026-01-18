package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonConfigurations;
import com.github.frosxt.jsonconfig.api.JsonSection;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class ParentChainTest {
    @Test
    public void testStructuralParent() throws Exception {
        String json = "{\"a\": {\"b\": {\"c\": 1}}}";
        JsonConfiguration root = JsonConfigurations.load(new StringReader(json));

        JsonSection b = root.getConfigurationSection("a.b");
        assertNotNull(b, "Section a.b should exist");

        JsonSection parent = b.getParent();
        assertNotNull(parent, "Parent should not be null");
        assertEquals("a", parent.getName(), "Parent name should be 'a' (structural parent)");
        assertEquals("a", parent.getCurrentPath(), "Parent path should be 'a'");

        assertEquals(root, parent.getParent(), "Parent's parent should be root");
    }
}
