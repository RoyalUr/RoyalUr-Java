package net.royalur.rules.standard;

import net.royalur.Game;
import net.royalur.model.GameState;
import net.royalur.model.PlayerIdentity;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A game of the Royal Game of Ur that uses a simple rule set.
 * This class exists solely to reduce the need to use as many generics.
 *
 * TODO : Turn into a delegate.
 */
public class StandardGame extends Game<StandardPiece, PlayerState, Roll> {

    /**
     * Instantiates a simple game of the Royal Game of Ur.
     * @param rules      The set of rules that are being used for this game.
     * @param lightIdentity The identity of the light player.
     * @param darkIdentity The identity of the dark player.
     * @param gameStates The states that have occurred so far in the game.
     */
    public StandardGame(
            @Nonnull StandardRuleSet<StandardPiece, PlayerState, Roll> rules,
            @Nonnull PlayerIdentity lightIdentity,
            @Nonnull PlayerIdentity darkIdentity,
            @Nonnull List<GameState<StandardPiece, PlayerState, Roll>> gameStates
    ) {
        super(rules, lightIdentity, darkIdentity, gameStates);
    }
}
