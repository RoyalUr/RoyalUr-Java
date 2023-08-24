package net.royalur.rules.standard;

import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.*;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A provider that creates standard rule sets.
 */
public class StandardRuleSetProvider implements RuleSetProvider<Piece, PlayerState> {

    public <R extends Roll> @Nonnull PieceProvider<Piece> createPieceProvider(
            @Nonnull GameSettings<R> settings,
            @Nonnull Map<String, String> metadata
    ) {
        return new BasicPieceProvider();
    }

    public <R extends Roll> @Nonnull PlayerStateProvider<PlayerState> createPlayerStateProvider(
            @Nonnull GameSettings<R> settings,
            @Nonnull Map<String, String> metadata
    ) {
        return new BasicPlayerStateProvider(settings.getStartingPieceCount());
    }

    @Override
    public <R extends Roll> @Nonnull StandardRuleSet<Piece, PlayerState, R> create(
            @Nonnull GameSettings<R> settings,
            @Nonnull Map<String, String> metadata
    ) {
        return new StandardRuleSet<>(
                settings.getBoardShape(),
                settings.getPaths(),
                settings.getDice(),
                settings.areRosettesSafe(),
                settings.doRosettesGrantExtraRolls(),
                settings.doCapturesGrantExtraRolls(),
                createPieceProvider(settings, metadata),
                createPlayerStateProvider(settings, metadata)
        );
    }
}
