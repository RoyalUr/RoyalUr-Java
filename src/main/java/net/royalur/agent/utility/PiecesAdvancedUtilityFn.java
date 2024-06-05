package net.royalur.agent.utility;


import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.fast.FastSimpleGame;

/**
 * Scores game states based upon how far pieces have been advanced
 * by each player.
 */
public class PiecesAdvancedUtilityFn extends UtilityFunction {

    private final int scoredPieceUtility;

    public PiecesAdvancedUtilityFn(RuleSet rules) {
        int pathLength = rules.getPaths().getLight().size();
        if (pathLength != rules.getPaths().getDark().size()) {
            throw new IllegalArgumentException(
                    "Paths of different sizes for light and dark are not supported"
            );
        }
        this.scoredPieceUtility = pathLength + 1;
    }

    @Override
    public float scoreGameStateForLight(FastSimpleGame game) {
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
