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
    public static final @Nonnull String VERSION_KEY = "notation_version";

    /**
     * The key in the JSON for the metadata of the game.
     */
    public static final @Nonnull String METADATA_KEY = "metadata";

    /**
     * The key in the JSON for the settings of a game.
     */
    public static final @Nonnull String SETTINGS_KEY = "settings";

    /**
     * The key in the JSON for the game setting of the board shape used.
     */
    public static final @Nonnull String BOARD_SHAPE_KEY = "board_shape";

    /**
     * The key in the JSON for the game setting of the paths used.
     */
    public static final @Nonnull String PATHS_KEY = "paths";

    /**
     * The key in the JSON for the game settings of the dice used.
     */
    public static final @Nonnull String DICE_KEY = "dice";

    /**
     * The key in the JSON for game setting of the starting piece count.
     */
    public static final @Nonnull String STARTING_PIECE_COUNT_KEY = "starting_piece_count";

    /**
     * The key in the JSON for the game setting of whether rosettes are safe.
     */
    public static final @Nonnull String SAFE_ROSETTES_KEY = "safe_rosettes";

    /**
     * The key in the JSON for the game setting of whether rosettes grant extra rolls.
     */
    public static final @Nonnull String ROSETTES_GRANT_EXTRA_ROLLS_KEY = "rosettes_grant_extra_rolls";

    /**
     * The key in the JSON for the game setting of whether rosettes grant extra rolls.
     */
    public static final @Nonnull String CAPTURES_GRANT_EXTRA_ROLLS_KEY = "captures_grant_extra_rolls";

    /**
     * The key in the JSON for the initial state of the board and players.
     */
    public static final @Nonnull String INITIAL_STATE_KEY = "initial_state";

    /**
     * The key in the JSON for the list of states in the game.
     */
    public static final @Nonnull String STATES_KEY = "states";

    /**
     * The key in the JSON for the type of state.
     */
    public static final @Nonnull String STATE_TYPE_KEY = "type";

    /**
     * Represents states of type {@link RolledGameState}.
     */
    public static final @Nonnull String STATE_TYPE_ROLLED = "rolled";

    /**
     * Represents states of type {@link MovedGameState}.
     */
    public static final @Nonnull String STATE_TYPE_MOVED = "moved";

    /**
     * Represents states of type {@link WaitingForRollGameState}.
     */
    public static final @Nonnull String STATE_TYPE_WAITING_FOR_ROLL = "waiting_for_roll";

    /**
     * Represents states of type {@link WaitingForMoveGameState}.
     */
    public static final @Nonnull String STATE_TYPE_WAITING_FOR_MOVE = "waiting_for_move";

    /**
     * Represents states of type {@link WinGameState}.
     */
    public static final @Nonnull String STATE_TYPE_WIN = "win";

    /**
     * The key in the JSON for the roll that was made.
     */
    public static final @Nonnull String ROLL_KEY = "roll";

    /**
     * The key in the JSON for the value of a roll.
     */
    public static final @Nonnull String ROLL_VALUE_KEY = "value";

    /**
     * The key in the JSON for a move that was made.
     */
    public static final @Nonnull String MOVE_KEY = "move";

    /**
     * The key in the JSON for the source piece of a move.
     */
    public static final @Nonnull String MOVE_SOURCE_KEY = "source";

    /**
     * The key in the JSON for the destination piece of a move.
     */
    public static final @Nonnull String MOVE_DEST_KEY = "destination";

    /**
     * The key in the JSON for the captured piece of a move.
     */
    public static final @Nonnull String MOVE_CAPTURED_KEY = "captured";

    /**
     * The key in the JSON for the moves that are available in a state.
     */
    public static final @Nonnull String AVAILABLE_MOVES_KEY = "available_moves";

    /**
     * The key in the JSON for the owner of a piece.
     */
    public static final @Nonnull String PIECE_OWNER_KEY = "owner";

    /**
     * The key in the JSON for the index of a piece on its path.
     */
    public static final @Nonnull String PIECE_INDEX_KEY = "index";

    /**
     * The key in the JSON for the player whose turn it is in a state.
     */
    public static final @Nonnull String TURN_KEY = "turn";

    /**
     * The key in the JSON for the player that won the game.
     */
    public static final @Nonnull String WINNER_KEY = "winner";

    /**
     * The key in the JSON for the state of the board.
     */
    public static final @Nonnull String BOARD_KEY = "board";

    /**
     * The key in the JSON for the pieces on a board.
     */
    public static final @Nonnull String BOARD_PIECES_KEY = "pieces";

    /**
     * The key in the JSON for the state of the players.
     */
    public static final @Nonnull String PLAYERS_KEY = "players";

    /**
     * The key in the JSON for the pieces of a player state.
     */
    public static final @Nonnull String PLAYER_PIECES_KEY = "pieces";

    /**
     * The key in the JSON for the score of a player state.
     */
    public static final @Nonnull String PLAYER_SCORE_KEY = "score";

    private final @Nonnull NameMap<?, ? extends BoardShapeFactory> boardShapes;
    private final @Nonnull NameMap<?, ? extends PathPairFactory> pathPairs;
    private final @Nonnull NameMap<?, ? extends DiceFactory<R>> dice;
    private final @Nonnull RuleSetProvider<P, S> ruleSetProvider;

    /**
     * A factory to build generators to write the JSON.
     */
    private final @Nonnull JsonFactory jsonFactory;

    /**
     * A mapper to use for parsing JSON into a tree.
     */
    private final @Nonnull ObjectMapper objectMapper;

    /**
     * Helper functions for type-checking JSON while reading.
     */
    private final @Nonnull JsonHelper helper;

    /**
     * Instantiates the JSON notation to encode and decode games.
     * @param pathPairs The paths that can be parsed in this notation.
     * @param boardShapes The board shapes that can be parsed in this notation.
     * @param dice The dice that can be parsed in this notation.
     */
    public JsonNotation(
            @Nonnull NameMap<?, ? extends BoardShapeFactory> boardShapes,
            @Nonnull NameMap<?, ? extends PathPairFactory> pathPairs,
            @Nonnull NameMap<?, ? extends DiceFactory<R>> dice,
            @Nonnull RuleSetProvider<P, S> ruleSetProvider
    ) {
        this.boardShapes = boardShapes;
        this.pathPairs = pathPairs;
        this.dice = dice;
        this.ruleSetProvider = ruleSetProvider;
        this.jsonFactory = JsonFactory.builder().build();
        this.objectMapper = new ObjectMapper(jsonFactory);
        this.helper = new JsonHelper();
    }

    public static @Nonnull JsonNotation<Piece, PlayerState, Roll> createSimple() {
        return new JsonNotation<>(
                BoardType.FACTORIES,
                PathType.FACTORIES,
                DiceType.FACTORIES,
                new SimpleRuleSetProvider()
        );
    }

    public void writeRoll(
            @Nonnull JsonGenerator generator,
            @Nonnull R roll
    ) throws IOException {

        generator.writeNumberField(ROLL_VALUE_KEY, roll.value());
    }

    public void writePiece(
            @Nonnull JsonGenerator generator,
            @Nonnull P piece
    ) throws IOException {

        generator.writeStringField(PIECE_OWNER_KEY, piece.getOwner().getCharStr());
        generator.writeNumberField(PIECE_INDEX_KEY, piece.getPathIndex());
    }

    protected void writePieceField(
            @Nonnull JsonGenerator generator,
            @Nonnull String fieldName,
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
            @Nonnull JsonGenerator generator,
            @Nonnull Move<P> move
    ) throws IOException {

        P sourcePiece = (move.hasSource() ? move.getSourcePiece() : null);
        writePieceField(generator, MOVE_SOURCE_KEY, sourcePiece);

        P destPiece = (move.hasDest() ? move.getDestPiece() : null);
        writePieceField(generator, MOVE_DEST_KEY, destPiece);

        P capturedPiece = (move.isCapture() ? move.getCapturedPiece() : null);
        writePieceField(generator, MOVE_CAPTURED_KEY, capturedPiece);
    }

    public void writeMoveList(
            @Nonnull JsonGenerator generator,
            @Nonnull List<Move<P>> moves
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
            @Nonnull JsonGenerator generator,
            @Nonnull Board<P> board
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
            @Nonnull JsonGenerator generator,
            @Nonnull S playerState
    ) throws IOException {

        generator.writeNumberField(PLAYER_PIECES_KEY, playerState.getPieceCount());
        generator.writeNumberField(PLAYER_SCORE_KEY, playerState.getScore());
    }

    public void writeRolledState(
            @Nonnull JsonGenerator generator,
            @Nonnull RolledGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull MovedGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull ActionGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull WaitingForRollGameState<P, S, R> state
    ) {
        // Nothing to include.
    }

    public void writeWaitingForMoveState(
            @Nonnull JsonGenerator generator,
            @Nonnull WaitingForMoveGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull PlayableGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull OngoingGameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull WinGameState<P, S, R> state
    ) throws IOException {

        generator.writeStringField(WINNER_KEY, state.getWinner().getCharStr());
    }

    public @Nonnull String getStateType(
            @Nonnull GameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull GameState<P, S, R> state
    ) throws IOException {

        generator.writeStringField(STATE_TYPE_KEY, getStateType(state));

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

        if (state instanceof OngoingGameState<P, S, R> ongoingState) {
            writeOngoingState(generator, ongoingState);

        } else if (state instanceof WinGameState<P, S, R> winState) {
            writeWinState(generator, winState);

        } else {
            throw new IllegalArgumentException("Unknown game state type " + state.getClass());
        }
    }

    public void writeStates(
            @Nonnull JsonGenerator generator,
            @Nonnull List<GameState<P, S, R>> states
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
            @Nonnull JsonGenerator generator,
            @Nonnull GameState<P, S, R> state
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
            @Nonnull JsonGenerator generator,
            @Nonnull GameSettings<R> settings
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
            @Nonnull JsonGenerator generator,
            @Nonnull GameMetadata metadata
    ) throws IOException {

        for (Map.Entry<String, String> entry : metadata.getAll().entrySet()) {
            generator.writeStringField(entry.getKey(), entry.getValue());
        }
    }

    public void writeGame(
            @Nonnull JsonGenerator generator,
            @Nonnull Game<P, S, R> game
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
    public @Nonnull String encodeGame(
            @Nonnull Game<P, S, R> game
    ) {
        // Write the JSON into a String buffer.
        Writer writer = new StringWriter();
        try (JsonGenerator generator = jsonFactory.createGenerator(writer)) {

            // Write the game.
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

    public @Nonnull R readRoll(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ObjectNode json
    ) {
        int rollValue = helper.readInt(json, ROLL_VALUE_KEY);
        return rules.getDiceFactory().createRoll(rollValue);
    }

    public @Nonnull P readPiece(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ObjectNode json
    ) {

        char ownerChar = helper.readChar(json, JsonNotation.PIECE_OWNER_KEY);
        PlayerType owner = PlayerType.getByChar(ownerChar);

        int pathIndex = helper.readInt(json, JsonNotation.PIECE_INDEX_KEY);
        return rules.getPieceProvider().create(
                owner, pathIndex
        );
    }

    private @Nonnull Tile getTileFromPiece(
            @Nonnull PathPair paths,
            @Nonnull P piece
    ) {
        return paths.get(piece.getOwner()).get(piece.getPathIndex());
    }

    public @Nonnull Move<P> readMove(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ObjectNode json
    ) {

        PathPair paths = rules.getPaths();

        ObjectNode sourceJson = helper.readNullableDict(json, MOVE_SOURCE_KEY);
        P source = (sourceJson != null ? readPiece(rules, sourceJson) : null);
        Tile sourceTile = (source != null ? getTileFromPiece(paths, source) : null);

        ObjectNode destJson = helper.readNullableDict(json, MOVE_DEST_KEY);
        P dest = (destJson != null ? readPiece(rules, destJson) : null);
        Tile destTile = (dest != null ? getTileFromPiece(paths, dest) : null);

        ObjectNode capturedJson = helper.readNullableDict(json, MOVE_CAPTURED_KEY);
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

    public @Nonnull List<Move<P>> readMoveList(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ArrayNode json
    ) {
        List<Move<P>> moves = new ArrayList<>();
        for (int index = 0; index < json.size(); ++index) {
            ObjectNode moveJson = helper.readArrayDictEntry(json, index);
            moves.add(readMove(rules, moveJson));
        }
        return moves;
    }

    public @Nonnull Board<P> readBoard(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ObjectNode json
    ) {

        Board<P> board = new Board<>(rules.getBoardShape());
        ObjectNode piecesJson = helper.readDict(json, BOARD_PIECES_KEY);

        Iterator<String> keyIterator = piecesJson.fieldNames();
        while (keyIterator.hasNext()) {
            String tileKey = keyIterator.next();
            Tile tile = Tile.fromString(tileKey);
            ObjectNode pieceJson = helper.readDict(piecesJson, tileKey);
            P piece = readPiece(rules, pieceJson);
            board.set(tile, piece);
        }
        return board;
    }

    public @Nonnull S readPlayerState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType playerType,
            @Nonnull ObjectNode json
    ) {

        int pieces = helper.readInt(json, PLAYER_PIECES_KEY);
        int score = helper.readInt(json, PLAYER_SCORE_KEY);
        return rules.getPlayerStateProvider().create(
                playerType, pieces, score
        );
    }

    public boolean isActionStateType(@Nonnull String stateType) {
        return stateType.equals(STATE_TYPE_ROLLED)
                || stateType.equals(STATE_TYPE_MOVED);
    }

    public boolean isPlayableGameState(@Nonnull String stateType) {
        return stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)
                || stateType.equals(STATE_TYPE_WAITING_FOR_MOVE);
    }

    public boolean isOngoingGameState(@Nonnull String stateType) {
        return isActionStateType(stateType) || isPlayableGameState(stateType);
    }

    public @Nonnull RolledGameState<P, S, R> readRolledState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull PlayerType turn
    ) {
        ObjectNode rollJson = helper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);
        return stateSource.createRolledState(rules, turn, roll);
    }

    public @Nonnull MovedGameState<P, S, R> readMovedState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull PlayerType turn
    ) {
        ObjectNode rollJson = helper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);

        ObjectNode moveJson = helper.readDict(json, MOVE_KEY);
        Move<P> move = readMove(rules, moveJson);

        return stateSource.createMovedState(rules, turn, roll, move);
    }

    public @Nonnull WaitingForRollGameState<P, S, R> readWaitingForRollState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull PlayerType turn
    ) {
        return stateSource.createWaitingForRollState(rules, turn);
    }

    public @Nonnull WaitingForMoveGameState<P, S, R> readWaitingForMoveState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull PlayerType turn
    ) {
        ObjectNode rollJson = helper.readDict(json, ROLL_KEY);
        R roll = readRoll(rules, rollJson);
        return stateSource.createWaitingForMoveState(rules, turn, roll);
    }

    public @Nonnull ActionGameState<P, S, R> readActionState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull String stateType,
            @Nonnull PlayerType turn
    ) {

        if (stateType.equals(STATE_TYPE_ROLLED)) {
            return readRolledState(rules, stateSource, json, turn);

        } else if (stateType.equals(STATE_TYPE_MOVED)) {
            return readMovedState(rules, stateSource, json, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown action state type: " + stateType);
        }
    }

    public @Nonnull PlayableGameState<P, S, R> readPlayableState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull String stateType,
            @Nonnull PlayerType turn
    ) {

        if (stateType.equals(STATE_TYPE_WAITING_FOR_ROLL)) {
            return readWaitingForRollState(rules, stateSource, json, turn);

        } else if (stateType.equals(STATE_TYPE_WAITING_FOR_MOVE)) {
            return readWaitingForMoveState(rules, stateSource, json, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown playable state type: " + stateType);
        }
    }

    public @Nonnull OngoingGameState<P, S, R> readOngoingState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json,
            @Nonnull String stateType
    ) {

        char turnChar = helper.readChar(json, TURN_KEY);
        PlayerType turn = PlayerType.getByChar(turnChar);

        if (isActionStateType(stateType)) {
            return readActionState(rules, stateSource, json, stateType, turn);

        } else if (isPlayableGameState(stateType)) {
            return readPlayableState(rules, stateSource, json, stateType, turn);

        } else {
            throw new JsonHelper.JsonReadError("Unknown ongoing state type: " + stateType);
        }
    }

    public @Nonnull WinGameState<P, S, R> readWinState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json
    ) {
        char winnerChar = helper.readChar(json, WINNER_KEY);
        PlayerType winner = PlayerType.getByChar(winnerChar);

        return stateSource.createWinState(rules, winner);
    }

    public @Nonnull GameState<P, S, R> readState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StateSource<P, S, R> stateSource,
            @Nonnull ObjectNode json
    ) {
        String stateType = helper.readString(json, STATE_TYPE_KEY);

        if (isOngoingGameState(stateType)) {
            return readOngoingState(rules, stateSource, json, stateType);

        } else if (stateType.equals(STATE_TYPE_WIN)) {
            return readWinState(rules, stateSource, json);

        } else {
            throw new JsonHelper.JsonReadError("Unknown state type: " + stateType);
        }
    }

    public @Nonnull GameState<P, S, R> readInitialState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ObjectNode json
    ) {
        ObjectNode boardJson = helper.readDict(json, BOARD_KEY);
        ObjectNode playersJson = helper.readDict(json, PLAYERS_KEY);
        ObjectNode lightPlayerJson = helper.readDict(playersJson, PlayerType.LIGHT.getCharStr());
        ObjectNode darkPlayerJson = helper.readDict(playersJson, PlayerType.DARK.getCharStr());

        Board<P> board = readBoard(rules, boardJson);
        S lightPlayer = readPlayerState(rules, PlayerType.LIGHT, lightPlayerJson);
        S darkPlayer = readPlayerState(rules, PlayerType.DARK, darkPlayerJson);

        StateSource<P, S, R> stateSource = new FullStateSource<>(
                board, lightPlayer, darkPlayer
        );
        return readState(rules, stateSource, json);
    }

    public @Nonnull List<GameState<P, S, R>> readStates(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull GameState<P, S, R> initialState,
            @Nonnull ArrayNode json
    ) {
        DerivedStateSource<P, S, R> stateSource = new DerivedStateSource<>(initialState);
        int lastIndex = -1;
        for (int jsonIndex = 0; jsonIndex < json.size(); ++jsonIndex) {
            ObjectNode stateJson = helper.readArrayDictEntry(json, jsonIndex);
            GameState<P, S, R> state = readState(rules, stateSource, stateJson);

            int index = stateSource.lastIndexOf(state);
            if (index <= lastIndex)
                throw new RuntimeException("DerivedStateSource did not include states in read order");

            lastIndex = index;
        }
        return stateSource.getAllStates();
    }

    public @Nonnull GameSettings<R> readGameSettings(@Nonnull ObjectNode json) {
        String boardShapeName = helper.readString(json, BOARD_SHAPE_KEY);
        String pathsName = helper.readString(json, PATHS_KEY);
        String diceName = helper.readString(json, DICE_KEY);
        int startingPieceCount = helper.readInt(json, STARTING_PIECE_COUNT_KEY);
        boolean safeRosettes = helper.readBool(json, SAFE_ROSETTES_KEY);
        boolean rosettesGrantExtraRolls = helper.readBool(
                json, ROSETTES_GRANT_EXTRA_ROLLS_KEY
        );
        boolean capturesGrantExtraRolls = helper.readBool(
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

    public @Nonnull GameMetadata readMetadata(@Nonnull ObjectNode json) {
        return new GameMetadata();
    }

    public @Nonnull Game<P, S, R> readGameV1(@Nonnull ObjectNode json) {

        ObjectNode metadataJson = helper.readDict(json, METADATA_KEY);
        GameMetadata metadata = readMetadata(metadataJson);

        ObjectNode settingsJson = helper.readDict(json, SETTINGS_KEY);
        GameSettings<R> settings = readGameSettings(settingsJson);
        RuleSet<P, S, R> rules = ruleSetProvider.create(settings, metadata);

        ObjectNode initialStateJson = helper.readDict(json, INITIAL_STATE_KEY);
        GameState<P, S, R> initialState = readInitialState(rules, initialStateJson);

        ArrayNode statesJson = helper.readArray(json, STATES_KEY);
        List<GameState<P, S, R>> states = readStates(rules, initialState, statesJson);

        List<GameState<P, S, R>> allStates = new ArrayList<>(states.size() + 1);
        allStates.add(initialState);
        allStates.addAll(states);
        return new Game<>(rules, metadata, allStates);
    }

    public @Nonnull Game<P, S, R> readGame(@Nonnull ObjectNode json) {

        int version = helper.readInt(json, VERSION_KEY);
        if (version == 1)
            return readGameV1(json);

        throw new JsonHelper.JsonReadError("Unknown JSON-Notation version: " + version);
    }

    @Override
    public @Nonnull Game<P, S, R> decodeGame(@Nonnull String encoded) {

        try (JsonParser parser = jsonFactory.createParser(encoded)) {

            JsonNode json = objectMapper.readTree(parser);
            if (!(json instanceof ObjectNode objectNode)) {
                throw new JsonHelper.JsonReadError(
                        "Expected an object, not a " + json.getNodeType().name()
                );
            }
            return readGame(objectNode);

        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON", e);
        }
    }
}
