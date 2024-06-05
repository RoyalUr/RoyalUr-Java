package net.royalur.model.dice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/**
 * The type of dice to be used in a game.
 */
public enum DiceType implements DiceFactory {

    /**
     * Represents rolling four binary die and counting the number
     * of ones that were rolled.
     */
    FOUR_BINARY("four_binary", "Four Binary") {
        @Override
        public Dice createDice(RandomGenerator random) {
            return new BinaryDice(getID(), random, 4);
        }
    },

    /**
     * Represents rolling three binary die and counting the number
     * of ones that were rolled. If no ones are rolled, then a value
     * of four is given.
     */
    THREE_BINARY_0EQ4("three_binary_0eq4", "Three Binary 0 Equals 4") {
        @Override
        public Dice createDice(RandomGenerator random) {
            return new BinaryDice0AsMax(getID(), random, 3);
        }
    },
    ;

    /**
     * A store to be used to parse dice.
     */
    public static final Map<String, DiceFactory> BY_ID;

    static {
        Map<String, DiceFactory> byID = new HashMap<>();
        for (DiceType type : values()) {
            byID.put(type.id, type);
        }
        BY_ID = Collections.unmodifiableMap(byID);
    }

    /**
     * An ID representing this type of die.
     */
    private final String id;

    /**
     * The name given to this dice.
     */
    private final String name;

    /**
     * Instantiates a type of dice.
     * @param id   A fixed numerical identifier to represent this dice.
     * @param name The name given to this dice.
     */
    DiceType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID that refers to this dice type.
     * @return The ID that refers to this dice type.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the name of this dice type.
     * @return The name of this dice type.
     */
    public String getName() {
        return name;
    }

    @Override
    public Dice createDice() {
        return createDice(new Random());
    }

    /**
     * Creates a set of these dice using {@code random} as its source of randomness.
     * @param random The source of randomness to use for the dice.
     * @return A new set of these dice.
     */
    public abstract Dice createDice(RandomGenerator random);

    /**
     * Creates a factory that produces dice using {@code randomProvider} to
     * generate the source of randomness for each dice that is produced.
     * @param randomProvider The provider of the source of randomness for each dice.
     * @return A factory for these dice.
     */
    public DiceFactory createFactory(Supplier<RandomGenerator> randomProvider) {
        return new DiceFactory() {
            @Override
            public String getID() {
                return DiceType.this.id;
            }

            @Override
            public Dice createDice() {
                return DiceType.this.createDice(randomProvider.get());
            }
        };
    }
}
