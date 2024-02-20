package net.royalur.lut;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.royalur.model.GameSettings;
import net.royalur.model.dice.Roll;
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
public class LutMetadata<R extends Roll> {

    private static final String AUTHOR_KEY = "author";
    private static final String GAME_SETTINGS_KEY = "game_settings";
    private static final Set<String> RESERVED_KEYS = Set.of(AUTHOR_KEY, GAME_SETTINGS_KEY);

    private final String author;
    private final GameSettings<R> gameSettings;
    private final Map<String, JsonNode> additionalMetadata;

    public LutMetadata(
            String author,
            GameSettings<R> gameSettings,
            Map<String, JsonNode> additionalMetadata
    ) {
        this.author = author;
        this.gameSettings = gameSettings;
        this.additionalMetadata = new HashMap<>(additionalMetadata);
    }

    public LutMetadata(
            String author,
            GameSettings<R> gameSettings
    ) {
        this(author, gameSettings, Collections.emptyMap());
    }

    public String getAuthor() {
        return author;
    }

    public GameSettings<R> getGameSettings() {
        return gameSettings;
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

    public String encode(JsonNotation<?, ?, R> notation) {
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
            JsonNotation<?, ?, R> notation,
            JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(AUTHOR_KEY, author);
        notation.writeGameSettings(generator, gameSettings);

        for (Map.Entry<String, JsonNode> entry : additionalMetadata.entrySet()) {
            generator.writeObjectFieldStart(entry.getKey());
            notation.getObjectMapper().writeTree(generator, entry.getValue());
        }
    }

    public static <R extends Roll> LutMetadata<R> decode(
            JsonNotation<?, ?, R> notation,
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

    public static <R extends Roll> LutMetadata<R> read(
            JsonNotation<?, ?, R> notation,
            ObjectNode json
    ) {
        String author = JsonHelper.readString(json, AUTHOR_KEY);

        ObjectNode gameSettingsJson = JsonHelper.readDict(json, GAME_SETTINGS_KEY);
        GameSettings<R> gameSettings = notation.readGameSettings(gameSettingsJson);

        Map<String, JsonNode> additionalMetadata = new HashMap<>();
        for (Map.Entry<String, JsonNode> entry : json.properties()) {
            if (entry.getKey().equals(AUTHOR_KEY))
                continue;
            if (entry.getKey().equals(GAME_SETTINGS_KEY))
                continue;

            additionalMetadata.put(entry.getKey(), entry.getValue());
        }
        return new LutMetadata<>(author, gameSettings, additionalMetadata);
    }
}
