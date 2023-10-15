package net.royalur.rules;

import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * Creates rule sets to match game settings.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 */
public interface RuleSetProvider<P extends Piece, S extends PlayerState> {

    /**
     * Creates a rule set to match the given settings and game metadata.
     * @param settings The settings of the game.
     * @param metadata The metadata associated with the game.
     * @param <R> The type of rolls that may be made.
     * @return A rule set matching the given settings and game metadata.
     */
    <R extends Roll> @Nonnull RuleSet<P, S, R> create(
            @Nonnull GameSettings<R> settings,
            @Nonnull GameMetadata metadata
    );
}
