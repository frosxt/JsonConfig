package com.github.frosxt.jsonconfig.gson;

import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GsonAdapterTest {

    @Test
    public void testGsonAdapter() {
        // Round trip: JConfig -> Gson -> JConfig
        final JsonObject org = new JsonObject();
        org.add("key", new JsonString("value"));
        org.add("num", new JsonNumber(123));

        final JsonElement gson = GsonAdapter.toGson(org);
        assertTrue(gson.isJsonObject());

        final JsonNode back = GsonAdapter.toJConfig(gson);
        assertTrue(back instanceof JsonObject);

        final JsonObject obj = (JsonObject) back;
        assertEquals("value", ((JsonString) obj.get("key")).getValue());
        assertEquals(123, ((JsonNumber) obj.get("num")).intValue());
    }
}
