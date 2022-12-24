package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.state.ActionGameState;
import net.royalur.model.state.MovedGameState;
import net.royalur.model.state.RolledGameState;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * RGN stands for Royal Game Notation. This is a textual format
 * that is intended to be readable by both humans and machines.
 * This notation is inspired by Chess' PGN (Portable Game Notation).
 * <p>
 * Additional thanks to the following contributors from the Royal
 * Game of Ur Discord server for their help in discussing the
 * merits of different features for the notation: Monomino,
 * Sachertorte, and Diego Raposo.
 */
public class RGN extends Notation {

    /**
     * The identifier given to the RGN notation.
     */
    public static final String ID = "RGN";

    /**
     * The default maximum length for lines in RGN that
     * encode the actions taken in a game.
     */
    public static final int DEFAULT_MAX_ACTION_LINE_LENGTH = 40;

    /**
     * The maximum length of the lines that contain moves.
     * This does not apply to the metadata lines.
     */
    public final int maxActionLineLength;

    /**
     * Instantiates the RGN notation to encode and decode games.
     * @param maxActionLineLength The maximum length of the lines that contain moves.
     */
    public RGN(int maxActionLineLength) {
        this.maxActionLineLength = maxActionLineLength;
    }

    /**
     * Instantiates the RGN notation to encode and decode games,
     * using the default maximum length of action lines.
     */
    public RGN() {
        this(DEFAULT_MAX_ACTION_LINE_LENGTH);
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }

    /**
     * Escapes {@code value} so that it can be included as a metadata value.
     * @param value The value to be escaped.
     * @return The escaped version of {@code value}.
     */
    public static @Nonnull String escape(@Nonnull String value) {
        StringBuilder builder = new StringBuilder("\"");
        for (char ch : value.toCharArray()) {
            if (ch == '"') {
                builder.append("\\\"");
            } else if (ch == '\\') {
                builder.append("\\\\");
            } else if (ch == '\r') {
                builder.append("\\r");
            } else if (ch == '\n') {
                builder.append("\\n");
            } else {
                builder.append(ch);
            }
        }
        return builder.append('"').toString();
    }

    /**
     * Encodes the dice roll from {@code rolledState} into {@code builder}.
     * @param rules The rules of the game in which the dice are being encoded.
     * @param builder The builder into which to append the encoded dice roll.
     * @param rolledState The state of the game that contains the dice roll to encode.
     * @param <P> The type of pieces that exist on the board in the given state.
     * @param <S> The type of the player state that is stored in the given state.
     * @param <R> The type of the dice rolls that was made in the given state.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void appendDiceRoll(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StringBuilder builder,
            @Nonnull RolledGameState<P, S, R> rolledState
    ) {
        Roll roll = rolledState.roll;
        builder.append("r").append(roll.value);
    }

    /**
     * Encodes the move from {@code movedState} into {@code builder}.
     * @param rules The rules of the game in which the dice are being encoded.
     * @param builder The builder into which to append the encoded move.
     * @param movedState The state of the game that contains the move to encode.
     * @param <P> The type of pieces that exist on the board in the given state.
     * @param <S> The type of the player state that is stored in the given state.
     * @param <R> The type of the dice rolls that was used in the given state.
     */
    protected <P extends Piece, S extends PlayerState, R extends Roll> void appendMove(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull StringBuilder builder,
            @Nonnull MovedGameState<P, S, R> movedState
    ) {
        Move<?> move = movedState.move;
        Tile from, to;

        // Get the origin tile.
        if (move.isIntroducingPiece()) {
            from = rules.paths.get(move.player).startTile;
        } else {
            from = move.getSource();
        }

        // Get the destination tile.
        if (move.isScoringPiece()) {
            to = rules.paths.get(move.player).endTile;
        } else {
            to = move.getDestination();
        }

        // Include the source coordinate.
        if (from.x != to.x) {
            from.encodeXLowerCase(builder);
        }
        if (from.y != to.y) {
            from.encodeY(builder);
        }

        // Record that a piece was captured.
        if (move.capturesPiece()) {
            builder.append("x");
        }

        // Include the destination coordinates.
        to.encodeXLowerCase(builder);
        to.encodeY(builder);

        // Mark if a rosette was reached.
        if (move.isLandingOnRosette(rules.boardShape)) {
            builder.append("+");
        }
    }

    @Override
    public <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull String
    encodeGame(@Nonnull Game<P, S, R> game) {

        StringBuilder builder = new StringBuilder();

        // Encode the metadata.
        for (Map.Entry<String, String> item : game.getMetadata().entrySet()) {
            builder.append("[")
                    .append(item.getKey())
                    .append(" ")
                    .append(escape(item.getValue()))
                    .append("]\n");
        }
        if (builder.length() > 0) {
            builder.append("\n");
        }

        // Encode the moves.
        int turn = 0;
        Player turnPlayer = null;
        boolean first = true;
        int lineLength = 0;
        StringBuilder actionBuilder = new StringBuilder();

        List<ActionGameState<P, S, R>> states = game.getActionStates();
        for (int index = 0; index < states.size(); ++index) {
            ActionGameState<P, S, R> actionState = states.get(index);
            RolledGameState<P, S, R> rollState = null;
            MovedGameState<P, S, R> moveState = null;

            if (actionState instanceof RolledGameState) {
                // If a roll is followed by a move, then wait for the move to encode it.
                if (index + 1 < states.size() && states.get(index + 1) instanceof MovedGameState)
                    continue;

                // Otherwise, encode it on its own.
                rollState = (RolledGameState<P, S, R>) actionState;
            } else if (actionState instanceof MovedGameState) {
                // Get the move.
                moveState = (MovedGameState<P, S, R>) actionState;

                // Try to find a roll to associate with this move.
                if (index > 0 && states.get(index - 1) instanceof RolledGameState) {
                    rollState = (RolledGameState<P, S, R>) states.get(index - 1);
                }
            } else {
                throw new IllegalArgumentException("Unknown action state type " + actionState.getClass());
            }

            // Reset the builder to encode this action.
            actionBuilder.setLength(0);

            // Detect new turns.
            Player currentPlayer = actionState.getTurnPlayer().player;
            if (turnPlayer != currentPlayer) {
                turn += 1;
                turnPlayer = currentPlayer;
                actionBuilder.append("(").append(turn).append(") ");
            }

            // Add in the rolls and moves that were made.
            if (rollState != null) {
                appendDiceRoll(game.rules, actionBuilder, rollState);
            }
            if (rollState != null && moveState != null) {
                actionBuilder.append(".");
            }
            if (moveState != null) {
                appendMove(game.rules, actionBuilder, moveState);
            }
            if (index == states.size() - 1 && game.isFinished()) {
                actionBuilder.append("#");
            }

            // Add the action to the encoded string. We wrap to a new
            // line if required to maintain the maximum line length.
            int actionLength = actionBuilder.length();
            lineLength += actionLength;

            if (!first) {
                if (lineLength + 1 < maxActionLineLength) {
                    builder.append(" ");
                    lineLength += 1;
                } else {
                    builder.append("\n");
                    lineLength = actionLength;
                }
            } else {
                first = false;
            }
            builder.append(actionBuilder);
        }
        return builder.toString();
    }

    @Override
    public <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull Game<P, S, R>
    decodeGame(@Nonnull RuleSet<P, S, R> rules, @Nonnull String encoded) {

        throw new UnsupportedOperationException("TODO");
    }
}
