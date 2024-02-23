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
import net.royalur.name.NameMap;
import net.royalur.rules.RuleSet;
import net.royalur.rules.RuleSetProvider;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A notation that can be used to encode games of the Royal
 * Game of Ur into JSON for serialisation. This notation
 * has been created to be read by machines, not humans.
 */
public class JsonNotation<
    P extends Piece, S extends PlayerState, R extends Roll
> implements Notation<P, S, R> {

    /**
     * The latest version of the JSON notation. If any breaking changes
     * are made to the JSON notation, then this field will be updated
     * to reflect that.
     */
    public static final int LATEST_VERSION = 1;

    /**
     * The key in the JSON for the version of the notation.
     */
    public static final String VERSION_KEY = "notation_version";

    /**
     * The key in the JSON for the metadata of the game.
     */
    public static final String METADATA_KEY = "metadata";

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
     * Represents states of type {@link WinGameState}.
     */
    public static final String STATE_TYPE_WIN = "win";

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
     * The key in the JSON for the owner of a piece.
     */
    public static final String PIECE_OWNER_KEY = "owner";

    /**
     * The key in the JSON for the index of a piece on its path.
     */
    public static final String PIECE_INDEX_KEY = "index";

    /**
     * The key in the JSON for the player whose turn it is in a state.
     */
    public static final String TURN_KEY = "turn";

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

    private final NameMap<?, ? extends BoardShapeFactory> boardShapes;
    private final NameMap<?, ? extends PathPairFactory> pathPairs;
    private final NameMap<?, ? extends DiceFactory<R>> dice;
    private final RuleSetProvider<P, S> ruleSetProvider;

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
            NameMap<?, ? extends BoardShapeFactory> boardShapes,
            NameMap<?, ? extends PathPairFactory> pathPairs,
            NameMap<?, ? extends DiceFactory<R>> dice,
            RuleSetProvider<P, S> ruleSetProvider,
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
            NameMap<?, ? extends BoardShapeFactory> boardShapes,
            NameMap<?, ? extends PathPairFactory> pathPairs,
            NameMap<?, ? extends DiceFactory<R>> dice,
            RuleSetProvider<P, S> ruleSetProvider
    ) {
        this(boardShapes, pathPairs, dice, ruleSetProvider, JsonFactory.builder().build());
    }

    public static JsonNotation<Piece, PlayerState, Roll> createSimple() {
        return new JsonNotation<>(
                BoardType.FACTORIES,
                PathType.FACTORIES,
                DiceType.FACTORIES,
                new SimpleRuleSetProvider()
        );
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void writeRoll(
            JsonGenerator generator,
            R roll
    ) throws IOException {

        generator.writeNumberField(ROLL_VALUE_KEY, roll.value());
    }

    public void writePiece(
            JsonGenerator generator,
            P piece
    ) throws IOException {

        generator.writeStringField(PIECE_OWNER_KEY, piece.getOwner().getCharStr());
        generator.writeNumberField(PIECE_INDEX_KEY, piece.getPathIndex());
    }

    protected void writePieceField(
            JsonGenerator generator,
            String fieldName,
            @Nullable P piece
    ) throws IOException {

        if (piece != null) {
            generator.writeObjectFieldStart(fieldName);
            try {
                writePiece(generator, piece);
            } finally {
                generator.writeEndObject();
            }
        } else {
            generator.writeNullField(fieldName);
        }
    }

    public void writeMove(
            JsonGenerator generator,
            Move<P> move
    ) throws IOException {

        P sourcePiece = (move.hasSource() ? move.getSourcePiece() : null);
        writePieceField(generator, MOVE_SOURCE_KEY, sourcePiece);

        P destPiece = (move.hasDest() ? move.getDestPiece() : null);
        writePieceField(generator, MOVE_DEST_KEY, destPiece);

        P capturedPiece = (move.isCapture() ? move.getCapturedPiece() : null);
        writePieceField(generator, MOVE_CAPTURED_KEY, capturedPiece);
    }

    public void writeMoveList(
            JsonGenerator generator,
            List<Move<P>> moves
    ) throws IOException {

        for (Move<P> move : moves) {
            generator.writeStartObject();
            try {
                writeMove(generator, move);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    public void writeBoard(
            JsonGenerator generator,
            Board<P> board
    ) throws IOException {

        generator.writeObjectFieldStart(BOARD_PIECES_KEY);
        try {
            for (Tile tile : board.getShape().getTiles()) {
                P piece = board.get(tile);
                if (piece != null) {
                    generator.writeObjectFieldStart(tile.toString());
                    try {
                        writePiece(generator, piece);
                    } finally {
                        generator.writeEndObject();
                    }
                }
            }
        } finally {
            generator.writeEndObject();
        }
    }

    public void writePlayerState(
            JsonGenerator generator,
            S playerState
    ) throws IOException {

        generator.writeNumberField(PLAYER_PIECES_KEY, playerState.getPieceCount());
        generator.writeNumberField(PLAYER_SCORE_KEY, playerState.getScore());
    }

    public void writeRolledState(
            JsonGenerator generator,
            RolledGameState<P, S, R> state
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
            MovedGameState<P, S, R> state
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
            ActionGameState<P, S, R> state
    ) throws IOException {

        if (state instanceof RolledGameState<P, S, R> rolledState) {
            writeRolledState(generator, rolledState);

        } else if (state instanceof MovedGameState<P, S, R> movedState) {
            writeMovedState(generator, movedState);

        } else {
            throw new IllegalArgumentException("Unknown action state type " + state.getClass());
        }
    }

    public void writeWaitingForRollState(
            JsonGenerator generator,
            WaitingForRollGameState<P, S, R> state
    ) {
        // Nothing to include.
    }

    public void writeWaitingForMoveState(
            JsonGenerator generator,
            WaitingForMoveGameState<P, S, R> state
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
            PlayableGameState<P, S, R> state
    ) throws IOException {

        if (state instanceof WaitingForRollGameState<P, S, R> waitingForRollState) {
            writeWaitingForRollState(generator, waitingForRollState);

        } else if (state instanceof WaitingForMoveGameState<P, S, R> waitingForMoveState) {
            writeWaitingForMoveState(generator, waitingForMoveState);

        } else {
            throw new IllegalArgumentException("Unknown playable state type " + state.getClass());
        }
    }

    public void writeOngoingState(
            JsonGenerator generator,
            OngoingGameState<P, S, R> state
    ) throws IOException {

        generator.writeStringField(TURN_KEY, state.getTurn().getCharStr());

        if (state instanceof ActionGameState<P, S, R> actionState) {
            writeActionState(generator, actionState);

        } else if (state instanceof PlayableGameState<P, S, R> playableState) {
            writePlayableState(generator, playableState);

        } else {
            throw new IllegalArgumentException("Unknown ongoing state type " + state.getClass());
        }
    }

    public void writeWinState(
            JsonGenerator generator,
            WinGameState<P, S, R> state
    ) throws IOException {

        generator.writeStringField(WINNER_KEY, state.getWinner().getCharStr());
    }

    public String getStateType(
            GameState<P, S, R> state
    ) {
        if (state instanceof RolledGameState)
            return STATE_TYPE_ROLLED;
        if (state instanceof MovedGameState)
            return STATE_TYPE_MOVED;
        if (state instanceof WaitingForRollGameState)
            return STATE_TYPE_WAITING_FOR_ROLL;
        if (state instanceof WaitingForMoveGameState)
            return STATE_TYPE_WAITING_FOR_MOVE;
        if (state instanceof WinGameState)
            return STATE_TYPE_WIN;

        throw new IllegalArgumentException("Unknown game state type " + state.getClass());
    }

    public void writeState(
            JsonGenerator generator,
            GameState<P, S, R> state
    ) throws IOException {

        generator.writeStringField(STATE_TYPE_KEY, getStateType(state));

        if (state instanceof OngoingGameState<P, S, R> ongoingState) {
            writeOngoingState(generator, ongoingState);

        } else if (state instanceof WinGameState<P, S, R> winState) {
            writeWinState(generator, winState);

        } else {
            throw new IllegalArgumentException("Unknown game state type " + state.getClass());
        }
    }

    public void writeStates(
            JsonGenerator generator,
            List<GameState<P, S, R>> states
    ) throws IOException {

        for (GameState<P, S, R> state : states) {
            generator.writeStartObject();
            try {
                writeState(generator, state);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    public void writeInitialState(
            JsonGenerator generator,
            GameState<P, S, R> state
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

        writeState(generator, state);
    }

    public void writeGameSettings(
            JsonGenerator generator,
            GameSettings<R> settings
    ) throws IOException {

        generator.writeStringField(
                BOARD_SHAPE_KEY, settings.getBoardShape().getName().getTextName()
        );
        generator.writeStringField(
                PATHS_KEY, settings.getPaths().getName().getTextName()
        );
        generator.writeStringField(
                DICE_KEY, settings.getDice().getName().getTextName()
        );
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
            GameMetadata metadata
    ) throws IOException {

        for (Map.Entry<String, String> entry : metadata.getAll().entrySet()) {
            generator.writeStringField(entry.getKey(), entry.getValue());
        }
    }

    public void writeGame(
            JsonGenerator generator,
            Game<P, S, R> game
    ) throws IOException {

        generator.writeNumberField(VERSION_KEY, LATEST_VERSION);

        generator.writeObjectFieldStart(METADATA_KEY);
        try {
            writeMetadata(generator, game.getMetadata());
        } finally {
            generator.writeEndObject();
        }

        generator.writeObjectFieldStart(SETTINGS_KEY);
        try {
            writeGameSettings(generator, game.getRules().getSettings());
        } finally {
            generator.writeEndObject();
        }

        List<GameState<P, S, R>> states = game.getLandmarkStates();
        generator.writeObjectFieldStart(INITIAL_STATE_KEY);
        try {
            writeInitialState(generator, states.get(0));
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
    public String encodeGame(
            Game<P, S, R> game
    ) {
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

    public R readRoll(
            RuleSet<P, S, R> rules,
            ObjectNode json
    ) {
        int rollValue = JsonHelper.readInt(json, ROLL_VALUE_KEY);
        return rules.getDiceFactory().createRoll(rollValue);
    }

    public P readPiece(
            RuleSet<P, S, R> rules,
            ObjectNode json
    ) {

        char ownerChar = JsonHelper.readChar(json, JsonNotation.PIECE_OWNER_KEY);
        PlayerType owner = PlayerType.getByChar(ownerChar);

        int pathIndex = JsonHelper.readInt(json, JsonNotation.PIECE_INDEX_KEY);
        return rules.getPieceProvider().create(
                owner, pathIndex
        );
    }

    private Tile getTileFromPiece(
            PathPair paths,
            P piece
    ) {
        return paths.get(piece.getOwner()).get(piece.getPathIndex());
    }

    public Move<P> readMove(
            RuleSet<P, S, R> rules,
            ObjectNode json
    ) {

        PathPair paths = rules.getPaths();

        ObjectNode sourceJson = JsonHelper.readNullableDict(json, MOVE_SOURCE_KEY);
        P source = (sourceJson != null ? readPiece(rules, sourceJson) : null);
        Tile sourceTile = (source != null ? getTileFromPiece(paths, source) : null);

        ObjectNode destJson = JsonHelper.readNullableDict(json, MOVE_DEST_KEY);
        P dest = (destJson != null ? readPiece(rules, destJson) : null);
        Tile destTile = (dest != null ? getTileFromPiece(paths, dest) : null);

        ObjectNode capturedJson = JsonHelper.readNullableDict(json, MOVE_CAPTURED_KEY);
        P captured = (capturedJson != null ? readPiece(rules, capturedJson) : null);

        PlayerType player = (source != null ? source.getOwner() : (dest != null ? dest.getOwner() : null));
        if (player == null)
            throw new JsonHelper.JsonReadError("Missing source AND dest, but we need at least one of them!");

        return new Move<>(
                player,
                sourceTile, source,
                destTile, dest,
                captured
        );
    }

    public List<Move<P>> readMoveList(
            RuleSet<P, S, R> rules,
            ArrayNode json
    ) {
        List<Move<P>> moves = new ArrayList<>();
        for (int index = 0; index < json.size(); ++index) {
            ObjectNode moveJson = JsonHelper.readArrayDictEntry(json, index);
            moves.add(readMove(rules, moveJson));
        }
        return moves;
    }

    public Board<P> readBoard(
            RuleSet<P, S, R> rules,
            ObjectNode json
    ) {

        Board<P> board = new Board<>(rules.getBoardShape());
        ObjectNode piecesJson = JsonHelper.readDict(json, BOARD_PIECES_KEY);

        Iterator<String> keyIterator = piecesJson.fieldNames();
        while (keyIterator.hasNext()) {
            String tileKey = keyIterator.next();
            Tile tile = Tile.fromString(tileKey);
            ObjectNode pieceJson = JsonHelper.readDict(piecesJson, tileKey);
            P piece = readPiece(rules, pieceJson);
            board.set(tile, piece);
        }
        return board;
    }

    public S readPlayerState(
            RuleSet<P, S, R> rules,
            PlayerType playerType,
            ObjectNode json
    ) {

        int pieces = JsonHelper.readInt(json, PLAYER_PIECES_KEY);
        int score = JsonHelper.readInt(json, PLAYER_SCORE_KEY);
        return rules.getPlayerStateProvider().create(
                playerType, pieces, score
        );
    }

    public boolean isActionStateType(String stateType) {
        return stateType.equals(STATE_TYPE_ROLLED)
                || stateType.equals(STATE_TYPE_MOVED);
    }

    public boolean isPlayableGameState(String stateType) {
        return stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)
                || stateType.equals(STATE_TYPE_WAITING_FOR_MOVE);
    }

    public boolean isOngoingGameState(String stateType) {
        return isActionStateType(stateType) || isPlayableGameState(stateType);
    }

    public RolledGameState<P, S, R> readRolledState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);
        return stateSource.createRolledState(rules, turn, roll);
    }

    public MovedGameState<P, S, R> readMovedState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);

        ObjectNode moveJson = JsonHelper.readDict(json, MOVE_KEY);
        Move<P> move = readMove(rules, moveJson);

        return stateSource.createMovedState(rules, turn, roll, move);
    }

    public WaitingForRollGameState<P, S, R> readWaitingForRollState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        return stateSource.createWaitingForRollState(rules, turn);
    }

    public WaitingForMoveGameState<P, S, R> readWaitingForMoveState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            PlayerType turn
    ) {
        ObjectNode rollJson = JsonHelper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);
        return stateSource.createWaitingForMoveState(rules, turn, roll);
    }

    public ActionGameState<P, S, R> readActionState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            String stateType,
            PlayerType turn
    ) {

        if (stateType.equals(STATE_TYPE_ROLLED)) {
            return readRolledState(rules, stateSource, json, turn);

        } else if (stateType.equals(STATE_TYPE_MOVED)) {
            return readMovedState(rules, stateSource, json, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown action state type: " + stateType);
        }
    }

    public PlayableGameState<P, S, R> readPlayableState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            String stateType,
            PlayerType turn
    ) {

        if (stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)) {
            return readWaitingForRollState(rules, stateSource, json, turn);

        } else if (stateType.equals(STATE_TYPE_WAITING_FOR_MOVE)) {
            return readWaitingForMoveState(rules, stateSource, json, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown playable state type: " + stateType);
        }
    }

    public OngoingGameState<P, S, R> readOngoingState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json,
            String stateType
    ) {

        char turnChar = JsonHelper.readChar(json, TURN_KEY);
        PlayerType turn = PlayerType.getByChar(turnChar);

        if (isActionStateType(stateType)) {
            return readActionState(rules, stateSource, json, stateType, turn);

        } else if (isPlayableGameState(stateType)) {
            return readPlayableState(rules, stateSource, json, stateType, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown ongoing state type: " + stateType);
        }
    }

    public WinGameState<P, S, R> readWinState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json
    ) {
        char winnerChar = JsonHelper.readChar(json, WINNER_KEY);
        PlayerType winner = PlayerType.getByChar(winnerChar);

        return stateSource.createWinState(rules, winner);
    }

    public GameState<P, S, R> readState(
            RuleSet<P, S, R> rules,
            StateSource<P, S, R> stateSource,
            ObjectNode json
    ) {
        String stateType = JsonHelper.readString(json, STATE_TYPE_KEY);

        if (isOngoingGameState(stateType)) {
            return readOngoingState(rules, stateSource, json, stateType);

        } else if (stateType.equals(STATE_TYPE_WIN)) {
            return readWinState(rules, stateSource, json);

        } else {
            throw new JsonHelper.JsonReadError("Unknown state type: " + stateType);
        }
    }

    public GameState<P, S, R> readInitialState(
            RuleSet<P, S, R> rules,
            ObjectNode json
    ) {
        ObjectNode boardJson = JsonHelper.readDict(json, BOARD_KEY);
        ObjectNode playersJson = JsonHelper.readDict(json, PLAYERS_KEY);
        ObjectNode lightPlayerJson = JsonHelper.readDict(playersJson, PlayerType.LIGHT.getCharStr());
        ObjectNode darkPlayerJson = JsonHelper.readDict(playersJson, PlayerType.DARK.getCharStr());

        Board<P> board = readBoard(rules, boardJson);
        S lightPlayer = readPlayerState(rules, PlayerType.LIGHT, lightPlayerJson);
        S darkPlayer = readPlayerState(rules, PlayerType.DARK, darkPlayerJson);

        StateSource<P, S, R> stateSource = new FullStateSource<>(
                board, lightPlayer, darkPlayer
        );
        return readState(rules, stateSource, json);
    }

    public List<GameState<P, S, R>> readStates(
            RuleSet<P, S, R> rules,
            GameState<P, S, R> initialState,
            ArrayNode json
    ) {
        DerivedStateSource<P, S, R> stateSource = new DerivedStateSource<>(initialState);
        int lastIndex = -1;
        for (int jsonIndex = 0; jsonIndex < json.size(); ++jsonIndex) {
            ObjectNode stateJson = JsonHelper.readArrayDictEntry(json, jsonIndex);
            GameState<P, S, R> state = readState(rules, stateSource, stateJson);

            int index = stateSource.lastIndexOf(state);
            if (index <= lastIndex)
                throw new RuntimeException("DerivedStateSource did not include states in read order");

            lastIndex = index;
        }
        return stateSource.getAllStates();
    }

    public GameSettings<R> readGameSettings(ObjectNode json) {
        String boardShapeName = JsonHelper.readString(json, BOARD_SHAPE_KEY);
        String pathsName = JsonHelper.readString(json, PATHS_KEY);
        String diceName = JsonHelper.readString(json, DICE_KEY);
        int startingPieceCount = JsonHelper.readInt(json, STARTING_PIECE_COUNT_KEY);
        boolean safeRosettes = JsonHelper.readBool(json, SAFE_ROSETTES_KEY);
        boolean rosettesGrantExtraRolls = JsonHelper.readBool(
                json, ROSETTES_GRANT_EXTRA_ROLLS_KEY
        );
        boolean capturesGrantExtraRolls = JsonHelper.readBool(
                json, CAPTURES_GRANT_EXTRA_ROLLS_KEY
        );
        return new GameSettings<>(
                boardShapes.get(boardShapeName).createBoardShape(),
                pathPairs.get(pathsName).createPathPair(),
                dice.get(diceName),
                startingPieceCount,
                safeRosettes,
                rosettesGrantExtraRolls,
                capturesGrantExtraRolls
        );
    }

    public GameMetadata readMetadata(ObjectNode json) {
        return new GameMetadata();
    }

    public Game<P, S, R> readGameV1(ObjectNode json) {

        ObjectNode metadataJson = JsonHelper.readDict(json, METADATA_KEY);
        GameMetadata metadata = readMetadata(metadataJson);

        ObjectNode settingsJson = JsonHelper.readDict(json, SETTINGS_KEY);
        GameSettings<R> settings = readGameSettings(settingsJson);
        RuleSet<P, S, R> rules = ruleSetProvider.create(settings, metadata);

        ObjectNode initialStateJson = JsonHelper.readDict(json, INITIAL_STATE_KEY);
        GameState<P, S, R> initialState = readInitialState(rules, initialStateJson);

        ArrayNode statesJson = JsonHelper.readArray(json, STATES_KEY);
        List<GameState<P, S, R>> states = readStates(rules, initialState, statesJson);

        List<GameState<P, S, R>> allStates = new ArrayList<>(states.size() + 1);
        allStates.add(initialState);
        allStates.addAll(states);
        return new Game<>(rules, metadata, allStates);
    }

    public Game<P, S, R> readGame(ObjectNode json) {

        int version = JsonHelper.readInt(json, VERSION_KEY);
        if (version == 1)
            return readGameV1(json);

        throw new JsonHelper.JsonReadError("Unknown JSON-Notation version: " + version);
    }

    @Override
    public Game<P, S, R> decodeGame(String encoded) {

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
