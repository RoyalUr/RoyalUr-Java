package net.royalur.rules.simple;

import net.royalur.model.*;
import net.royalur.rules.Dice;

import javax.annotation.Nonnull;


/**
 * A concrete implementation of the simple rule set, {@link SimpleRuleSet}.
 * @param <R> The type of rolls that are made by the dice.
 */
public class ConcreteSimpleRuleSet<R extends Roll> extends SimpleRuleSet<SimplePiece, PlayerState, R> {

    /**
     * Instantiate a simple rule set.
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param dice The dice that are used to generate dice rolls.
     * @param startingPieceCount The number of pieces that each player starts with.
     */
    public ConcreteSimpleRuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull Dice<R> dice,
            int startingPieceCount
    ) {
        super(boardShape, paths, dice, startingPieceCount);
    }

    @Override
    public @Nonnull SimplePiece createNewPiece(@Nonnull Player owner, int newPathIndex) {
        return new SimplePiece(owner, newPathIndex);
    }

    @Override
    public @Nonnull SimplePiece createMovedPiece(@Nonnull SimplePiece fromPiece, int newPathIndex) {
        return new SimplePiece(fromPiece.owner, newPathIndex);
    }

    @Override
    public @Nonnull PlayerState generateNewPlayerState(@Nonnull Player player, @Nonnull String name) {
        return new PlayerState(player, name, startingPieceCount, 0);
    }
}
