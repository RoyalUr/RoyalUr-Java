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
 */
public class RGN extends Notation {

    /**
     * The identifier given to the RGN notation.
     */
    public static final String ID = "RGN";

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
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
    public @Nonnull String encodeGame(@Nonnull Game<?, ?, ?> game) {
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
    public <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull Game<P, S, R>
    decodeGame(@Nonnull RuleSet<P, S, R> rules, @Nonnull String encoded) {

        throw new UnsupportedOperationException("TODO");
    }
}
