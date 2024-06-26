package net.royalur.agent.utility;

import net.royalur.rules.simple.fast.FastSimpleGame;

/**
 * A function that is used to score game states.
 */
public abstract class UtilityFunction {

    /**
     * Scores the state of the game numerically, where a positive
     * value represents that light is advantaged, and a negative
     * value represents that dark is advantaged.
     * @param game The game to evaluate.
     * @return A utility value for light in the given state.
     */
    public abstract float scoreGameStateForLight(FastSimpleGame game);

    /**
     * Scores the state of the game numerically, where a positive
     * value represents that the current player is advantaged, and
     * a negative value represents that the waiting player is advantaged.
     * @param game The game to evaluate.
     * @return A utility value for the current player of the game.
     */
    public float scoreGame(FastSimpleGame game) {
        float lightUtility = scoreGameStateForLight(game);
        return game.isLightTurn ? lightUtility : -lightUtility;
    }
}
