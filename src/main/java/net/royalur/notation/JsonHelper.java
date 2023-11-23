package net.royalur.notation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Contains type checking helper methods for retrieving JSON values,
 * as Jackson doesn't support that for some reason.
 */
public class JsonHelper {

    public static final double MAX_FLOAT_READ_EPSILON = 0.01;
    public static final BigDecimal MAX_DOUBLE_READ_EPSILON = new BigDecimal("0.01");

    public static class JsonReadError extends RuntimeException {
        public JsonReadError(@Nonnull String message) {
            super(message);
        }
    }

    public static class JsonKeyError extends JsonReadError {
        public JsonKeyError(@Nonnull String message) {
            super(message);
        }
    }

    public static class JsonTypeError extends JsonReadError {
        public JsonTypeError(@Nonnull String message) {
            super(message);
        }
    }

    public @Nonnull JsonNode readValue(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        if (!json.has(key))
            throw new JsonKeyError("Missing " + key);

        return json.get(key);
    }

    public @Nonnull ObjectNode readDict(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a dictionary, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public @Nullable ObjectNode readNullableDict(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (value.isNull())
            return null;

        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a dictionary, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public @Nonnull ArrayNode readArray(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (!(value instanceof ArrayNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a dictionary, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ArrayNode) value;
    }

    public @Nullable ArrayNode readNullableArray(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (value.isNull())
            return null;

        if (!(value instanceof ArrayNode)) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a dictionary, " +
                            "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ArrayNode) value;
    }

    public @Nonnull JsonNode readArrayEntry(
            @Nonnull ArrayNode json,
            int index
    ) {
        if (!json.has(index))
            throw new JsonKeyError("Missing " + index + ". Array has only " + json.size() + " entries");

        return json.get(index);
    }

    public @Nonnull ObjectNode readArrayDictEntry(
            @Nonnull ArrayNode json,
            int index
    ) {
        JsonNode value = readArrayEntry(json, index);
        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                    "Expected the " + index + "'th entry to be a dictionary, " +
                            "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public @Nonnull String readString(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (!value.isTextual()) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a string, " +
                    "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value.textValue();
    }

    public @Nullable String readNullableString(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (value.isNull())
            return null;

        if (!value.isTextual()) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a string, " +
                            "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value.textValue();
    }

    public char readChar(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        String value = readString(json, key);
        if (value.length() != 1) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a single character, " +
                    "but it was " + value.length() + " characters"
            );
        }
        return value.charAt(0);
    }

    public @Nonnull JsonNode readNumber(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (!value.isNumber()) {
            throw new JsonTypeError(
                "Expected " + key + " to be a number, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value;
    }

    public int readInt(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readNumber(json, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToInt())
            throw new JsonTypeError("Expected " + key + " to be representable as an int");

        return value.intValue();
    }

    public long readLong(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readNumber(json, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToLong())
            throw new JsonTypeError("Expected " + key + " to be representable as a long");

        return value.longValue();
    }

    public float readFloat(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readNumber(json, key);
        float valueFloat = value.floatValue();
        if (value.isFloat())
            return valueFloat;

        double valueDouble = value.doubleValue();
        double epsilon = valueDouble - valueFloat;
        if (Math.abs(epsilon) > MAX_FLOAT_READ_EPSILON) {
            throw new JsonTypeError(
                    "Expected " + key + " to be representable as a float, " +
                    "but could not represent " + value.asText() + " accurately enough"
            );
        }
        return valueFloat;
    }

    public double readDouble(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readNumber(json, key);
        double valueDouble = value.doubleValue();
        if (value.isDouble() || value.isFloat() || value.isInt())
            return valueDouble;

        BigDecimal valueBigDecimal = value.decimalValue();
        BigDecimal epsilon = valueBigDecimal.subtract(new BigDecimal(valueDouble));
        if (epsilon.abs().compareTo(MAX_DOUBLE_READ_EPSILON) > 0) {
            throw new JsonTypeError(
                    "Expected " + key + " to be representable as a double, " +
                    "but could not represent " + value.asText() + " accurately enough"
            );
        }
        return valueDouble;
    }

    public boolean readBool(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readValue(json, key);
        if (!(value instanceof BooleanNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a boolean, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value.booleanValue();
    }
}
