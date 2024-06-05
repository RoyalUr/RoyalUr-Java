package net.royalur.model.dice;

import javax.annotation.Nullable;
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
    FOUR_BINARY("four_binary", "Four Binary", 4) {
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
    THREE_BINARY_0EQ4("three_binary_0eq4", "Three Binary 0 Equals 4", 3) {
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
     * The number of dice.
     */
    private final int dieCount;

    /**
     * Instantiates a type of dice.
     * @param id A fixed numerical identifier to represent this dice.
     * @param name The name given to this dice.
     * @param dieCount The number of dice.
     */
    DiceType(String id, String name, int dieCount) {
        this.id = id;
        this.name = name;
        this.dieCount = dieCount;
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

    /**
     * Gets the number of dice.
     * @return The number of dice.
     */
    public int getDieCount() {
        return dieCount;
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

    /**
     * Get the dice type with an ID of {@param id}.
     * @param id The ID of the dice type.
     * @return The dice type with the given ID.
     */
    public static DiceType getByID(String id) {
        for (DiceType diceType : values()) {
            if (diceType.id.equals(id))
                return diceType;
        }
        throw new IllegalArgumentException("Unknown dice type " + id);
    }

    /**
     * Get the dice type with an ID of {@param id}, or else {@code null}.
     * @param id The ID of the dice type to look for.
     * @return The dice type with the given ID, or null.
     */
    public static @Nullable DiceType getByIDOrNull(@Nullable String id) {
        if (id == null)
            return null;

        for (DiceType diceType : values()) {
            if (diceType.id.equals(id))
                return diceType;
        }
        return null;
    }
}
