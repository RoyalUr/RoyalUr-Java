package net.royalur.rules;

import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.rules.state.GameState;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RuleSetTest {

    public static class NamedRuleSet {
        public final String name;
        public final RuleSet rules;

        public NamedRuleSet(String name, RuleSet rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class RuleSetProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new NamedRuleSet(
                            "Standard-Finkel",
                            RuleSet.createSimple(GameSettings.FINKEL)
                    )),
                    Arguments.of(new NamedRuleSet(
                            "Standard-Aseb",
                            RuleSet.createSimple(GameSettings.ASEB)
                    ))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateEmptyBoard(NamedRuleSet nrs) {

        RuleSet rules = nrs.rules;
        GameState initialState = rules.generateInitialGameState();
        Board board = initialState.getBoard();
        assertNotNull(board);
        assertEquals(board.getShape(), rules.getBoardShape());

        for (Tile tile : rules.getBoardShape().getTilesByRow()) {
            assertNull(board.get(tile));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public <P extends Piece, S extends PlayerState, R extends Roll>
    void testGenerateNewPlayerState(NamedRuleSet nrs) {

        RuleSet rules = nrs.rules;
        GameState initialState = rules.generateInitialGameState();

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
    void testRollDice(NamedRuleSet nrs) {
        RuleSet rules = nrs.rules;
        Dice dice = rules.getDiceFactory().createDice();
        for (int test = 0; test < 10_000; ++test) {
            Roll roll = dice.roll();
            assertNotNull(roll);
            assertTrue(roll.value() <= dice.getMaxRollValue());
        }
    }

    private static <T> List<T> createNullFilledList(int length) {
        List<T> list = new ArrayList<>();
        for (int index = 0; index < length; ++index) {
            list.add(null);
        }
        return list;
    }

    @ParameterizedTest
    @ArgumentsSource(RuleSetProvider.class)
    public void testFindAvailableMoves(NamedRuleSet nrs) {
        RuleSet rules = nrs.rules;
        Dice dice = rules.getDiceFactory().createDice();

        GameState initialState = rules.generateInitialGameState();
        Board board = initialState.getBoard();
        PlayerState light = initialState.getLightPlayer();
        PlayerState dark = initialState.getDarkPlayer();

        int availableLight = 0;
        int availableDark = 0;
        List<List<Move>> lightMovesByRoll = createNullFilledList(dice.getMaxRollValue() + 1);
        List<List<Move>> darkMovesByRoll = createNullFilledList(dice.getMaxRollValue() + 1);
        for (int test = 0; test < 10_000; ++test) {
            Roll roll = dice.roll();

            if (roll.value() == 0) {
                assertEquals(Collections.emptyList(), rules.findAvailableMoves(board, light, roll));
                assertEquals(Collections.emptyList(), rules.findAvailableMoves(board, dark, roll));
                continue;
            }

            List<Move> lightMoves = rules.findAvailableMoves(board, light, roll);
            assertNotNull(lightMoves);

            List<Move> darkMoves = rules.findAvailableMoves(board, dark, roll);
            assertNotNull(darkMoves);

            if (!lightMoves.isEmpty()) {
                availableLight += 1;
            }
            if (!darkMoves.isEmpty()) {
                availableDark += 1;
            }

            if (lightMovesByRoll.get(roll.value()) != null) {
                assertEquals(lightMoves, lightMovesByRoll.get(roll.value()));
                assertEquals(darkMoves, darkMovesByRoll.get(roll.value()));
            } else {
                lightMovesByRoll.set(roll.value(), lightMoves);
                darkMovesByRoll.set(roll.value(), darkMoves);
            }
        }
        assertTrue(availableLight > 0);
        assertTrue(availableDark > 0);
    }
}
