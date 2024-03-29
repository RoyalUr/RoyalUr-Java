package net.royalur.notation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.Instant;

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

    public static @Nullable JsonNode readNullableValue(ObjectNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull())
            return null;

        return node;
    }

    public static JsonNode readValue(ObjectNode json, String key) {
        if (!json.has(key))
            throw new JsonKeyError("Missing " + key);

        return json.get(key);
    }

    public static ObjectNode checkedToObject(JsonNode value, String key) {
        if (!(value instanceof ObjectNode)) {
            throw new JsonTypeError(
                    "Expected " + key + " to be an object, not "
                            + value.getNodeType().name().toLowerCase()
            );
        }
        return (ObjectNode) value;
    }

    public static ObjectNode readObject(ObjectNode json, String key) {
        return checkedToObject(readValue(json, key), key);
    }

    public static @Nullable ObjectNode readNullableObject(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToObject(value, key) : null);
    }

    public static ArrayNode checkedToArray(JsonNode value, String key) {
        if (!(value instanceof ArrayNode)) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a dictionary, not "
                            + value.getNodeType().name().toLowerCase()
            );
        }
        return (ArrayNode) value;
    }

    public static ArrayNode readArray(ObjectNode json, String key) {
        return checkedToArray(readValue(json, key), key);
    }

    public static @Nullable ArrayNode readNullableArray(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToArray(value, key) : null);
    }

    public static JsonNode readArrayEntry(ArrayNode json, int index) {
        if (!json.has(index))
            throw new JsonKeyError("Missing " + index + ". Array has only " + json.size() + " entries");

        return json.get(index);
    }

    public static ObjectNode readArrayObjectEntry(ArrayNode json, int index) {
        return checkedToObject(readArrayEntry(json, index), "the " + index + "'th entry");
    }

    public static String checkedToString(JsonNode value, String key) {
        if (!value.isTextual()) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a string, not "
                            + value.getNodeType().name().toLowerCase()
            );
        }
        return value.textValue();
    }

    public static String readString(ObjectNode json, String key) {
        return checkedToString(readValue(json, key), key);
    }

    public static @Nullable String readNullableString(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToString(value, key) : null);
    }

    public static String readStringWithDefault(ObjectNode json, String key, String defaultValue) {
        String value = readNullableString(json, key);
        return (value != null ? value : defaultValue);
    }

    public static char checkedToChar(String value, String key) {
        if (value.length() != 1) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a single character, but it was "
                            + value.length() + " characters"
            );
        }
        return value.charAt(0);
    }

    public static char readChar(ObjectNode json, String key) {
        return checkedToChar(readString(json, key), key);
    }

    public static @Nullable Character readNullableChar(ObjectNode json, String key) {
        String value = readNullableString(json, key);
        return (value != null ? checkedToChar(value, key) : null);
    }

    public static char readCharWithDefault(ObjectNode json, String key, char defaultValue) {
        Character value = readNullableChar(json, key);
        return (value != null ? value : defaultValue);
    }

    public static JsonNode checkedToNumber(JsonNode value, String key) {
        if (!value.isNumber()) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a number, not "
                            + value.getNodeType().name().toLowerCase()
            );
        }
        return value;
    }

    public static JsonNode readNumber(ObjectNode json, String key) {
        return checkedToNumber(readValue(json, key), key);
    }

    public static @Nullable JsonNode readNullableNumber(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToNumber(value, key) : null);
    }

    public static int checkedToInt(JsonNode value, String key) {
        value = checkedToNumber(value, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToInt())
            throw new JsonTypeError("Expected " + key + " to be representable as an int");

        return value.intValue();
    }

    public static int readInt(ObjectNode json, String key) {
        return checkedToInt(readValue(json, key), key);
    }

    public static @Nullable Integer readNullableInt(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToInt(value, key) : null);
    }

    public static int readIntWithDefault(ObjectNode json, String key, int defaultValue) {
        Integer value = readNullableInt(json, key);
        return (value != null ? value : defaultValue);
    }

    public static long checkedToLong(JsonNode value, String key) {
        value = checkedToNumber(value, key);
        if (value.isFloatingPointNumber())
            throw new JsonTypeError("Expected " + key + " to be an integer");
        if (!value.canConvertToLong())
            throw new JsonTypeError("Expected " + key + " to be representable as a long");

        return value.longValue();
    }

    public static long readLong(ObjectNode json, String key) {
        return checkedToLong(readValue(json, key), key);
    }

    public static @Nullable Long readNullableLong(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToLong(value, key) : null);
    }

    public static long readLongWithDefault(ObjectNode json, String key, long defaultValue) {
        Long value = readNullableLong(json, key);
        return (value != null ? value : defaultValue);
    }

    public static float checkedToFloat(JsonNode value, String key) {
        value = checkedToNumber(value, key);
        float valueFloat = value.floatValue();
        if (value.isFloat())
            return valueFloat;

        double valueDouble = value.doubleValue();
        double epsilon = valueDouble - valueFloat;
        if (Math.abs(epsilon) > MAX_FLOAT_READ_EPSILON) {
            throw new JsonTypeError(
                    "Expected " + key + " to be representable as a float, "
                            + "but could not represent " + value.asText()
                            + " accurately enough"
            );
        }
        return valueFloat;
    }

    public static float readFloat(ObjectNode json, String key) {
        return checkedToFloat(readValue(json, key), key);
    }

    public static @Nullable Float readNullableFloat(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToFloat(value, key) : null);
    }

    public static float readFloatWithDefault(ObjectNode json, String key, float defaultValue) {
        Float value = readNullableFloat(json, key);
        return (value != null ? value : defaultValue);
    }

    public static double checkedToDouble(JsonNode value, String key) {
        value = checkedToNumber(value, key);
        double valueDouble = value.doubleValue();
        if (value.isDouble() || value.isFloat() || value.isInt())
            return valueDouble;

        BigDecimal valueBigDecimal = value.decimalValue();
        BigDecimal epsilon = valueBigDecimal.subtract(new BigDecimal(valueDouble));
        if (epsilon.abs().compareTo(MAX_DOUBLE_READ_EPSILON) > 0) {
            throw new JsonTypeError(
                    "Expected " + key + " to be representable as a double, "
                            + "but could not represent " + value.asText()
                            + " accurately enough"
            );
        }
        return valueDouble;
    }

    public static double readDouble(ObjectNode json, String key) {
        return checkedToDouble(readValue(json, key), key);
    }

    public static @Nullable Double readNullableDouble(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToDouble(value, key) : null);
    }

    public static double readDoubleWithDefault(ObjectNode json, String key, double defaultValue) {
        Double value = readNullableDouble(json, key);
        return (value != null ? value : defaultValue);
    }

    public static boolean checkedToBool(JsonNode value, String key) {
        if (!(value instanceof BooleanNode)) {
            throw new JsonTypeError(
                    "Expected " + key + " to be a boolean, not "
                            + value.getNodeType().name().toLowerCase()
            );
        }
        return value.booleanValue();
    }

    public static boolean readBool(ObjectNode json, String key) {
        return checkedToBool(readValue(json, key), key);
    }

    public static @Nullable Boolean readNullableBool(ObjectNode json, String key) {
        JsonNode value = readNullableValue(json, key);
        return (value != null ? checkedToBool(value, key) : null);
    }

    public static boolean readBoolWithDefault(ObjectNode json, String key, boolean defaultValue) {
        Boolean value = readNullableBool(json, key);
        return (value != null ? value : defaultValue);
    }

    public static Instant parseDate(String value) {
        return Instant.parse(value);
    }

    public static String encodeDate(Instant instant) {
        return instant.toString();
    }

    public static @Nullable String encodeNullableDate(@Nullable Instant instant) {
        return (instant != null ? encodeDate(instant) : null);
    }

    /**
     * Reads an ISO date string.
     */
    public static Instant readDate(ObjectNode json, String key) {
        return parseDate(readString(json, key));
    }

    /**
     * Reads an ISO date string.
     */
    public static @Nullable Instant readNullableDate(ObjectNode json, String key) {
        String value = readNullableString(json, key);
        return (value != null ? parseDate(value) : null);
    }

    /**
     * Reads an ISO date string.
     */
    public static Instant readDateWithDefault(ObjectNode json, String key, Instant defaultValue) {
        Instant value = readNullableDate(json, key);
        return (value != null ? value : defaultValue);
    }
}
