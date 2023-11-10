package net.royalur.notation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contains type checking helper methods for retrieving JSON values,
 * as Jackson doesn't support that for some reason.
 */
public class JsonHelper {

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
        if (value.numberType() != JsonParser.NumberType.INT) {
            throw new JsonTypeError(
                "Expected " + key + " to be an integer, " +
                "not " + value.numberType().name().toLowerCase()
            );
        }
        return value.intValue();
    }

    public long readLong(
            @Nonnull ObjectNode json,
            @Nonnull String key
    ) {
        JsonNode value = readNumber(json, key);
        JsonParser.NumberType numberType = value.numberType();
        if (numberType != JsonParser.NumberType.INT && numberType != JsonParser.NumberType.LONG) {
            throw new JsonTypeError(
                    "Expected " + key + " to be an integer or a long, " +
                            "not " + value.numberType().name().toLowerCase()
            );
        }
        return value.longValue();
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
