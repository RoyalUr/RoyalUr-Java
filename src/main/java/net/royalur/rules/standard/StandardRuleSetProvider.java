package net.royalur.rules.standard;

import net.royalur.model.GameSettings;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.PieceProvider;
import net.royalur.rules.PlayerStateProvider;
import net.royalur.rules.RuleSet;
import net.royalur.rules.RuleSetProvider;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A provider that creates standard rule sets.
 */
public class StandardRuleSetProvider implements RuleSetProvider<StandardPiece, PlayerState> {

    public <R extends Roll> @Nonnull PieceProvider<StandardPiece> createPieceProvider(
            @Nonnull GameSettings<R> settings,
            @Nonnull Map<String, String> metadata
    ) {
        return new StandardPieceProvider();
    }

    public <R extends Roll> @Nonnull PlayerStateProvider<PlayerState> createPlayerStateProvider(
            @Nonnull GameSettings<R> settings,
            @Nonnull Map<String, String> metadata
    ) {
        return new StandardPlayerStateProvider(settings.getStartingPieceCount());
    }

    @Override
    public <R extends Roll> @Nonnull StandardRuleSet<StandardPiece, PlayerState, R> create(
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
