package net.royalur.model.dice;

import net.royalur.name.Name;
import net.royalur.name.NameMap;
import net.royalur.name.UniqueNameMap;

import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/**
 * The type of dice to be used in a game.
 */
public enum DiceType implements Name, DiceFactory {

    /**
     * Represents rolling four binary die and counting the number
     * of ones that were rolled.
     */
    FOUR_BINARY(1, "FourBinary") {
        @Override
        public Dice createDice(RandomGenerator random) {
            return new BinaryDice(this, random, 4);
        }
    },

    /**
     * Represents rolling three binary die and counting the number
     * of ones that were rolled. If no ones are rolled, then a value
     * of four is given.
     */
    THREE_BINARY_0MAX(2, "ThreeBinary0Max") {
        @Override
        public Dice createDice(RandomGenerator random) {
            return new BinaryDice0AsMax(this, random, 3);
        }
    },
    ;

    /**
     * A store to be used to parse dice.
     */
    public static final NameMap<DiceType, DiceFactory> FACTORIES;

    static {
        NameMap<DiceType, DiceFactory> factories = new UniqueNameMap<>();
        for (DiceType type : values()) {
            factories.put(type, type);
        }
        FACTORIES = factories.unmodifiableCopy();
    }

    /**
     * A constant numerical ID representing the dice.
     * This ID will never change.
     */
    private final int id;

    /**
     * The name given to this dice.
     */
    private final String name;

    /**
     * Instantiates a type of dice.
     * @param id   A fixed numerical identifier to represent this dice.
     * @param name The name given to this dice.
     */
    DiceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Name getName() {
        return this;
    }

    @Override
    public String getTextName() {
        return name;
    }

    @Override
    public boolean hasID() {
        return true;
    }

    @Override
    public int getID() {
        return id;
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
            public Dice createDice() {
                return DiceType.this.createDice(randomProvider.get());
            }

            @Override
            public Name getName() {
                return DiceType.this;
            }
        };
    }
}
