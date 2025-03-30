package net.royalur.notation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.path.PathPairFactory;
import net.royalur.model.path.PathType;
import net.royalur.model.shape.BoardShapeFactory;
import net.royalur.model.shape.BoardType;
import net.royalur.rules.RuleSet;
import net.royalur.rules.RuleSetProvider;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.state.*;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

/**
 * A notation that can be used to encode games of the Royal
 * Game of Ur into JSON for serialisation. This notation
 * has been created to be read by machines, not humans.
 */
public class JsonNotation implements Notation {

    /**
     * The latest version of the JSON notation. If any breaking changes
     * are made to the JSON notation, then this field will be updated
     * to reflect that.
     */
    public static final int LATEST_VERSION = 2;

    /**
     * The key in the JSON for the version of the notation.
     */
    public static final String VERSION_KEY = "notation_version";

    /**
     * The key in the JSON for the metadata of the game.
     */
    public static final String GAME_METADATA_KEY = "metadata";

    /**
     * The key in the JSON for the settings of a game.
     */
    public static final String SETTINGS_KEY = "settings";

    /**
     * The key in the JSON for the game setting of the board shape used.
     */
    public static final String BOARD_SHAPE_KEY = "board_shape";

    /**
     * The key in the JSON for the game setting of the paths used.
     */
    public static final String PATHS_KEY = "paths";

    /**
     * The key in the JSON for the game settings of the dice used.
     */
    public static final String DICE_KEY = "dice";

    /**
     * The key in the JSON for game setting of the starting piece count.
     */
    public static final String STARTING_PIECE_COUNT_KEY = "start_pieces";

    /**
     * The key in the JSON for the game setting of whether rosettes are safe.
     */
    public static final String SAFE_ROSETTES_KEY = "safe_rosettes";

    /**
     * The key in the JSON for the game setting of whether rosettes grant extra rolls.
     */
    public static final String ROSETTES_GRANT_EXTRA_ROLLS_KEY = "rosettes_grant_rolls";

    /**
     * The key in the JSON for the game setting of whether rosettes grant extra rolls.
     */
    public static final String CAPTURES_GRANT_EXTRA_ROLLS_KEY = "captures_grant_rolls";

    /**
     * The key in the JSON for the initial state of the board and players.
     */
    public static final String INITIAL_STATE_KEY = "initial_state";

    /**
     * The key in the JSON for the list of states in the game.
     */
    public static final String STATES_KEY = "states";

    /**
     * The key in the JSON for the time this state was reached
     * in milliseconds since the start of the game.
     */
    public static final String STATE_TIME_KEY = "time";

    /**
     * The key in the JSON for the metadata of the game.
     */
    public static final String STATE_METADATA_KEY = "metadata";

    /**
     * The key in the JSON for the type of state.
     */
    public static final String STATE_TYPE_KEY = "type";

    /**
     * Represents states of type {@link RolledGameState}.
     */
    public static final String STATE_TYPE_ROLLED = "roll";

    /**
     * Represents states of type {@link MovedGameState}.
     */
    public static final String STATE_TYPE_MOVED = "move";

    /**
     * Represents states of type {@link WaitingForRollGameState}.
     */
    public static final String STATE_TYPE_WAITING_FOR_ROLL = "wait4roll";

    /**
     * Represents states of type {@link WaitingForMoveGameState}.
     */
    public static final String STATE_TYPE_WAITING_FOR_MOVE = "wait4move";

    /**
     * Represents states of type {@link ResignedGameState}.
     */
    public static final String STATE_TYPE_RESIGNED = "resigned";

    /**
     * Represents states of type {@link AbandonedGameState}.
     */
    public static final String STATE_TYPE_ABANDONED = "abandoned";

    /**
     * Represents states of type {@link EndGameState}.
     */
    public static final String STATE_TYPE_END = "win";

    /**
     * The key in the JSON for the roll that was made.
     */
    public static final String ROLL_KEY = "roll";

    /**
     * The key in the JSON for the value of a roll.
     */
    public static final String ROLL_VALUE_KEY = "value";

    /**
     * The key in the JSON for a move that was made.
     */
    public static final String MOVE_KEY = "move";

    /**
     * The key in the JSON for the source piece of a move.
     */
    public static final String MOVE_SOURCE_KEY = "src";

    /**
     * The key in the JSON for the destination piece of a move.
     */
    public static final String MOVE_DEST_KEY = "dest";

    /**
     * The key in the JSON for the captured piece of a move.
     */
    public static final String MOVE_CAPTURED_KEY = "captured";

    /**
     * The key in the JSON for the moves that are available in a state.
     */
    public static final String AVAILABLE_MOVES_KEY = "moves";

    /**
     * The old key in the JSON for the owner of a piece.
     */
    public static final String OLD_PIECE_OWNER_KEY = "owner";

    /**
     * The old key in the JSON for the index of a piece on its path.
     */
    public static final String OLD_PIECE_INDEX_KEY = "index";

    /**
     * The key in the JSON for the player whose turn it is in a state.
     */
    public static final String TURN_KEY = "turn";

    /**
     * The key in the JSON for the player that abandoned the game.
     */
    public static final String CONTROL_PLAYER_KEY = "player";

    /**
     * The key in the JSON for the reason that a game was abandoned.
     */
    public static final String ABANDONED_REASON_KEY = "reason";

    /**
     * The key in the JSON for the player that won the game.
     */
    public static final String WINNER_KEY = "winner";

    /**
     * The key in the JSON for the state of the board.
     */
    public static final String BOARD_KEY = "board";

    /**
     * The key in the JSON for the pieces on a board.
     */
    public static final String BOARD_PIECES_KEY = "pieces";

    /**
     * The key in the JSON for the state of the players.
     */
    public static final String PLAYERS_KEY = "players";

    /**
     * The key in the JSON for the pieces of a player state.
     */
    public static final String PLAYER_PIECES_KEY = "pieces";

    /**
     * The key in the JSON for the score of a player state.
     */
    public static final String PLAYER_SCORE_KEY = "score";

    private final Map<String, ? extends BoardShapeFactory> boardShapes;
    private final Map<String, ? extends PathPairFactory> pathPairs;
    private final Map<String, ? extends DiceFactory> dice;
    private final RuleSetProvider ruleSetProvider;

    /**
     * A factory to build generators to write the JSON.
     */
    private final JsonFactory jsonFactory;

    /**
     * A mapper to use for parsing JSON into a tree.
     */
    private final ObjectMapper objectMapper;

    /**
     * Instantiates the JSON notation to encode and decode games.
     * @param pathPairs The paths that can be parsed in this notation.
     * @param boardShapes The board shapes that can be parsed in this notation.
     * @param dice The dice that can be parsed in this notation.
     * @param ruleSetProvider The provider to create rule sets from game settings.
     * @param jsonFactory A factory to build generators to write the JSON.
     */
    public JsonNotation(
            Map<String, ? extends BoardShapeFactory> boardShapes,
            Map<String, ? extends PathPairFactory> pathPairs,
            Map<String, ? extends DiceFactory> dice,
            RuleSetProvider ruleSetProvider,
            JsonFactory jsonFactory
    ) {
        this.boardShapes = boardShapes;
        this.pathPairs = pathPairs;
        this.dice = dice;
        this.ruleSetProvider = ruleSetProvider;
        this.jsonFactory = jsonFactory;
        this.objectMapper = new ObjectMapper(jsonFactory);
    }

    /**
     * Instantiates the JSON notation to encode and decode games.
     * @param pathPairs The paths that can be parsed in this notation.
     * @param boardShapes The board shapes that can be parsed in this notation.
     * @param dice The dice that can be parsed in this notation.
     * @param ruleSetProvider The provider to create rule sets from game settings.
     */
    public JsonNotation(
            Map<String, ? extends BoardShapeFactory> boardShapes,
            Map<String, ? extends PathPairFactory> pathPairs,
            Map<String, ? extends DiceFactory> dice,
            RuleSetProvider ruleSetProvider
    ) {
        this(boardShapes, pathPairs, dice, ruleSetProvider, JsonFactory.builder().build());
    }

    /**
     * Instantiates a default JSON notation to read simple games.
     */
    public JsonNotation() {
        this(
                BoardType.PARSING_MAP,
                PathType.PARSING_MAP,
                DiceType.PARSING_MAP,
                new SimpleRuleSetProvider()
        );
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void writeRoll(JsonGenerator generator, Roll roll) throws IOException {
        generator.writeNumberField(ROLL_VALUE_KEY, roll.value());
    }

    public int encodePiece(Piece piece) {
        PlayerType owner = piece.getOwner();
        int sign;
        if (owner == PlayerType.LIGHT) {
            sign = 1;
        } else if (owner == PlayerType.DARK) {
            sign = -1;
        } else {
            throw new IllegalArgumentException("Unknown player type " + owner.getName());
        }
        return sign * (piece.getPathIndex() + 1);
    }

    protected void writePieceField(
            JsonGenerator generator,
            String fieldName,
            @Nullable Piece piece
    ) throws IOException {

        if (piece != null) {
            generator.writeNumberField(fieldName, encodePiece(piece));
        }
    }

    public void writeMove(JsonGenerator generator, Move move) throws IOException {
        Piece sourcePiece = (move.hasSource() ? move.getSourcePiece() : null);
        writePieceField(generator, MOVE_SOURCE_KEY, sourcePiece);

        Piece destPiece = (move.hasDest() ? move.getDestPiece() : null);
        writePieceField(generator, MOVE_DEST_KEY, destPiece);

        Piece capturedPiece = (move.isCapture() ? move.getCapturedPiece() : null);
        writePieceField(generator, MOVE_CAPTURED_KEY, capturedPiece);
    }

    public void writeMoveList(JsonGenerator generator, List<Move> moves) throws IOException {
        for (Move move : moves) {
            generator.writeStartObject();
            try {
                writeMove(generator, move);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    public void writeBoard(JsonGenerator generator, Board board) throws IOException {
        generator.writeObjectFieldStart(BOARD_PIECES_KEY);
        try {
            for (Tile tile : board.getShape().getTiles()) {
                Piece piece = board.get(tile);
                if (piece != null) {
                    writePieceField(generator, tile.toString(), piece);
                }
            }
        } finally {
            generator.writeEndObject();
        }
    }

    public void writePlayerState(
            JsonGenerator generator,
            PlayerState playerState
    ) throws IOException {

        generator.writeNumberField(PLAYER_PIECES_KEY, playerState.getPieceCount());
        generator.writeNumberField(PLAYER_SCORE_KEY, playerState.getScore());
    }

    public void writeRolledState(
            JsonGenerator generator,
            RolledGameState state
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(generator, state.getRoll());
        } finally {
            generator.writeEndObject();
        }

        generator.writeArrayFieldStart(AVAILABLE_MOVES_KEY);
        try {
            writeMoveList(generator, state.getAvailableMoves());
        } finally {
            generator.writeEndArray();
        }
    }

    public void writeMovedState(
            JsonGenerator generator,
            MovedGameState state
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(generator, state.getRoll());
        } finally {
            generator.writeEndObject();
        }

        generator.writeObjectFieldStart(MOVE_KEY);
        try {
            writeMove(generator, state.getMove());
        } finally {
            generator.writeEndObject();
        }
    }

    public void writeActionState(
            JsonGenerator generator,
            ActionGameState state
    ) throws IOException {

        if (state instanceof RolledGameState rolledState) {
            writeRolledState(generator, rolledState);

        } else if (state instanceof MovedGameState movedState) {
            writeMovedState(generator, movedState);

        } else {
            throw new IllegalArgumentException("Unknown action state type " + state.getClass());
        }
    }

    public void writeWaitingForRollState(
            JsonGenerator generator,
            WaitingForRollGameState state
    ) {
        // Nothing to include.
    }

    public void writeWaitingForMoveState(
            JsonGenerator generator,
            WaitingForMoveGameState state
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(generator, state.getRoll());
        } finally {
            generator.writeEndObject();
        }

        generator.writeArrayFieldStart(AVAILABLE_MOVES_KEY);
        try {
            writeMoveList(generator, state.getAvailableMoves());
        } finally {
            generator.writeEndArray();
        }
    }

    public void writePlayableState(
            JsonGenerator generator,
            PlayableGameState state
    ) throws IOException {

        if (state instanceof WaitingForRollGameState waitingForRollState) {
            writeWaitingForRollState(generator, waitingForRollState);

        } else if (state instanceof WaitingForMoveGameState waitingForMoveState) {
            writeWaitingForMoveState(generator, waitingForMoveState);

        } else {
            throw new IllegalArgumentException("Unknown playable state type " + state.getClass());
        }
    }

    public void writeOngoingState(
            JsonGenerator generator,
            OngoingGameState state
    ) throws IOException {

        generator.writeStringField(TURN_KEY, state.getTurn().getCharStr());

        if (state instanceof ActionGameState actionState) {
            writeActionState(generator, actionState);

        } else if (state instanceof PlayableGameState playableState) {
            writePlayableState(generator, playableState);

        } else {
            throw new IllegalArgumentException("Unknown ongoing state type " + state.getClass());
        }
    }

    public void writeResignedState(
            JsonGenerator generator,
            ResignedGameState state
    ) throws IOException {
         // Nothing to write.
    }

    public void writeAbandonedState(
            JsonGenerator generator,
            AbandonedGameState state
    ) throws IOException {
        generator.writeStringField(ABANDONED_REASON_KEY, state.getReason().getID());
    }

    public void writeControlState(
            JsonGenerator generator,
            ControlGameState state
    ) throws IOException {

        if (state.hasPlayer()) {
            generator.writeStringField(CONTROL_PLAYER_KEY, state.getPlayer().getCharStr());
        }

        if (state instanceof ResignedGameState resignedState) {
            writeResignedState(generator, resignedState);

        } else if (state instanceof AbandonedGameState abandonedState) {
            writeAbandonedState(generator, abandonedState);

        } else {
            throw new IllegalArgumentException("Unknown control state type " + state.getClass());
        }
    }

    public void writeEndState(
            JsonGenerator generator,
            EndGameState state
    ) throws IOException {
        if (state.hasWinner()) {
            generator.writeStringField(WINNER_KEY, state.getWinner().getCharStr());
        }
    }

    public String getStateType(GameState state) {
        if (state instanceof RolledGameState)
            return STATE_TYPE_ROLLED;
        if (state instanceof MovedGameState)
            return STATE_TYPE_MOVED;
        if (state instanceof WaitingForRollGameState)
            return STATE_TYPE_WAITING_FOR_ROLL;
        if (state instanceof WaitingForMoveGameState)
            return STATE_TYPE_WAITING_FOR_MOVE;
        if (state instanceof ResignedGameState)
            return STATE_TYPE_RESIGNED;
        if (state instanceof AbandonedGameState)
            return STATE_TYPE_ABANDONED;
        if (state instanceof EndGameState)
            return STATE_TYPE_END;

        throw new IllegalArgumentException("Unknown game state type " + state.getClass());
    }

    public void writeDerivedState(
            JsonGenerator generator,
            GameState state
    ) throws IOException {
        generator.writeStringField(STATE_TYPE_KEY, getStateType(state));
        generator.writeNumberField(STATE_TIME_KEY, state.getTimeSinceGameStartMs());
        writeMetadata(generator, STATE_METADATA_KEY, state.getMetadata());

        if (state instanceof OngoingGameState ongoingState) {
            writeOngoingState(generator, ongoingState);

        } else if (state instanceof ControlGameState controlState) {
            writeControlState(generator, controlState);

        } else if (state instanceof EndGameState winState) {
            writeEndState(generator, winState);

        } else {
            throw new IllegalArgumentException("Unknown game state type " + state.getClass());
        }
    }

    public void writeStates(
            JsonGenerator generator,
            List<GameState> states
    ) throws IOException {
        for (GameState state : states) {
            generator.writeStartObject();
            try {
                writeDerivedState(generator, state);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    public void writeCompleteState(
            JsonGenerator generator,
            GameState state
    ) throws IOException {

        generator.writeObjectFieldStart(BOARD_KEY);
        try {
            writeBoard(generator, state.getBoard());
        } finally {
            generator.writeEndObject();
        }

        generator.writeObjectFieldStart(PLAYERS_KEY);
        try {
            generator.writeObjectFieldStart(PlayerType.LIGHT.getCharStr());
            try {
                writePlayerState(generator, state.getLightPlayer());
            } finally {
                generator.writeEndObject();
            }

            generator.writeObjectFieldStart(PlayerType.DARK.getCharStr());
            try {
                writePlayerState(generator, state.getDarkPlayer());
            } finally {
                generator.writeEndObject();
            }
        } finally {
            generator.writeEndObject();
        }

        writeDerivedState(generator, state);
    }

    public void writeGameSettings(
            JsonGenerator generator,
            GameSettings settings
    ) throws IOException {

        generator.writeStringField(BOARD_SHAPE_KEY, settings.getBoardShape().getID());
        generator.writeStringField(PATHS_KEY, settings.getPaths().getID());
        generator.writeStringField(DICE_KEY, settings.getDice().getID());
        generator.writeNumberField(
                STARTING_PIECE_COUNT_KEY, settings.getStartingPieceCount()
        );
        generator.writeBooleanField(
                SAFE_ROSETTES_KEY, settings.areRosettesSafe()
        );
        generator.writeBooleanField(
                ROSETTES_GRANT_EXTRA_ROLLS_KEY, settings.doRosettesGrantExtraRolls()
        );
        generator.writeBooleanField(
                CAPTURES_GRANT_EXTRA_ROLLS_KEY, settings.doCapturesGrantExtraRolls()
        );
    }

    public void writeMetadata(
            JsonGenerator generator,
            String key,
            Map<String, String> metadata
    ) throws IOException {

        if (metadata.isEmpty())
            return;

        generator.writeObjectFieldStart(GAME_METADATA_KEY);
        try {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                generator.writeStringField(entry.getKey(), entry.getValue());
            }
        } finally {
            generator.writeEndObject();
        }
    }

    public void writeGame(JsonGenerator generator, Game game) throws IOException {
        generator.writeNumberField(VERSION_KEY, LATEST_VERSION);
        writeMetadata(generator, GAME_METADATA_KEY, game.getMetadata().getAll());

        generator.writeObjectFieldStart(SETTINGS_KEY);
        try {
            writeGameSettings(generator, game.getRules().getSettings());
        } finally {
            generator.writeEndObject();
        }

        List<GameState> states = game.getLandmarkStates();
        generator.writeObjectFieldStart(INITIAL_STATE_KEY);
        try {
            writeCompleteState(generator, states.get(0));
        } finally {
            generator.writeEndObject();
        }

        generator.writeArrayFieldStart(STATES_KEY);
        try {
            writeStates(generator, states.subList(1, states.size()));
        } finally {
            generator.writeEndArray();
        }
    }

    @Override
    public String encodeGame(Game game) {
        Writer writer = new StringWriter();
        try (JsonGenerator generator = jsonFactory.createGenerator(writer)) {

            generator.writeStartObject();
            try {
                writeGame(generator, game);
            } finally {
                generator.writeEndObject();
                generator.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON", e);
        }
        return writer.toString();
    }

    public Roll readRoll(RuleSet rules, ObjectNode json) {
        int rollValue = JsonHelper.readInt(json, ROLL_VALUE_KEY);
        return rules.getDiceFactory().createRoll(rollValue);
    }

    public Piece readOldPiece(RuleSet rules, ObjectNode json) {
        char ownerChar = JsonHelper.readChar(json, JsonNotation.OLD_PIECE_OWNER_KEY);
        PlayerType owner = PlayerType.getByChar(ownerChar);
        int pathIndex = JsonHelper.readInt(json, JsonNotation.OLD_PIECE_INDEX_KEY);
        return rules.getPieceProvider().create(owner, pathIndex);
    }

    public @Nullable Piece readNullablePiece(RuleSet rules, ObjectNode json, String key) {
        JsonNode node = JsonHelper.readNullableValue(json, key);
        if (node == null)
            return null;
        if (node instanceof ObjectNode pieceJson)
            return readOldPiece(rules, pieceJson);

        int value = JsonHelper.checkedToInt(node, key);
        PlayerType owner = (value < 0 ? PlayerType.DARK : PlayerType.LIGHT);
        int pathIndex = Math.abs(value) - 1;
        return rules.getPieceProvider().create(owner, pathIndex);
    }

    public Piece readPiece(RuleSet rules, ObjectNode json, String key) {
        Piece piece = readNullablePiece(rules, json, key);
        if (piece == null)
            throw new JsonHelper.JsonTypeError("Missing " + key);

        return piece;
    }

    private Tile getTileFromPiece(PathPair paths, Piece piece) {
        return paths.get(piece.getOwner()).get(piece.getPathIndex());
    }

    public Move readMove(RuleSet rules, ObjectNode json) {
        PathPair paths = rules.getPaths();

        Piece source = readNullablePiece(rules, json, MOVE_SOURCE_KEY);
        Tile sourceTile = (source != null ? getTileFromPiece(paths, source) : null);
        Piece dest = readNullablePiece(rules, json, MOVE_DEST_KEY);
        Tile destTile = (dest != null ? getTileFromPiece(paths, dest) : null);
        Piece captured = readNullablePiece(rules, json, MOVE_CAPTURED_KEY);

        PlayerType player = (source != null ? source.getOwner() : (dest != null ? dest.getOwner() : null));
        if (player == null)
            throw new JsonHelper.JsonReadError("Missing source AND dest, but we need at least one of them!");

        return new Move(
                player,
                sourceTile, source,
                destTile, dest,
                captured
        );
    }

    public List<Move> readMoveList(RuleSet rules, ArrayNode json) {
        List<Move> moves = new ArrayList<>();
        for (int index = 0; index < json.size(); ++index) {
            ObjectNode moveJson = JsonHelper.readArrayObjectEntry(json, index);
            moves.add(readMove(rules, moveJson));
        }
        return moves;
    }

    public Board readBoard(RuleSet rules, ObjectNode json) {
        Board board = new Board(rules.getBoardShape());
        ObjectNode piecesJson = JsonHelper.readObject(json, BOARD_PIECES_KEY);

        Iterator<String> keyIterator = piecesJson.fieldNames();
        while (keyIterator.hasNext()) {
            String tileKey = keyIterator.next();
            Tile tile = Tile.fromString(tileKey);
            Piece piece = readPiece(rules, piecesJson, tileKey);
            board.set(tile, piece);
        }
        return board;
    }

    public PlayerState readPlayerState(
            RuleSet rules,
            PlayerType playerType,
            ObjectNode json
    ) {
        int pieces = JsonHelper.readInt(json, PLAYER_PIECES_KEY);
        int score = JsonHelper.readInt(json, PLAYER_SCORE_KEY);
        return rules.getPlayerStateProvider().create(
                playerType, pieces, score
        );
    }

    public boolean isActionState(String stateType) {
        return stateType.equals(STATE_TYPE_ROLLED)
                || stateType.equals(STATE_TYPE_MOVED);
    }

    public boolean isPlayableState(String stateType) {
        return stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)
                || stateType.equals(STATE_TYPE_WAITING_FOR_MOVE);
    }

    public boolean isOngoingState(String stateType) {
        return isActionState(stateType) || isPlayableState(stateType);
    }

    public boolean isControlState(String stateType) {
        return stateType.equals(STATE_TYPE_RESIGNED)
                || stateType.equals(STATE_TYPE_ABANDONED);
    }

    public RolledGameState readRolledState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readObject(json, ROLL_KEY);
        Roll roll = readRoll(rules, rollJson);
        return stateSource.createRolledState(
                rules, timeSinceGameStartMs, turn, roll
        );
    }

    public MovedGameState readMovedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readObject(json, ROLL_KEY);
        Roll roll = readRoll(rules, rollJson);

        ObjectNode moveJson = JsonHelper.readObject(json, MOVE_KEY);
        Move move = readMove(rules, moveJson);

        return stateSource.createMovedState(
                rules, timeSinceGameStartMs, turn, roll, move
        );
    }

    public WaitingForRollGameState readWaitingForRollState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        return stateSource.createWaitingForRollState(
                rules, timeSinceGameStartMs, turn
        );
    }

    public WaitingForMoveGameState readWaitingForMoveState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readObject(json, ROLL_KEY);
        Roll roll = readRoll(rules, rollJson);
        return stateSource.createWaitingForMoveState(
                rules, timeSinceGameStartMs, turn, roll
        );
    }

    public ActionGameState readActionState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            String stateType,
            PlayerType turn
    ) {
        if (stateType.equals(STATE_TYPE_ROLLED)) {
            return readRolledState(
                    rules, timeSinceGameStartMs, stateSource, json, turn
            );
        } else if (stateType.equals(STATE_TYPE_MOVED)) {
            return readMovedState(
                    rules, timeSinceGameStartMs, stateSource, json, turn
            );
        } else {
            throw new JsonHelper.JsonReadError("Unknown action state type: " + stateType);
        }
    }

    public PlayableGameState readPlayableState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            String stateType,
            PlayerType turn
    ) {
        if (stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)) {
            return readWaitingForRollState(
                    rules, timeSinceGameStartMs, stateSource, json, turn
            );
        } else if (stateType.equals(STATE_TYPE_WAITING_FOR_MOVE)) {
            return readWaitingForMoveState(
                    rules, timeSinceGameStartMs, stateSource, json, turn
            );
        } else {
            throw new JsonHelper.JsonReadError("Unknown playable state type: " + stateType);
        }
    }

    public OngoingGameState readOngoingState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            String stateType
    ) {
        char turnChar = JsonHelper.readChar(json, TURN_KEY);
        PlayerType turn = PlayerType.getByChar(turnChar);

        if (isActionState(stateType)) {
            return readActionState(
                    rules, timeSinceGameStartMs, stateSource, json, stateType, turn
            );
        } else if (isPlayableState(stateType)) {
            return readPlayableState(
                    rules, timeSinceGameStartMs, stateSource, json, stateType, turn
            );
        } else {
            throw new JsonHelper.JsonReadError("Unknown ongoing state type: " + stateType);
        }
    }

    public ResignedGameState readResignedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            PlayerType player
    ) {
        return stateSource.createResignedState(
                rules, timeSinceGameStartMs, player
        );
    }

    public AbandonedGameState readAbandonedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            @Nullable PlayerType player
    ) {
        String reasonID = JsonHelper.readString(json, ABANDONED_REASON_KEY);
        AbandonReason reason = AbandonReason.getByID(reasonID);
        return stateSource.createAbandonedState(
                rules, timeSinceGameStartMs, reason, player
        );
    }

    public ControlGameState readControlState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json,
            String stateType
    ) {
        Character playerChar = JsonHelper.readNullableChar(json, CONTROL_PLAYER_KEY);
        PlayerType player = (playerChar != null ? PlayerType.getByChar(playerChar) : null);

        if (stateType.equals(STATE_TYPE_RESIGNED)) {
            if (player == null)
                throw new IllegalArgumentException("player should not be null for a resigned game state");

            return readResignedState(
                    rules, timeSinceGameStartMs, stateSource, json, player
            );
        } else if (stateType.equals(STATE_TYPE_ABANDONED)) {
            return readAbandonedState(
                    rules, timeSinceGameStartMs, stateSource, json, player
            );
        } else {
            throw new JsonHelper.JsonReadError("Unknown control state type: " + stateType);
        }
    }

    public EndGameState readEndState(
            RuleSet rules,
            long timeSinceGameStartMs,
            StateSource stateSource,
            ObjectNode json
    ) {
        Character winnerChar = JsonHelper.readNullableChar(json, WINNER_KEY);
        PlayerType winner = (winnerChar != null ? PlayerType.getByChar(winnerChar) : null);
        return stateSource.createEndState(rules, timeSinceGameStartMs, winner);
    }

    public GameState readDerivedState(
            RuleSet rules,
            StateSource stateSource,
            ObjectNode json
    ) {
        String stateType = JsonHelper.readString(json, STATE_TYPE_KEY);
        long timeSinceGameStartMs = JsonHelper.readLongWithDefault(json, STATE_TIME_KEY, 0);
        ObjectNode metadataJson = JsonHelper.readObject(json, STATE_METADATA_KEY);
        Map<String, String> metadata = readMetadata(metadataJson);

        GameState state;
        if (isOngoingState(stateType)) {
            state = readOngoingState(
                    rules, timeSinceGameStartMs, stateSource, json, stateType
            );
        } else if (isControlState(stateType)) {
            state = readControlState(
                    rules, timeSinceGameStartMs, stateSource, json, stateType
            );
        } else if (stateType.equals(STATE_TYPE_END)) {
            state = readEndState(
                    rules, timeSinceGameStartMs, stateSource, json
            );
        } else {
            throw new JsonHelper.JsonReadError("Unknown state type: " + stateType);
        }

        if (!metadata.isEmpty()) {
            state.addMetadata(metadata);
        }
        return state;
    }

    public GameState readCompleteState(
            RuleSet rules,
            ObjectNode json
    ) {
        ObjectNode boardJson = JsonHelper.readObject(json, BOARD_KEY);
        ObjectNode playersJson = JsonHelper.readObject(json, PLAYERS_KEY);
        ObjectNode lightPlayerJson = JsonHelper.readObject(playersJson, PlayerType.LIGHT.getCharStr());
        ObjectNode darkPlayerJson = JsonHelper.readObject(playersJson, PlayerType.DARK.getCharStr());

        Board board = readBoard(rules, boardJson);
        PlayerState lightPlayer = readPlayerState(rules, PlayerType.LIGHT, lightPlayerJson);
        PlayerState darkPlayer = readPlayerState(rules, PlayerType.DARK, darkPlayerJson);

        StateSource stateSource = new FullStateSource(
                board, lightPlayer, darkPlayer
        );
        return readDerivedState(rules, stateSource, json);
    }

    public List<GameState> readStates(
            RuleSet rules,
            GameState initialState,
            ArrayNode json
    ) {
        DerivedStateSource stateSource = new DerivedStateSource(initialState);
        int lastIndex = -1;
        for (int jsonIndex = 0; jsonIndex < json.size(); ++jsonIndex) {
            ObjectNode stateJson = JsonHelper.readArrayObjectEntry(json, jsonIndex);
            GameState state = readDerivedState(rules, stateSource, stateJson);

            int index = stateSource.lastIndexOf(state);
            if (index <= lastIndex)
                throw new RuntimeException("DerivedStateSource did not include states in read order");

            lastIndex = index;
        }
        return stateSource.getAllStates();
    }

    public GameSettings readGameSettings(ObjectNode json) {
        String boardID = JsonHelper.readString(json, BOARD_SHAPE_KEY).toLowerCase();
        String pathID = JsonHelper.readString(json, PATHS_KEY).toLowerCase();
        String diceID = JsonHelper.readString(json, DICE_KEY).toLowerCase();
        int startingPieceCount = JsonHelper.readInt(json, STARTING_PIECE_COUNT_KEY);
        boolean safeRosettes = JsonHelper.readBool(json, SAFE_ROSETTES_KEY);
        boolean rosettesGrantExtraRolls = JsonHelper.readBool(
                json, ROSETTES_GRANT_EXTRA_ROLLS_KEY
        );
        boolean capturesGrantExtraRolls = JsonHelper.readBool(
                json, CAPTURES_GRANT_EXTRA_ROLLS_KEY
        );

        BoardShapeFactory board = this.boardShapes.get(boardID);
        PathPairFactory path = this.pathPairs.get(pathID);
        DiceFactory dice = this.dice.get(diceID);
        if (board == null)
            throw new IllegalArgumentException("Unknown board shape " + boardID);
        if (path == null)
            throw new IllegalArgumentException("Unknown path pair " + pathID);
        if (dice == null)
            throw new IllegalArgumentException("Unknown dice " + diceID);

        return new GameSettings(
                board.createBoardShape(),
                path.createPathPair(),
                dice,
                startingPieceCount,
                safeRosettes,
                rosettesGrantExtraRolls,
                capturesGrantExtraRolls
        );
    }

    public Map<String, String> readMetadata(ObjectNode json) {
        Map<String, String> metadata = new HashMap<>();
        Iterator<String> fieldIterator = json.fieldNames();
        while (fieldIterator.hasNext()) {
            String key = fieldIterator.next();
            String value = JsonHelper.readString(json, key);
            metadata.put(key, value);
        }
        return metadata;
    }

    public Game readGameV1Or2(ObjectNode json) {
        ObjectNode metadataJson = JsonHelper.readObject(json, GAME_METADATA_KEY);
        GameMetadata metadata = new GameMetadata(readMetadata(metadataJson));

        ObjectNode settingsJson = JsonHelper.readObject(json, SETTINGS_KEY);
        GameSettings settings = readGameSettings(settingsJson);
        RuleSet rules = ruleSetProvider.create(settings, metadata);

        ObjectNode initialStateJson = JsonHelper.readObject(json, INITIAL_STATE_KEY);
        GameState initialState = readCompleteState(rules, initialStateJson);

        ArrayNode statesJson = JsonHelper.readArray(json, STATES_KEY);
        List<GameState> states = readStates(rules, initialState, statesJson);
        return new Game(rules, metadata, states);
    }

    public Game readGame(ObjectNode json) {
        int version = JsonHelper.readInt(json, VERSION_KEY);
        if (version == 1 || version == 2)
            return readGameV1Or2(json);

        throw new JsonHelper.JsonReadError("Unknown JSON-Notation version: " + version);
    }

    @Override
    public Game decodeGame(String encoded) {
        try (JsonParser parser = jsonFactory.createParser(encoded)) {

            JsonNode json = objectMapper.readTree(parser);
            if (!(json instanceof ObjectNode objectNode)) {
                throw new JsonHelper.JsonReadError(
                        "Expected an object, not a " + json.getNodeType().name()
                );
            }
            return readGame(objectNode);

        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON", e);
        }
    }
}
