package net.royalur.rules;

import net.royalur.model.Roll;

import javax.annotation.Nonnull;

/**
 * A generator of dice rolls.
 */
public interface Dice<R extends Roll> {

    @Nonnull R roll();
}
