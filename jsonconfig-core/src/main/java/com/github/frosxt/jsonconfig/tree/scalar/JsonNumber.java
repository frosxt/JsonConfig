package com.github.frosxt.jsonconfig.tree.scalar;

import com.github.frosxt.jsonconfig.tree.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Represents a JSON number value.
 * Numbers are normalized to BigInteger (integral) or BigDecimal (decimal) for
 * exactness.
 */
public class JsonNumber extends JsonNode {
    private final Number value;

    public JsonNumber(final Number value) {
        if (value == null) {
            throw new IllegalArgumentException("JsonNumber value cannot be null");
        }
        this.value = normalize(value);
    }

    private static Number normalize(final Number value) {
        if (value instanceof BigInteger || value instanceof BigDecimal) {
            return value;
        }
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return BigInteger.valueOf(value.longValue());
        }

        return new BigDecimal(value.toString());
    }

    public Number getValue() {
        return value;
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public BigInteger bigIntegerValue() {
        if (value instanceof final BigInteger bigInteger) {
            return bigInteger;
        }
        if (value instanceof final BigDecimal bigDecimal) {
            return bigDecimal.toBigInteger();
        }

        return BigInteger.valueOf(value.longValue());
    }

    public BigDecimal bigDecimalValue() {
        if (value instanceof final BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof final BigInteger bigInteger) {
            return new BigDecimal(bigInteger);
        }

        return BigDecimal.valueOf(value.doubleValue());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsonNumber that = (JsonNumber) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
