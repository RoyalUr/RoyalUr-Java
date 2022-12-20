package net.royalur.rules;

import net.royalur.model.*;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.rules.simple.ConcreteSimpleRuleSet;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RuleSetTest {

    public static class NamedRuleSet<P extends Piece, S extends PlayerState, R extends Roll> {
        public final @Nonnull String name;
        public final @Nonnull RuleSet<P, S, R> rules;

        public NamedRuleSet(@Nonnull String name, @Nonnull RuleSet<P, S, R> rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }

    public static class RuleSetProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new NamedRuleSet<>("Standard-Bell", new ConcreteSimpleRuleSet<>(
                            new StandardBoardShape(),
                            new BellPathPair(),
                            new StandardDice(),
                            7
                    ))),
                    Arguments.of(new NamedRuleSet<>("Standard-Aseb", new ConcreteSimpleRuleSet<>(
                            new AsebBoardShape(),
                            new AsebPathPair(),
                            new StandardDice(),
                            5
                    )))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateEmptyBoard(NamedRuleSet<P, S, R> nrs) {

        RuleSet<?, ?, ?> rules = nrs.rules;
        Board<?> emptyBoard = rules.generateEmptyBoard();
        assertNotNull(emptyBoard);
        assertEquals(emptyBoard.shape, rules.boardShape);

        for (Tile tile : rules.boardShape.getTilesByRow()) {
            assertNull(emptyBoard.get(tile));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateNewPlayerState(NamedRuleSet<P, S, R> nrs) {

        RuleSet<?, ?, ?> rules = nrs.rules;

        PlayerState light = rules.generateNewPlayerState(Player.LIGHT);
        assertNotNull(light);
        assertEquals(Player.LIGHT, light.player);
        assertEquals(PlayerState.ANONYMOUS_NAME, light.name);

        PlayerState dark = rules.generateNewPlayerState(Player.DARK);
        assertNotNull(dark);
        assertEquals(Player.DARK, dark.player);
        assertEquals(PlayerState.ANONYMOUS_NAME, dark.name);

        PlayerState namedLight = rules.generateNewPlayerState(Player.LIGHT, "Alice");
        assertNotNull(namedLight);
        assertEquals(Player.LIGHT, namedLight.player);
        assertEquals("Alice", namedLight.name);

        PlayerState namedDark = rules.generateNewPlayerState(Player.DARK, "Bob");
        assertNotNull(namedDark);
        assertEquals(Player.DARK, namedDark.player);
        assertEquals("Bob", namedDark.name);
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testRollDice(NamedRuleSet<P, S, R> nrs) {
        RuleSet<?, ?, ?> rules = nrs.rules;
        for (int test = 0; test < 10_000; ++test) {
            Roll roll = rules.rollDice();
            assertNotNull(roll);
            assertTrue(roll.value <= rules.dice.maxRoll);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    @SuppressWarnings("unchecked")
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testFindAvailableMoves(NamedRuleSet<P, S, R> nrs) {
        RuleSet<P, S, R> rules = nrs.rules;
        Board<P> board = rules.generateEmptyBoard();
        S light = rules.generateNewPlayerState(Player.LIGHT);
        S dark = rules.generateNewPlayerState(Player.DARK);

        int availableLight = 0;
        int availableDark = 0;
        List<Move<P>>[] lightMovesByRoll = (List<Move<P>>[]) new List[rules.dice.maxRoll + 1];
        List<Move<P>>[] darkMovesByRoll = (List<Move<P>>[]) new List[rules.dice.maxRoll + 1];
        for (int test = 0; test < 10_000; ++test) {
            R roll = rules.rollDice();

            if (roll.value == 0) {
                assertThrows(IllegalArgumentException.class, () -> rules.findAvailableMoves(board, light, roll));
                assertThrows(IllegalArgumentException.class, () -> rules.findAvailableMoves(board, dark, roll));
                continue;
            }

            List<Move<P>> lightMoves = rules.findAvailableMoves(board, light, roll);
            assertNotNull(lightMoves);

            List<Move<P>> darkMoves = rules.findAvailableMoves(board, dark, roll);
            assertNotNull(darkMoves);

            if (lightMoves.size() > 0) {
                availableLight += 1;
            }
            if (darkMoves.size() > 0) {
                availableDark += 1;
            }

            if (lightMovesByRoll[roll.value] != null) {
                assertEquals(lightMoves, lightMovesByRoll[roll.value]);
                assertEquals(darkMoves, darkMovesByRoll[roll.value]);
            } else {
                lightMovesByRoll[roll.value] = lightMoves;
                darkMovesByRoll[roll.value] = darkMoves;
            }
        }
        assertTrue(availableLight > 0);
        assertTrue(availableDark > 0);
    }
}
