package net.royalur.rules;

import javax.annotation.Nonnull;

/**
 * <p>
 *  The standard dice used for the Royal Game of Ur that
 *  consist of four tetrahedral dice with two marked and
 *  two unmarked corners. The number of marked faces that
 *  land up are counted as the value of the dice.
 * </p>
 * <p>
 *  This implementation rolls each dice into one of six
 *  orientations that each die could take if they were
 *  always rotated such that they had a point facing
 *  upwards. This follows the current visualisation of
 *  the dice on <a href="https://royalur.net">RoyalUr.net</a>.
 * </p>
 */
public class TetrahedralDice implements Dice<TetrahedralDiceRoll> {

    /**
     * The orientation of each dice represents how it landed.
     */
    public enum Orientation {
        /**
         * 
         */
        UP_TOP(true, 1),
        UP_BOTTOM_LEFT(true, 2),
        UP_BOTTOM_RIGHT(true, 3),

        DOWN_TOP(false, 4),
        DOWN_BOTTOM_LEFT(false, 5),
        DOWN_BOTTOM_RIGHT(false, 6);

        public final boolean isUp;
        public final int id;

        Orientation(boolean isUp, int id) {
            this.isUp = isUp;
            this.id = id;
        }
    }

    @Override
    public @Nonnull TetrahedralDiceRoll roll() {
        return null;
    }
}
