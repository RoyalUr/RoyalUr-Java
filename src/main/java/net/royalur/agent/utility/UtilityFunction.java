package net.royalur.agent.utility;

import net.royalur.name.Name;
import net.royalur.name.Named;
import net.royalur.rules.standard.fast.FastGame;

import javax.annotation.Nonnull;

/**
 * A function that is used to score game states.
 */
public abstract class UtilityFunction implements Named<Name> {

    /**
     * The name of this path pair.
     */
    private final @Nonnull Name name;

    /**
     * Instantiates a utility function.
     * @param name The name of this utility function.
     */
    public UtilityFunction(@Nonnull Name name) {
        this.name = name;
    }

    @Override
    public @Nonnull Name getName() {
        return name;
    }

    /**
     * Scores the state of the game numerically, where a positive
     * value represents that light is advantaged, and a negative
     * value represents that dark is advantaged.
     * @param game The game to evaluate.
     * @return A utility value for light in the given state.
     */
    public abstract float scoreGameStateForLight(@Nonnull FastGame game);

    /**
     * Scores the state of the game numerically, where a positive
     * value represents that the current player is advantaged, and
     * a negative value represents that the waiting player is advantaged.
     * @param game The game to evaluate.
     * @return A utility value for the current player of the game.
     */
    public float scoreGame(@Nonnull FastGame game) {
        float lightUtility = scoreGameStateForLight(game);
        return game.isLightTurn ? lightUtility : -lightUtility;
    }
}
