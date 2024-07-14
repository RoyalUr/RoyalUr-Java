package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPairFactory;
import net.royalur.model.path.PathType;
import net.royalur.model.shape.BoardShapeFactory;
import net.royalur.model.shape.BoardType;
import net.royalur.rules.state.ActionGameState;
import net.royalur.rules.state.MovedGameState;
import net.royalur.rules.state.RolledGameState;
import net.royalur.rules.RuleSet;

import java.util.List;
import java.util.Map;

/**
 * RGN (Royal Game Notation) is a textual format to encode games
 * of the Royal Game of Ur. The notation is intended to be readable
 * by both humans and machines. This notation is inspired by Chess'
 * PGN (Portable Game Notation).
 * <p>
 * RGN was developed by Padraig Lamont with help from several
 * contributors from the Royal Game of Ur Discord server:
 * Airis, Diego Raposo, Monomino, Sachertorte, Capt. Fab., and Raph.
 */
public class RGN implements Notation {

    /**
     * The default maximum length for lines in RGN that
     * encode the actions taken in a game.
     */
    public static final int DEFAULT_MAX_ACTION_LINE_LENGTH = 0;

    /**
     * The default maximum length of each line representing a turn
     * in RGN. A single turn can exceed the max action line length,
     * in which case it will be placed on its own line.
     */
    public static final int DEFAULT_MAX_TURN_LINE_LENGTH = 60;

    /**
     * A map of factories for identifying path pairs for parsing.
     */
    private final Map<String, ? extends PathPairFactory> pathPairs;

    /**
     * A map of factories for identifying board shapes for parsing.
     */
    private final Map<String, ? extends BoardShapeFactory> boardShapes;

    /**
     * The maximum length of the lines that contain moves.
     * This does not apply to the metadata lines.
     */
    private final int maxActionLineLength;

    /**
     * The maximum length of a turn before it is split onto another line.
     * This does not apply to the metadata lines.
     */
    private final int maxTurnLineLength;

    /**
     * Instantiates the RGN notation to encode and decode games.
     * @param pathPairs The paths that can be parsed in this notation.
     * @param boardShapes The board shapes that can be parsed in this notation.
     * @param maxActionLineLength The maximum length of the lines that contain moves.
     * @param maxTurnLineLength The maximum length of a turn before it is split onto another line.
     */
    public RGN(
            Map<String, ? extends PathPairFactory> pathPairs,
            Map<String, ? extends BoardShapeFactory> boardShapes,
            int maxActionLineLength,
            int maxTurnLineLength
    ) {
        this.pathPairs = pathPairs;
        this.boardShapes = boardShapes;
        this.maxActionLineLength = maxActionLineLength;
        this.maxTurnLineLength = maxTurnLineLength;
    }

    /**
     * Instantiates the RGN notation to encode and decode games.
     */
    public RGN() {
        this(
                PathType.PARSING_MAP,
                BoardType.PARSING_MAP,
                DEFAULT_MAX_ACTION_LINE_LENGTH,
                DEFAULT_MAX_TURN_LINE_LENGTH
        );
    }

    /**
     * Escapes {@code value} so that it can be included as a metadata value.
     * @param value The value to be escaped.
     * @return The escaped version of {@code value}.
     */
    public static String escape(String value) {
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
     */
    public void appendDiceRoll(
            RuleSet rules,
            StringBuilder builder,
            RolledGameState rolledState
    ) {
        Roll roll = rolledState.getRoll();
        builder.append(roll.value());
    }

    /**
     * Encodes the move from {@code movedState} into {@code builder}.
     * @param rules The rules of the game in which the dice are being encoded.
     * @param builder The builder into which to append the encoded move.
     * @param move The move to encode.
     */
    public void appendMove(
            RuleSet rules,
            StringBuilder builder,
            Move move
    ) {
        appendMove(rules, builder, move, false);
    }

    /**
     * Encodes the move from {@code movedState} into {@code builder}.
     * @param rules The rules of the game in which the dice are being encoded.
     * @param builder The builder into which to append the encoded move.
     * @param move The move to encode.
     * @param spaceBeforeRosette Whether to include a space before the asterisk
     *                           used to indicate landing on a rosette.
     */
    public void appendMove(
            RuleSet rules,
            StringBuilder builder,
            Move move,
            boolean spaceBeforeRosette
    ) {
        List<Tile> path = rules.getPaths().getWithStartEnd(move.getPlayer());

        if (move.isIntroduction()) {
            path.get(0).encodeX(builder);
            builder.append('@');
        } else {
            move.getSource().encode(builder);
        }

        if (move.isScore()) {
            path.get(path.size() - 1).encodeX(builder);
            builder.append('+');
        } else {
            move.getDest().encode(builder);
        }

        if (move.isCapture()) {
            builder.append('x');
        }
        if (move.isDestRosette(rules.getBoardShape())) {
            if (spaceBeforeRosette) {
                builder.append(' ');
            }
            builder.append('*');
        }
    }

    @Override
    public String encodeGame(Game game) {
        StringBuilder builder = new StringBuilder();

        // Encode the metadata.
        for (Map.Entry<String, String> item : game.getMetadata().getAll().entrySet()) {
            builder.append("[")
                    .append(item.getKey())
                    .append(" ")
                    .append(escape(item.getValue()))
                    .append("]\n");
        }
        if (!builder.isEmpty()) {
            builder.append("\n");
        }

        // Encode the moves.
        int turn = 0;
        PlayerType turnPlayer = null;
        boolean first = true;

        int lineLength = 0;
        int turnLength = 0;
        StringBuilder turnBuilder = new StringBuilder();
        StringBuilder actionBuilder = new StringBuilder();

        List<ActionGameState> states = game.getActionStates();
        for (int index = 0; index < states.size(); ++index) {
            ActionGameState actionState = states.get(index);
            RolledGameState rollState = null;
            MovedGameState moveState = null;

            if (actionState instanceof RolledGameState) {
                // If a roll is followed by a move, then wait for the move to encode it.
                if (index + 1 < states.size() && states.get(index + 1) instanceof MovedGameState)
                    continue;

                // Otherwise, encode it on its own.
                rollState = (RolledGameState) actionState;

            } else if (actionState instanceof MovedGameState) {
                // Get the move.
                moveState = (MovedGameState) actionState;
            } else {
                throw new IllegalArgumentException("Unknown action state type " + actionState.getClass());
            }

            // Reset the builder to encode this action.
            actionBuilder.setLength(0);

            // Detect new turns.
            PlayerType currentPlayer = actionState.getTurnPlayer().getPlayer();
            if (turnPlayer != currentPlayer) {
                turn += 1;
                turnPlayer = currentPlayer;
                if (currentPlayer == PlayerType.LIGHT) {
                    actionBuilder.append((turn + 1) / 2).append(". ");
                } else {
                    actionBuilder.append("/ ");
                }
            }

            // Rolls are only included if there was no move, as the roll
            // can be determined from the move.
            if (rollState != null && moveState == null) {
                appendDiceRoll(game.getRules(), actionBuilder, rollState);
            }

            // If there was a move, include it.
            if (moveState != null) {
                appendMove(game.getRules(), actionBuilder, moveState.getMove(), true);
            } else {
                actionBuilder.append(" --");
            }
            if (index == states.size() - 1 && game.isFinished()) {
                actionBuilder.append("#");
            }

            // Add the action to the turn. We wrap to a new line
            // if required to maintain the maximum line length.
            int actionLength = actionBuilder.length();
            turnLength += actionLength;
            if (!turnBuilder.isEmpty()) {
                if (turnLength + 1 < maxTurnLineLength) {
                    turnBuilder.append(" ");
                    turnLength += 1;
                } else {
                    turnBuilder.append("\n");
                    turnLength = actionLength;
                }
            }
            turnBuilder.append(actionBuilder);

            // Add the turn to the encoded string if there is a new turn.
            if (index + 1 >= states.size()
                    || (currentPlayer != PlayerType.LIGHT && states.get(index + 1).getTurn() != currentPlayer)) {

                lineLength += turnBuilder.length();
                if (!first) {
                    if (lineLength + 1 < maxActionLineLength) {
                        builder.append(" ");
                        lineLength += 1;
                    } else {
                        builder.append("\n");
                        lineLength = turnLength;
                    }
                } else {
                    first = false;
                }
                builder.append(turnBuilder);

                // Reset turnBuilder.
                turnBuilder.setLength(0);
                turnLength = 0;
            }
        }
        return builder.toString();
    }

    @Override
    public Game decodeGame(String encoded) {
        throw new UnsupportedOperationException("TODO");
    }
}
