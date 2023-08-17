package net.royalur.agent.utility;

import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.name.TextName;
import net.royalur.rules.RuleSet;
import net.royalur.rules.standard.StandardPiece;
import net.royalur.rules.state.GameState;

import javax.annotation.Nonnull;

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
    public float scoreGameStateForLight(
            @Nonnull GameState<StandardPiece, PlayerState, Roll> state
    ) {
        int lightScore = state.getLightPlayer().getScore();
        int darkScore = state.getDarkPlayer().getScore();
        float utility = (lightScore - darkScore) * scoredPieceUtility;

        for (StandardPiece piece : state.getBoard()) {
            utility += piece.getPathIndex() + 1;
        }
        return utility;
    }
}
