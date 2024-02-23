package net.royalur.notation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        public JsonReadError(String message) {
            super(message);
        }
    }

    public static class JsonKeyError extends JsonReadError {
        public JsonKeyError(String message) {
            super(message);
        }
    }

    public static class JsonTypeError extends JsonReadError {
        public JsonTypeError(String message) {
            super(message);
        }
    }

    private JsonHelper() {}

    public static JsonNode readValue(ObjectNode json, String key) {
        if (!json.has(key))
            throw new JsonKeyError("Missing " + key);

        return json.get(key);
    }

    public static ObjectNode readDict(ObjectNode json, String key) {
        JsonNode value = readValue(json, key);
        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a dictionary, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public static @Nullable ObjectNode readNullableDict(ObjectNode json, String key) {
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

    public static ArrayNode readArray(ObjectNode json, String key) {
        JsonNode value = readValue(json, key);
        if (!(value instanceof ArrayNode)) {
            throw new JsonTypeError(
                "Expected " + key + " to be a dictionary, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ArrayNode) value;
    }

    public static @Nullable ArrayNode readNullableArray(ObjectNode json, String key) {
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

    public static JsonNode readArrayEntry(ArrayNode json, int index) {
        if (!json.has(index))
            throw new JsonKeyError("Missing " + index + ". Array has only " + json.size() + " entries");

        return json.get(index);
    }

    public static ObjectNode readArrayDictEntry(ArrayNode json, int index) {
        JsonNode value = readArrayEntry(json, index);
        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                    "Expected the " + index + "'th entry to be a dictionary, " +
                            "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public static String readString(ObjectNode json, String key) {
        JsonNode value = readValue(json, key);
        if (!value.isTextual()) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a string, " +
                    "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value.textValue();
    }

    public static @Nullable String readNullableString(ObjectNode json, String key) {
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

    public static char readChar(ObjectNode json, String key) {
        String value = readString(json, key);
        if (value.length() != 1) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a single character, " +
                    "but it was " + value.length() + " characters"
            );
        }
        return value.charAt(0);
    }

    public static JsonNode readNumber(ObjectNode json, String key) {
        JsonNode value = readValue(json, key);
        if (!value.isNumber()) {
            throw new JsonTypeError(
                "Expected " + key + " to be a number, " +
                "not " + value.getNodeType().name().toLowerCase()
            );
        }
        return value;
    }

    public static int readInt(ObjectNode json, String key) {
        JsonNode value = readNumber(json, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToInt())
            throw new JsonTypeError("Expected " + key + " to be representable as an int");

        return value.intValue();
    }

    public static long readLong(ObjectNode json, String key) {
        JsonNode value = readNumber(json, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToLong())
            throw new JsonTypeError("Expected " + key + " to be representable as a long");

        return value.longValue();
    }

    public static float readFloat(ObjectNode json, String key) {
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

    public static double readDouble(ObjectNode json, String key) {
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

    public static boolean readBool(ObjectNode json, String key) {
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
