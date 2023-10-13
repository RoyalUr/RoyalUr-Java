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
import net.royalur.util.Cast;

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
public class JsonNotation implements RGUNotation {

    /**
     * The latest version of the JSON notation. If any breaking changes
     * are made to the JSON notation, then this field will be updated
     * to reflect that.
     */
    public static final int LATEST_VERSION = 1;

    /**
     * The key in the JSON for the version of the notation.
     */
    public static final @Nonnull String VERSION_KEY = "notation-version";

    /**
     * The key in the JSON for the metadata of the game.
     */
    public static final @Nonnull String METADATA_KEY = "metadata";

    /**
     * The key in the JSON for the list of actions played in the game.
     */
    public static final @Nonnull String ACTIONS_KEY = "actions";

    /**
     * The key in the JSON for the list of states in the game.
     */
    public static final @Nonnull String STATES_KEY = "states";

    /**
     * The key in the JSON for the type of an action.
     */
    public static final @Nonnull String ACTION_TYPE_KEY = "type";

    /**
     * The key in the JSON for the value of a roll that was made.
     */
    public static final @Nonnull String ACTION_ROLL_KEY = "roll";

    /**
     * The key in the JSON for a move that was made.
     */
    public static final @Nonnull String ACTION_MOVE_KEY = "move";

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
     * The key in the JSON for the owner of a piece.
     */
    public static final @Nonnull String PIECE_OWNER_KEY = "owner";

    /**
     * The key in the JSON for the tile that a piece is on.
     */
    public static final @Nonnull String PIECE_TILE_KEY = "tile";

    /**
     * The key in the JSON for the index of a piece on its path.
     * This is only used for pieces that have an index stored with
     * them, such as {@link Piece}.
     */
    public static final @Nonnull String PIECE_INDEX_KEY = "index";

    /**
     * The key in the JSON for the source tile of a move.
     */
    public static final @Nonnull String MOVE_LANDS_ON_ROSETTE_KEY = "rosette";

    /**
     * The key in the JSON for whether the state represents a won game.
     */
    public static final @Nonnull String STATE_WON_KEY = "is_won";

    /**
     * The key in the JSON for the player whose turn it is in a state.
     */
    public static final @Nonnull String STATE_TURN_KEY = "turn";

    /**
     * The key in the JSON for the player that won the game.
     */
    public static final @Nonnull String STATE_WINNER_KEY = "winner";

    /**
     * The key in the JSON for the player that lost the game.
     */
    public static final @Nonnull String STATE_LOSER_KEY = "loser";

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

    /**
     * Writes the state of a player to the JSON generator.
     * @param playerState The state of the player to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writePlayerState(
            @Nonnull S playerState,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeNumberField(PLAYER_PIECES_KEY, playerState.getPieceCount());
        generator.writeNumberField(PLAYER_SCORE_KEY, playerState.getScore());
    }

    /**
     * Writes the state of a game to the JSON generator.
     * @param state The game state to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeState(
            @Nonnull GameState<P, S, R> state,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeBooleanField(STATE_WON_KEY, state instanceof WinGameState);

        if (state instanceof OngoingGameState<P, S, R> ongoingState) {
            generator.writeStringField(STATE_TURN_KEY, ongoingState.getTurn().getTextName());
        } else if (state instanceof WinGameState<P, S, R> winState) {
            generator.writeStringField(STATE_WINNER_KEY, winState.getWinner().getTextName());
            generator.writeStringField(STATE_LOSER_KEY, winState.getLoser().getTextName());
        }

        generator.writeStringField(STATE_BOARD_KEY, state.getBoard().toString("", false));

        // Write the states of the players.
        generator.writeObjectFieldStart(STATE_PLAYERS_KEY);
        try {
            // Light player.
            generator.writeObjectFieldStart(PlayerType.LIGHT.getTextName());
            try {
                writePlayerState(state.getLightPlayer(), rules, generator);
            } finally {
                generator.writeEndObject();
            }

            // Dark player.
            generator.writeObjectFieldStart(PlayerType.DARK.getTextName());
            try {
                writePlayerState(state.getDarkPlayer(), rules, generator);
            } finally {
                generator.writeEndObject();
            }
        } finally {
            generator.writeEndObject();
        }
    }

    /**
     * Writes a roll that was taken in a game to the JSON generator.
     * @param state The roll game state to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeRolledAction(
            @Nonnull RolledGameState<P, S, R> state,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeNumberField(ACTION_ROLL_KEY, state.getRoll().value());
    }

    /**
     * Writes a piece that is to be moved, or that is on a board, to the JSON generator.
     * @param tile The tile that the piece is on.
     * @param piece The piece to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writePiece(
            @Nonnull Tile tile,
            @Nonnull P piece,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(PIECE_OWNER_KEY, piece.getOwner().getTextName());
        generator.writeStringField(PIECE_TILE_KEY, tile.toString());
        generator.writeNumberField(PIECE_INDEX_KEY, piece.getPathIndex());
    }

    /**
     * Writes a move that was made in a game to the JSON generator.
     * @param move The move to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeMove(
            @Nonnull Move<P> move,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        // Metadata that may be useful for querying or analytics.
        generator.writeBooleanField(MOVE_LANDS_ON_ROSETTE_KEY, move.isDestRosette(rules.getBoardShape()));

        // Write the source piece being moved.
        if (!move.isIntroducingPiece()) {
            generator.writeObjectFieldStart(MOVE_SOURCE_KEY);
            try {
                writePiece(move.getSource(), move.getSourcePiece(), rules, generator);
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
                writePiece(move.getDest(), move.getDestPiece(), rules, generator);
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
                writePiece(move.getDest(), move.getCapturedPiece(), rules, generator);
            } finally {
                generator.writeEndObject();
            }
        } else {
            generator.writeNullField(MOVE_CAPTURED_KEY);
        }
    }

    /**
     * Writes a move action that was made in a game to the JSON generator.
     * @param state The action game state to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeMovedAction(
            @Nonnull MovedGameState<P, S, R> state,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeNumberField(ACTION_ROLL_KEY, state.getRoll().value());

        generator.writeObjectFieldStart(ACTION_MOVE_KEY);
        try {
            writeMove(state.getMove(), rules, generator);
        } finally {
            generator.writeEndObject();
        }
    }

    protected <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull String getActionType(
            @Nonnull ActionGameState<P, S, R> state
    ) {
        if (state instanceof MovedGameState<P,S,R>)
            return "Move";
        if (state instanceof RolledGameState<P,S,R>)
            return "Roll";

        throw new IllegalArgumentException("Unknown action type for given game state class: " + state.getClass());
    }

    /**
     * Writes an action in a game to the JSON generator.
     * @param state The action game state to write.
     * @param rules The rules of the game.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeAction(
            @Nonnull ActionGameState<P, S, R> state,
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        generator.writeStringField(ACTION_TYPE_KEY, getActionType(state));

        if (state instanceof RolledGameState) {
            writeRolledAction(Cast.unsafeCast(state), rules, generator);
        } else if (state instanceof MovedGameState) {
            writeMovedAction(Cast.unsafeCast(state), rules, generator);
        } else {
            throw new IllegalArgumentException("Unknown action game state type " + state.getClass());
        }
    }

    /**
     * Writes the actions of the game to the JSON generator.
     * @param game The game to write the actions of.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeActions(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        for (ActionGameState<P, S, R> state : game.getActionStates()) {
            generator.writeStartObject();
            try {
                writeAction(state, game.getRules(), generator);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    /**
     * Writes the landmark states of the game to the JSON generator.
     * @param game The game to write the states of.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeStates(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        for (GameState<P, S, R> state : game.getLandmarkStates()) {
            generator.writeStartObject();
            try {
                writeState(state, game.getRules(), generator);
            } finally {
                generator.writeEndObject();
            }
        }
    }

    /**
     * Writes the metadata of the game to the JSON generator.
     * @param game The game to write the metadata of.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeMetadata(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        for (Map.Entry<String, String> entry : game.getMetadata().getAll().entrySet()) {
            generator.writeStringField(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Writes the game to the JSON generator.
     * @param game The game to write.
     * @param <P> The type of pieces in the game.
     * @param <S> The type of player state stored in the game.
     * @param <R> The type of roll made in the game.
     * @throws IOException If there is an error writing the JSON.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void writeGame(
            @Nonnull Game<P, S, R> game,
            @Nonnull JsonGenerator generator
    ) throws IOException {

        // Write the version of the notation.
        generator.writeNumberField(VERSION_KEY, LATEST_VERSION);

        // Write the metadata of the game.
        generator.writeObjectFieldStart(METADATA_KEY);
        try {
            writeMetadata(game, generator);
        } finally {
            generator.writeEndObject();
        }

        // Write the actions taken in the game.
        generator.writeArrayFieldStart(ACTIONS_KEY);
        try {
            writeActions(game, generator);
        } finally {
            generator.writeEndArray();
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
