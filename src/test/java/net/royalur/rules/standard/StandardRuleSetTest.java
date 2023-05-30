package net.royalur.rules.standard;

import net.royalur.model.*;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.rules.Dice;
import net.royalur.rules.state.GameState;
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

public class StandardRuleSetTest {

    public static class NamedSimpleRuleSet {
        public final @Nonnull String name;
        public final @Nonnull StandardRuleSet<StandardPiece, PlayerState, Roll> rules;

        public NamedSimpleRuleSet(@Nonnull String name, @Nonnull StandardRuleSet<StandardPiece, PlayerState, Roll> rules) {
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
                    Arguments.of(new NamedSimpleRuleSet("Standard-Bell", new StandardRuleSet<>(
                            new StandardBoardShape(),
                            new BellPathPair(),
                            new StandardDice(),
                            new StandardPieceProvider(),
                            new StandardPlayerStateProvider(7)
                    ))),
                    Arguments.of(new NamedSimpleRuleSet("Standard-Aseb", new StandardRuleSet<>(
                            new AsebBoardShape(),
                            new AsebPathPair(),
                            new StandardDice(),
                            new StandardPieceProvider(),
                            new StandardPlayerStateProvider(5)
                    )))
            );
        }
    }

    @Test
    public void testIncompatibleBoardShapeAndPaths() {
        Dice<Roll> dice = new StandardDice();
        StandardPieceProvider pieceProvider = new StandardPieceProvider();
        StandardPlayerStateProvider playerStateProvider = new StandardPlayerStateProvider(7);

        assertThrows(IllegalArgumentException.class, () -> new StandardRuleSet<>(
                new AsebBoardShape(), new BellPathPair(), dice, pieceProvider, playerStateProvider
        ));
        assertThrows(IllegalArgumentException.class, () -> new StandardRuleSet<>(
                new StandardBoardShape(), new AsebPathPair(), dice, pieceProvider, playerStateProvider
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleRuleSetProvider.class)
    public void testFindAvailableMoves_IntroducingPieces(NamedSimpleRuleSet nrs) {
        StandardRuleSet<StandardPiece, PlayerState, Roll> rules = nrs.rules;
        GameState<StandardPiece, PlayerState, Roll> initialState = rules.generateInitialGameState();
        Board<StandardPiece> board = initialState.board;
        PlayerState light = initialState.lightPlayer;
        PlayerState dark = initialState.darkPlayer;

        int maxRoll = rules.getDice().getMaxRollValue();
        PathPair paths = rules.getPaths();
        for (int roll = 1; roll < maxRoll; ++roll) {
            for (PlayerState playerState : new PlayerState[] {light, dark}) {
                Player player = playerState.player;
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
