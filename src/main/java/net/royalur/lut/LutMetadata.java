package net.royalur.lut;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.royalur.lut.buffer.ValueType;
import net.royalur.model.GameSettings;
import net.royalur.notation.JsonHelper;
import net.royalur.notation.JsonNotation;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds the metadata stored for a LUT.
 */
public class LutMetadata {

    private static final String GAME_SETTINGS_KEY = "game_settings";
    private static final String VALUE_TYPE_KEY = "value_type";
    private static final Set<String> RESERVED_KEYS = Set.of(GAME_SETTINGS_KEY, VALUE_TYPE_KEY);

    private final GameSettings gameSettings;
    private final ValueType valueType;
    private final Map<String, JsonNode> additionalMetadata;

    public LutMetadata(
            GameSettings gameSettings,
            ValueType valueType,
            Map<String, JsonNode> additionalMetadata
    ) {
        this.gameSettings = gameSettings;
        this.valueType = valueType;
        this.additionalMetadata = new HashMap<>(additionalMetadata);
    }

    public LutMetadata(GameSettings gameSettings, ValueType valueType) {
        this(gameSettings, valueType, Collections.emptyMap());
    }

    public LutMetadata copy() {
        return new LutMetadata(
                gameSettings, valueType,
                new HashMap<>(additionalMetadata)
        );
    }

    public LutMetadata copyWithValueType(ValueType valueType) {
        return new LutMetadata(
                gameSettings, valueType,
                new HashMap<>(additionalMetadata)
        );
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Map<String, JsonNode> getAdditionalMetadata() {
        return additionalMetadata;
    }

    public void addMetadata(String key, JsonNode value) {
        if (RESERVED_KEYS.contains(key))
            throw new IllegalArgumentException("\"" + key + "\" is a reserved key");

        additionalMetadata.put(key, value);
    }

    public void addMetadata(String key, String value) {
        addMetadata(key, new TextNode(value));
    }

    public void addMetadata(String key, double value) {
        addMetadata(key, new DoubleNode(value));
    }

    public String encode(JsonNotation notation) {
        Writer writer = new StringWriter();
        try (JsonGenerator generator = notation.getJsonFactory().createGenerator(writer)) {

            generator.writeStartObject();
            try {
                write(notation, generator);
            } finally {
                generator.writeEndObject();
                generator.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON", e);
        }
        return writer.toString();
    }

    public void write(
            JsonNotation notation,
            JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(VALUE_TYPE_KEY, this.valueType.getTextID());
        for (Map.Entry<String, JsonNode> entry : additionalMetadata.entrySet()) {
            generator.writeFieldName(entry.getKey());
            notation.getObjectMapper().writeTree(generator, entry.getValue());
        }

        generator.writeObjectFieldStart(GAME_SETTINGS_KEY);
        try {
            notation.writeGameSettings(generator, gameSettings);
        } finally {
            generator.writeEndObject();
        }
    }

    public static LutMetadata decode(
            JsonNotation notation,
            String encoded
    ) {
        try (JsonParser parser = notation.getJsonFactory().createParser(encoded)) {

            JsonNode json = notation.getObjectMapper().readTree(parser);
            if (!(json instanceof ObjectNode objectNode)) {
                throw new JsonHelper.JsonReadError(
                        "Expected an object, not a " + json.getNodeType().name()
                );
            }
            return read(notation, objectNode);

        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON", e);
        }
    }

    public static LutMetadata read(
            JsonNotation notation,
            ObjectNode json
    ) {
        ObjectNode gameSettingsJson = JsonHelper.readObject(json, GAME_SETTINGS_KEY);
        GameSettings gameSettings = notation.readGameSettings(gameSettingsJson);
        ValueType valueType = ValueType.getByTextID(JsonHelper.readStringWithDefault(
                json, VALUE_TYPE_KEY, ValueType.PERCENT16.getTextID()
        ));

        Map<String, JsonNode> additionalMetadata = new HashMap<>();
        for (Map.Entry<String, JsonNode> entry : json.properties()) {
            if (RESERVED_KEYS.contains(entry.getKey()))
                continue;

            additionalMetadata.put(entry.getKey(), entry.getValue());
        }
        return new LutMetadata(gameSettings, valueType, additionalMetadata);
    }
}
