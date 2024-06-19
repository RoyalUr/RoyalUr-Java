package net.royalur.rules.simple;

import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.rules.*;


/**
 * A provider that creates simple rule sets.
 */
public class SimpleRuleSetProvider implements RuleSetProvider {

    @Override
    public SimpleRuleSet create(
            GameSettings settings,
            GameMetadata metadata
    ) {
        SimplePieceProvider pieceProvider = new SimplePieceProvider();
        SimplePlayerStateProvider stateProvider = new SimplePlayerStateProvider(
                settings.getStartingPieceCount()
        );
        return new SimpleRuleSet(
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
