package net.royalur.rules;

import net.royalur.model.Roll;

/**
 * A roll of four tetrahedral die that each have three
 * marked and three unmarked corners. The number of marked
 * corners that are up are counted as the value of the roll.
 */
public class TetrahedralDiceRoll extends Roll {

    public TetrahedralDiceRoll(int value) {
        super(value);
    }
}
