package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * RGN stands for Royal Game Notation. This is a textual format
 * that is intended to be readable by both humans and machines.
 * This notation is inspired by Chess' PGN (Portable Game Notation).
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class RGNNotation<P extends Piece, S extends PlayerState, R extends Roll> extends Notation<P, S, R> {

    /**
     * The rules to use to generate the game. This is important,
     * as some implicit information may only be available by
     * playing through all the recorded moves. For example,
     * the index of a piece on its path.
     */
    public final @Nonnull RuleSet<P, S, R> rules;

    public RGNNotation(@Nonnull RuleSet<P, S, R> rules) {
        super("RGN");
        this.rules = rules;
    }

    /**
     * Escapes {@param value} so that it can be included as a metadata value.
     * @param value The value to be escaped.
     * @return The escaped version of {@param value}.
     */
    public static @Nonnull String escape(@Nonnull String value) {
        StringBuilder builder = new StringBuilder("\"");
        for (char ch : value.toCharArray()) {
            if (ch == '"') {
                builder.append("\\\"");
            } else if (ch == '\\') {
                builder.append("\\\\");
            } else {
                builder.append(ch);
            }
        }
        return builder.append('"').toString();
    }

    @Override
    public @Nonnull String encode(@Nonnull Game<P, S, R> game) {
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
        return builder.toString();
    }

    @Override
    public @Nonnull Game<P, S, R> decode(@Nonnull String encoded) {
        throw new UnsupportedOperationException("TODO");
    }
}
