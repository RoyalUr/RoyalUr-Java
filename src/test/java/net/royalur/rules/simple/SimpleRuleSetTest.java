package net.royalur.rules.simple;

import net.royalur.model.*;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.rules.Dice;
import net.royalur.rules.dice.StandardDice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleRuleSetTest {

    public static class NamedSimpleRuleSet {
        public final @Nonnull String name;
        public final @Nonnull SimpleRuleSet<SimplePiece, PlayerState, Roll> rules;

        public NamedSimpleRuleSet(@Nonnull String name, @Nonnull SimpleRuleSet<SimplePiece, PlayerState, Roll> rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }

    public static class SimpleRuleSetProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new NamedSimpleRuleSet("Standard-Bell", new ConcreteSimpleRuleSet<>(
                            new StandardBoardShape(),
                            new BellPathPair(),
                            new StandardDice(),
                            7
                    ))),
                    Arguments.of(new NamedSimpleRuleSet("Standard-Aseb", new ConcreteSimpleRuleSet<>(
                            new AsebBoardShape(),
                            new AsebPathPair(),
                            new StandardDice(),
                            5
                    )))
            );
        }
    }

    @Test
    public void testNew() {
        Dice<Roll> dice = new StandardDice();
        int startingPieceCount = 7;

        assertThrows(IllegalArgumentException.class, () -> new ConcreteSimpleRuleSet<>(
                new AsebBoardShape(), new BellPathPair(), dice, startingPieceCount
        ));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteSimpleRuleSet<>(
                new StandardBoardShape(), new AsebPathPair(), dice, startingPieceCount
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleRuleSetProvider.class)
    public void testFindAvailableMoves_IntroducingPieces(NamedSimpleRuleSet nrs) {
        SimpleRuleSet<SimplePiece, PlayerState, Roll> rules = nrs.rules;
        Board<SimplePiece> board = rules.generateEmptyBoard();
        PlayerState light = rules.generateNewPlayerState(Player.LIGHT);
        PlayerState dark = rules.generateNewPlayerState(Player.DARK);

        for (int roll = 1; roll < rules.dice.maxRollValue; ++roll) {
            for (PlayerState playerState : new PlayerState[] {light, dark}) {
                Player player = playerState.player;
                assertEquals(
                        List.of(new Move<>(
                                player,
                                null, null,
                                rules.paths.get(player).get(roll - 1),
                                new SimplePiece(player, roll - 1),
                                null
                        )),
                        rules.findAvailableMoves(board, playerState, Roll.of(roll))
                );
            }
        }
    }
}
