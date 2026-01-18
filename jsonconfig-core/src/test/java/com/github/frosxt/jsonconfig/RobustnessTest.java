package com.github.frosxt.jsonconfig;

import com.github.frosxt.jsonconfig.api.JsonConfiguration;
import com.github.frosxt.jsonconfig.runtime.config.JsonConfigurationImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verification for robustness and correctness edge cases.
 */
public class RobustnessTest {

    @Test
    public void testNullPathEnforcement() {
        final JsonConfiguration config = new JsonConfigurationImpl();

        assertThrows(IllegalArgumentException.class, () -> config.get(null));
        assertThrows(IllegalArgumentException.class, () -> config.set(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> config.contains(null));
        assertThrows(IllegalArgumentException.class, () -> config.isSet(null));
        assertThrows(IllegalArgumentException.class, () -> config.createSection(null));
        assertThrows(IllegalArgumentException.class, () -> config.getConfigurationSection(null));
        assertThrows(IllegalArgumentException.class, () -> config.getString(null));
        assertThrows(IllegalArgumentException.class, () -> config.getList(null));
    }
}
