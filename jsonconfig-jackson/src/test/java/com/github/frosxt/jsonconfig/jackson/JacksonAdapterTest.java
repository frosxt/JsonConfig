package com.github.frosxt.jsonconfig.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JacksonAdapterTest {

    @Test
    public void testJacksonAdapter() {
        // Round trip: JConfig -> Jackson -> JConfig
        final JsonObject org = new JsonObject();
        org.add("key", new JsonString("value"));
        org.add("bool", JsonBoolean.TRUE);

        final JsonNode jackson = JacksonAdapter.toJackson(org);
        assertTrue(jackson.isObject());

        final com.github.frosxt.jsonconfig.tree.JsonNode back = JacksonAdapter.toJConfig(jackson);
        assertTrue(back instanceof JsonObject);

        final JsonObject obj = (JsonObject) back;
        assertEquals("value", ((JsonString) obj.get("key")).getValue());
        assertEquals(true, ((JsonBoolean) obj.get("bool")).getValue());
    }
}
