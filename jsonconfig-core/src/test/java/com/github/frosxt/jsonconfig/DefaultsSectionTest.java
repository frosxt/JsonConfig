package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.api.JsonConfigurations;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultsSectionTest {
    @Test
    public void testDefaultsDoNotProvideSections() throws Exception {
        JsonConfiguration root = JsonConfigurations.load(new StringReader("{}"));
        JsonConfiguration defs = JsonConfigurations.load(new StringReader("{\"sec\": {\"val\": 1}}"));
        root.setDefaults(defs);

        // Defaults apply to scalar getters, but not to sections.
        assertNull(root.getConfigurationSection("sec"), "getConfigurationSection should not return defaults");

        Object sec = root.get("sec");
        assertNull(sec, "get() should not return default sections (Option A)");

        // Scalars still work
        assertEquals(1, root.getInt("sec.val"), "Scalars should resolve through defaults");

        // isSet checks local existence only, while contains checks defaults.
        assertTrue(root.contains("sec.val"), "contains should be true for default scalars");
        assertFalse(root.isSet("sec.val"), "isSet should be false for defaults");
    }
}
