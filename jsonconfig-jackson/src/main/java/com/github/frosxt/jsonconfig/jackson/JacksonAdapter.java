package com.github.frosxt.jsonconfig.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

public class JacksonAdapter {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JacksonAdapter() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static com.github.frosxt.jsonconfig.tree.JsonNode toJConfig(final JsonNode node) {
        if (node == null || node.isNull()) {
            return JsonNull.INSTANCE;
        }

        if (node.isBoolean()) {
            return node.asBoolean() ? JsonBoolean.TRUE : JsonBoolean.FALSE;
        }

        if (node.isNumber()) {
            if (node.isIntegralNumber()) {
                if (node.isBigInteger()) {
                    return new JsonNumber(node.bigIntegerValue());
                }
                return new JsonNumber(node.longValue());
            } else {
                if (node.isBigDecimal()) {
                    return new JsonNumber(node.decimalValue());
                }
                return new JsonNumber(node.doubleValue());
            }
        }

        if (node.isTextual()) {
            return new JsonString(node.asText());
        }

        if (node.isArray()) {
            final JsonArray arr = new JsonArray();
            for (final JsonNode child : node) {
                arr.add(toJConfig(child));
            }
            return arr;
        }

        if (node.isObject()) {
            final JsonObject obj = new JsonObject();
            final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                obj.add(field.getKey(), toJConfig(field.getValue()));
            }
            return obj;
        }

        return JsonNull.INSTANCE;
    }

    public static JsonNode toJackson(final com.github.frosxt.jsonconfig.tree.JsonNode node) {
        if (node == null || node instanceof JsonNull) {
            return mapper.createObjectNode().nullNode();
        }

        if (node instanceof JsonBoolean) {
            return BooleanNode.valueOf(((JsonBoolean) node).getValue());
        }

        if (node instanceof JsonString) {
            return TextNode.valueOf(((JsonString) node).getValue());
        }

        if (node instanceof JsonNumber) {
            final Number num = ((JsonNumber) node).getValue();
            if (num instanceof final BigDecimal bigDecimal) {
                return DecimalNode.valueOf(bigDecimal);
            }
            if (num instanceof final BigInteger bigInteger) {
                return BigIntegerNode.valueOf(bigInteger);
            }
            if (num instanceof final Double d) {
                return DoubleNode.valueOf(d);
            }
            if (num instanceof final Long l) {
                return LongNode.valueOf(l);
            }
            if (num instanceof final Integer integer) {
                return IntNode.valueOf(integer);
            }

            return DecimalNode.valueOf(new BigDecimal(num.toString()));
        }

        if (node instanceof JsonArray) {
            final ArrayNode arr = mapper.createArrayNode();
            for (final com.github.frosxt.jsonconfig.tree.JsonNode child : (JsonArray) node) {
                arr.add(toJackson(child));
            }
            return arr;
        }

        if (node instanceof JsonObject) {
            final ObjectNode obj = mapper.createObjectNode();
            for (final Map.Entry<String, com.github.frosxt.jsonconfig.tree.JsonNode> entry : ((JsonObject) node)
                    .entrySet()) {
                obj.set(entry.getKey(), toJackson(entry.getValue()));
            }
            return obj;
        }

        return mapper.createObjectNode().nullNode();
    }
}
