package net.royalur.rules.standard;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.path.PathType;
import net.royalur.model.dice.Dice;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.GameState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StandardRuleSetTest {

    public static class NamedSimpleRuleSet {
        public final String name;
        public final RuleSet<StandardPiece, PlayerState, Roll> rules;

        public NamedSimpleRuleSet(String name, RuleSet<StandardPiece, PlayerState, Roll> rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class SimpleRuleSetProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new NamedSimpleRuleSet(
                            "Standard-Finkel",
                            RuleSet.createStandard(GameSettings.FINKEL)
                    )),
                    Arguments.of(new NamedSimpleRuleSet(
                            "Standard-Aseb",
                            RuleSet.createStandard(GameSettings.ASEB)
                    ))
            );
        }
    }

    @Test
    public void testIncompatibleBoardShapeAndPaths() {
        // We should be able to create invalid settings.
        GameSettings<Roll> invalidAseb = GameSettings.ASEB.withPaths(PathType.BELL);
        GameSettings<Roll> invalidFinkel = GameSettings.FINKEL.withPaths(PathType.ASEB);

        // They should just fail when creating a rule set.
        assertThrows(IllegalArgumentException.class, () -> RuleSet.createStandard(invalidAseb));
        assertThrows(IllegalArgumentException.class, () -> RuleSet.createStandard(invalidFinkel));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleRuleSetProvider.class)
    public void testFindAvailableMoves_IntroducingPieces(NamedSimpleRuleSet nrs) {
        RuleSet<StandardPiece, PlayerState, Roll> rules = nrs.rules;
        GameState<StandardPiece, PlayerState, Roll> initialState = rules.generateInitialGameState();
        Board<StandardPiece> board = initialState.getBoard();
        PlayerState light = initialState.getLightPlayer();
        PlayerState dark = initialState.getDarkPlayer();

        Dice<Roll> sampleDice = rules.getDiceFactory().create();
        int maxRoll = sampleDice.getMaxRollValue();
        PathPair paths = rules.getPaths();
        for (int roll = 1; roll < maxRoll; ++roll) {
            for (PlayerState playerState : new PlayerState[] {light, dark}) {
                PlayerType player = playerState.getPlayer();
                assertEquals(
                        List.of(new Move<>(
                                player,
                                null, null,
                                paths.get(player).get(roll - 1),
                                new StandardPiece(player, roll - 1),
                                null
                        )),
                        rules.findAvailableMoves(board, playerState, Roll.of(roll))
                );
            }
        }
    }
}
