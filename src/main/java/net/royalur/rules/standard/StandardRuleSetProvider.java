package net.royalur.rules.standard;

import net.royalur.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.*;

import javax.annotation.Nonnull;

/**
 * A provider that creates standard rule sets.
 */
public class StandardRuleSetProvider implements RuleSetProvider<Piece, PlayerState> {

    @Override
    public <R extends Roll> @Nonnull StandardRuleSet<Piece, PlayerState, R> create(
            @Nonnull GameSettings<R> settings,
            @Nonnull GameMetadata metadata
    ) {
        BasicPieceProvider pieceProvider = new BasicPieceProvider();
        BasicPlayerStateProvider stateProvider = new BasicPlayerStateProvider(
                settings.getStartingPieceCount()
        );
        return new StandardRuleSet<>(
                settings.getBoardShape(),
                settings.getPaths(),
                settings.getDice(),
                settings.areRosettesSafe(),
                settings.doRosettesGrantExtraRolls(),
                settings.doCapturesGrantExtraRolls(),
                pieceProvider,
                stateProvider
        );
    }
}
