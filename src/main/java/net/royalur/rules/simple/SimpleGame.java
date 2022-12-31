package net.royalur.rules.simple;

import net.royalur.Game;
import net.royalur.model.GameState;
import net.royalur.model.PlayerIdentity;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A game of the Royal Game of Ur that uses a simple rule set.
 * This class exists to reduce the need to use as many generics.
 */
public class SimpleGame extends Game<SimplePiece, PlayerState, Roll> {

    /**
     * Instantiates a simple game of the Royal Game of Ur.
     * @param rules      The set of rules that are being used for this game.
     * @param lightIdentity The identity of the light player.
     * @param darkIdentity The identity of the dark player.
     * @param gameStates The states that have occurred so far in the game.
     */
    public SimpleGame(
            @Nonnull SimpleRuleSet<SimplePiece, PlayerState, Roll> rules,
            @Nonnull PlayerIdentity lightIdentity,
            @Nonnull PlayerIdentity darkIdentity,
            @Nonnull List<GameState<SimplePiece, PlayerState, Roll>> gameStates
    ) {
        super(rules, lightIdentity, darkIdentity, gameStates);
    }
}
