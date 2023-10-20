package net.royalur.notation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPairFactory;
import net.royalur.model.path.PathType;
import net.royalur.model.shape.BoardShapeFactory;
import net.royalur.model.shape.BoardType;
import net.royalur.name.NameMap;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

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
     * The key in the JSON for the value of a roll that was made.
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
     * The key in the JSON for the light player, and
     * the value used to represent the light player.
     */
    public static final @Nonnull String LIGHT_CODE = "light";

    /**
     * The key in the JSON for the dark player, and
     * the value used to represent the dark player.
     */
    public static final @Nonnull String DARK_CODE = "dark";

    /**
     * The key in the JSON for the owner of a piece.
     */
    public static final @Nonnull String PIECE_OWNER_KEY = "owner";

    /**
     * The key in the JSON for the tile that a piece is on.
     */
    public static final @Nonnull String PIECE_TILE_KEY = "tile";

    /**
     * The key in the JSON for the index of a piece on its path.
     */
    public static final @Nonnull String PIECE_INDEX_KEY = "index";

    /**
     * The key in the JSON for the player whose turn it is in a state.
     */
    public static final @Nonnull String STATE_TURN_KEY = "turn";

    /**
     * The key in the JSON for the player that won the game.
     */
    public static final @Nonnull String STATE_WINNER_KEY = "winner";

    /**
     * The key in the JSON for the contents of the board in a game state.
     */
    public static final @Nonnull String STATE_BOARD_KEY = "board";

    /**
     * The key in the JSON for the state of the players.
     */
    public static final @Nonnull String STATE_PLAYERS_KEY = "players";

    /**
     * The key in the JSON for the pieces of a player state.
     */
    public static final @Nonnull String PLAYER_PIECES_KEY = "pieces";

    /**
     * The key in the JSON for the score of a player state.
     */
    public static final @Nonnull String PLAYER_SCORE_KEY = "score";

    /**
     * A factory to build generators to write the JSON.
     */
    private final @Nonnull JsonFactory jsonFactory;

    /**
     * A map of factories for identifying path pairs for parsing.
     */
    private final @Nonnull NameMap<?, ? extends PathPairFactory> pathPairs;

    /**
     * A map of factories for identifying board shapes for parsing.
     */
    private final @Nonnull NameMap<?, ? extends BoardShapeFactory> boardShapes;

    /**
     * Instantiates the JSON notation to encode and decode games.
     * @param pathPairs The paths that can be parsed in this notation.
     * @param boardShapes The board shapes that can be parsed in this notation.
     */
    public JsonNotation(
            @Nonnull NameMap<?, ? extends PathPairFactory> pathPairs,
            @Nonnull NameMap<?, ? extends BoardShapeFactory> boardShapes
    ) {
        this.pathPairs = pathPairs;
        this.boardShapes = boardShapes;
        this.jsonFactory = JsonFactory.builder().build();
    }

    /**
     * Instantiates the JSON notation to encode and decode games.
     */
    public JsonNotation() {
        this(PathType.FACTORIES, BoardType.FACTORIES);
    }

    protected @Nonnull String getPlayerTypeCode(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> LIGHT_CODE;
            case DARK -> DARK_CODE;
        };
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writePlayerState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull S playerState,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeNumberField(PLAYER_PIECES_KEY, playerState.getPieceCount());
        generator.writeNumberField(PLAYER_SCORE_KEY, playerState.getScore());
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeRoll(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull R roll,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeNumberField(ROLL_VALUE_KEY, roll.value());
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writePiece(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull Tile tile,
            @Nonnull P piece,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(PIECE_OWNER_KEY, getPlayerTypeCode(piece.getOwner()));
        generator.writeStringField(PIECE_TILE_KEY, tile.toString());
        generator.writeNumberField(PIECE_INDEX_KEY, piece.getPathIndex());
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeMove(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull Move<P> move,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        // Write the source piece being moved.
        if (!move.isIntroducingPiece()) {
            generator.writeObjectFieldStart(MOVE_SOURCE_KEY);
            try {
                writePiece(rules, move.getSource(), move.getSourcePiece(), generator);
            } finally {
                generator.writeEndObject();
            }
        } else {
            generator.writeNullField(MOVE_SOURCE_KEY);
        }

        // Write the new destination piece.
        if (!move.isScoringPiece()) {
            generator.writeObjectFieldStart(MOVE_DEST_KEY);
            try {
                writePiece(rules, move.getDest(), move.getDestPiece(), generator);
            } finally {
                generator.writeEndObject();
            }
        } else {
            generator.writeNullField(MOVE_DEST_KEY);
        }

        // Write the captured piece.
        if (move.isCapture()) {
            generator.writeObjectFieldStart(MOVE_CAPTURED_KEY);
            try {
                writePiece(rules, move.getDest(), move.getCapturedPiece(), generator);
            } finally {
                generator.writeEndObject();
            }
        } else {
            generator.writeNullField(MOVE_CAPTURED_KEY);
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeRolledState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull RolledGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(rules, state.getRoll(), generator);
        } finally {
            generator.writeEndObject();
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeMovedState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull MovedGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(rules, state.getRoll(), generator);
        } finally {
            generator.writeEndObject();
        }

        generator.writeObjectFieldStart(MOVE_KEY);
        try {
            writeMove(rules, state.getMove(), generator);
        } finally {
            generator.writeEndObject();
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeActionState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull ActionGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        if (state instanceof RolledGameState<P, S, R> rolledState) {
            writeRolledState(rules, rolledState, generator);

        } else if (state instanceof MovedGameState<P, S, R> movedState) {
            writeMovedState(rules, movedState, generator);

        } else {
            throw new IllegalArgumentException("Unknown action game state type " + state.getClass());
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeWaitingForRollState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull WaitingForRollGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) {

        // Nothing to do.
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeWaitingForMoveState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeObjectFieldStart(ROLL_KEY);
        try {
            writeRoll(rules, state.getRoll(), generator);
        } finally {
            generator.writeEndObject();
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writePlayableState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayableGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        if (state instanceof WaitingForRollGameState<P, S, R> waitingForRollState) {
            writeWaitingForRollState(rules, waitingForRollState, generator);

        } else if (state instanceof WaitingForMoveGameState<P, S, R> waitingForMoveState) {
            writeWaitingForMoveState(rules, waitingForMoveState, generator);

        } else {
            throw new IllegalArgumentException("Unknown playable game state type " + state.getClass());
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeOngoingState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull OngoingGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(STATE_TURN_KEY, getPlayerTypeCode(state.getTurn()));

        if (state instanceof ActionGameState<P, S, R> actionState) {
            writeActionState(rules, actionState, generator);

        } else if (state instanceof PlayableGameState<P, S, R> playableState) {
            writePlayableState(rules, playableState, generator);

        } else {
            throw new IllegalArgumentException("Unknown ongoing game state type " + state.getClass());
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeWinState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull WinGameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(STATE_WINNER_KEY, getPlayerTypeCode(state.getWinner()));
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull String getStateType(
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

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull GameState<P, S, R> state,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(STATE_TYPE_KEY, getStateType(state));
        generator.writeStringField(STATE_BOARD_KEY, state.getBoard().toString("", false));

        // Write the states of the players.
        generator.writeObjectFieldStart(STATE_PLAYERS_KEY);
        try {
            // Light player.
            generator.writeObjectFieldStart(LIGHT_CODE);
            try {
                writePlayerState(rules, state.getLightPlayer(), generator);
            } finally {
                generator.writeEndObject();
            }

            // Dark player.
            generator.writeObjectFieldStart(DARK_CODE);
            try {
                writePlayerState(rules, state.getDarkPlayer(), generator);
            } finally {
                generator.writeEndObject();
            }
        } finally {
            generator.writeEndObject();
        }

        // Write more detailed information about each state.
        if (state instanceof OngoingGameState<P, S, R> ongoingState) {
            writeOngoingState(rules, ongoingState, generator);

        } else if (state instanceof WinGameState<P, S, R> winState) {
            writeWinState(rules, winState, generator);

        } else {
            throw new IllegalArgumentException("Unknown game state type " + state.getClass());
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeStates(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        for (GameState<P, S, R> state : game.getLandmarkStates()) {
            generator.writeStartObject();
            try {
                writeState(game.getRules(), state, generator);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    protected void writeMetadata(
            @Nonnull GameMetadata metadata,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        for (Map.Entry<String, String> entry : metadata.getAll().entrySet()) {
            generator.writeStringField(entry.getKey(), entry.getValue());
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeGame(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        // Write the version of the notation.
        generator.writeNumberField(VERSION_KEY, LATEST_VERSION);

        // Write the metadata of the game.
        generator.writeObjectFieldStart(METADATA_KEY);
        try {
            writeMetadata(game.getMetadata(), generator);
        } finally {
            generator.writeEndObject();
        }

        // Write the states in the game.
        generator.writeArrayFieldStart(STATES_KEY);
        try {
            writeStates(game, generator);
        } finally {
            generator.writeEndArray();
        }
    }

    @Override
    public @Nonnull <P extends Piece, S extends PlayerState, R extends Roll> String encodeGame(
            @Nonnull Game<P, S, R> game
    ) {

        // Write the JSON into a String buffer.
        Writer writer = new StringWriter();
        JsonGenerator generator;
        try {
            generator = jsonFactory.createGenerator(writer);
            generator.useDefaultPrettyPrinter();

            // Write the game.
            generator.writeStartObject();
            try {
                writeGame(game, generator);
            } finally {
                generator.writeEndObject();
                generator.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON", e);
        }
        return writer.toString();
    }

    @Override
    public @Nonnull <P extends Piece, S extends PlayerState, R extends Roll> Game<P, S, R> decodeGame(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull String encoded
    ) {

        throw new UnsupportedOperationException();
    }
}
