package net.royalur.model.dice;

import net.royalur.name.Name;
import net.royalur.name.Named;

import javax.annotation.Nonnull;
import java.util.random.RandomGenerator;

/**
 * A factory that creates dice.
 * @param <R> The type of roll that this dice produce.
 */
public interface DiceFactory<R extends Roll> extends Named<Name> {

    /**
     * Create an instance of the dice using a default source of randomness.
     * @return The instance of the dice using a default source of randomness.
     */
    @Nonnull Dice<R> createDice();
}
