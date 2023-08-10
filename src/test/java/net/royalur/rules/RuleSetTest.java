package net.royalur.rules;

import net.royalur.model.*;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.rules.standard.StandardDice;
import net.royalur.rules.standard.StandardPieceProvider;
import net.royalur.rules.standard.StandardPlayerStateProvider;
import net.royalur.rules.standard.StandardRuleSet;
import net.royalur.rules.state.GameState;
import net.royalur.util.Cast;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.Collections;
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
                    Arguments.of(new NamedRuleSet<>("Standard-Bell", new StandardRuleSet<>(
                            new StandardBoardShape(),
                            new BellPathPair(),
                            new StandardDice(),
                            false,
                            new StandardPieceProvider(),
                            new StandardPlayerStateProvider(7)
                    ))),
                    Arguments.of(new NamedRuleSet<>("Standard-Aseb", new StandardRuleSet<>(
                            new AsebBoardShape(),
                            new AsebPathPair(),
                            new StandardDice(),
                            true,
                            new StandardPieceProvider(),
                            new StandardPlayerStateProvider(5)
                    )))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateEmptyBoard(NamedRuleSet<P, S, R> nrs) {

        RuleSet<?, ?, ?> rules = nrs.rules;
        GameState<?, ?, ?> initialState = rules.generateInitialGameState();
        Board<?> board = initialState.getBoard();
        assertNotNull(board);
        assertEquals(board.getShape(), rules.getBoardShape());

        for (Tile tile : rules.getBoardShape().getTilesByRow()) {
            assertNull(board.get(tile));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateNewPlayerState(NamedRuleSet<P, S, R> nrs) {

        RuleSet<?, ?, ?> rules = nrs.rules;
        GameState<?, ?, ?> initialState = rules.generateInitialGameState();

        PlayerState light = initialState.getLightPlayer();
        assertNotNull(light);
        assertEquals(PlayerType.LIGHT, light.getPlayer());

        PlayerState dark = initialState.getDarkPlayer();
        assertNotNull(dark);
        assertEquals(PlayerType.DARK, dark.getPlayer());
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testRollDice(NamedRuleSet<P, S, R> nrs) {
        RuleSet<?, ?, ?> rules = nrs.rules;
        Dice<?> dice = rules.getDice();
        for (int test = 0; test < 10_000; ++test) {
            Roll roll = dice.roll();
            assertNotNull(roll);
            assertTrue(roll.getValue() <= dice.getMaxRollValue());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testFindAvailableMoves(NamedRuleSet<P, S, R> nrs) {
        RuleSet<P, S, R> rules = nrs.rules;
        Dice<R> dice = rules.getDice();

        GameState<P, S, R> initialState = rules.generateInitialGameState();
        Board<P> board = initialState.getBoard();
        S light = initialState.getLightPlayer();
        S dark = initialState.getDarkPlayer();

        int availableLight = 0;
        int availableDark = 0;
        List<Move<P>>[] lightMovesByRoll = Cast.unsafeCast(new List[dice.getMaxRollValue() + 1]);
        List<Move<P>>[] darkMovesByRoll = Cast.unsafeCast(new List[dice.getMaxRollValue() + 1]);
        for (int test = 0; test < 10_000; ++test) {
            R roll = dice.roll();

            if (roll.getValue() == 0) {
                assertEquals(Collections.emptyList(), rules.findAvailableMoves(board, light, roll));
                assertEquals(Collections.emptyList(), rules.findAvailableMoves(board, dark, roll));
                continue;
            }

            List<Move<P>> lightMoves = rules.findAvailableMoves(board, light, roll);
            assertNotNull(lightMoves);

            List<Move<P>> darkMoves = rules.findAvailableMoves(board, dark, roll);
            assertNotNull(darkMoves);

            if (!lightMoves.isEmpty()) {
                availableLight += 1;
            }
            if (!darkMoves.isEmpty()) {
                availableDark += 1;
            }

            if (lightMovesByRoll[roll.getValue()] != null) {
                assertEquals(lightMoves, lightMovesByRoll[roll.getValue()]);
                assertEquals(darkMoves, darkMovesByRoll[roll.getValue()]);
            } else {
                lightMovesByRoll[roll.getValue()] = lightMoves;
                darkMovesByRoll[roll.getValue()] = darkMoves;
            }
        }
        assertTrue(availableLight > 0);
        assertTrue(availableDark > 0);
    }
}
