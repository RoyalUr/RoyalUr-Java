package net.royalur.agent.utility;

import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.name.TextName;
import net.royalur.rules.RuleSet;
import net.royalur.rules.standard.StandardPiece;
import net.royalur.rules.standard.fast.FastGame;

import javax.annotation.Nonnull;

/**
 * Scores game states based upon how much pieces have been advanced
 * by each player.
 */
public class PiecesAdvancedUtilityFn extends UtilityFunction<StandardPiece, PlayerState, Roll> {

    private final int scoredPieceUtility;

    public PiecesAdvancedUtilityFn(@Nonnull RuleSet<StandardPiece, PlayerState, Roll> rules) {
        super(new TextName("PiecesAdvanced"));

        int pathLength = rules.getPaths().getLight().size();
        if (pathLength != rules.getPaths().getDark().size()) {
            throw new IllegalArgumentException(
                    "Paths of different sizes for light and dark are not supported"
            );
        }
        this.scoredPieceUtility = pathLength + 1;
    }

    @Override
    public float scoreGameStateForLight(@Nonnull FastGame game) {
        float utility = (game.light.score - game.dark.score) * scoredPieceUtility;
        int[] boardPieces = game.board.pieces;
        for (int boardPiece : boardPieces) {
            // The board piece is already positive for light, negative for dark,
            // and has a magnitude equal to how far each piece has moved.
            utility += boardPiece;
        }
        return utility;
    }
}
