package net.royalur.rules.simple;

import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.*;

import javax.annotation.Nonnull;

/**
 * A provider that creates simple rule sets.
 */
public class SimpleRuleSetProvider implements RuleSetProvider<Piece, PlayerState> {

    @Override
    public <R extends Roll> @Nonnull SimpleRuleSet<Piece, PlayerState, R> create(
            @Nonnull GameSettings<R> settings,
            @Nonnull GameMetadata metadata
    ) {
        SimplePieceProvider pieceProvider = new SimplePieceProvider();
        SimplePlayerStateProvider stateProvider = new SimplePlayerStateProvider(
                settings.getStartingPieceCount()
        );
        return new SimpleRuleSet<>(
                settings.getBoardShape(),
                settings.getPaths(),
                settings.getDice(),
                pieceProvider,
                stateProvider,
                settings.areRosettesSafe(),
                settings.doRosettesGrantExtraRolls(),
                settings.doCapturesGrantExtraRolls()
        );
    }
}
