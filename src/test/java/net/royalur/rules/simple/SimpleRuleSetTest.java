package net.royalur.rules.simple;

import net.royalur.Game;
import net.royalur.TestUtils;
import net.royalur.model.*;
import net.royalur.model.dice.*;
import net.royalur.model.path.PathPair;
import net.royalur.model.path.PathType;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;
import net.royalur.rules.state.GameState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleRuleSetTest {

    public static class NamedGameSettings {
        public final String name;
        public final GameSettings settings;

        public NamedGameSettings(String name, GameSettings settings) {
            this.name = name;
            this.settings = settings;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class StandardGameSettingsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {

            DiceFactory dice = TestUtils.createDeterministicDice(
                    DiceType.FOUR_BINARY
            );
            return Stream.of(
                    Arguments.of(new NamedGameSettings(
                            "Finkel",
                            GameSettings.FINKEL.withDice(dice)
                    )),
                    Arguments.of(new NamedGameSettings(
                            "Masters",
                            GameSettings.MASTERS.withDice(dice)
                    )),
                    Arguments.of(new NamedGameSettings(
                            "Skiriuk",
                            GameSettings.FINKEL.withPaths(PathType.SKIRIUK)
                                    .withDice(dice)
                    )),
                    Arguments.of(new NamedGameSettings(
                            "Murray",
                            GameSettings.FINKEL.withPaths(PathType.MURRAY)
                                    .withDice(dice)
                    )),
                    Arguments.of(new NamedGameSettings(
                            "Aseb",
                            GameSettings.ASEB.withDice(dice)
                    ))
            );
        }
    }

    @Test
    public void testIncompatibleBoardShapeAndPaths() {
        // We should be able to create invalid settings.
        GameSettings invalidAseb = GameSettings.ASEB.withPaths(PathType.BELL);
        GameSettings invalidFinkel = GameSettings.FINKEL.withPaths(PathType.ASEB);

        // They should just fail when creating a rule set.
        assertThrows(IllegalArgumentException.class, () -> RuleSet.createSimple(invalidAseb));
        assertThrows(IllegalArgumentException.class, () -> RuleSet.createSimple(invalidFinkel));
    }

    @ParameterizedTest
    @ArgumentsSource(StandardGameSettingsProvider.class)
    public void testFindAvailableMoves_IntroducingPieces(NamedGameSettings nrs) {
        GameSettings settings = nrs.settings;
        SimpleRuleSet rules = RuleSet.createSimple(settings);
        GameState initialState = rules.generateInitialGameState();
        Board board = initialState.getBoard();
        PlayerState light = initialState.getLightPlayer();
        PlayerState dark = initialState.getDarkPlayer();

        Dice sampleDice = rules.getDiceFactory().createDice();
        int maxRoll = sampleDice.getMaxRollValue();
        PathPair paths = rules.getPaths();
        for (int roll = 1; roll < maxRoll; ++roll) {
            for (PlayerState playerState : new PlayerState[] {light, dark}) {
                PlayerType player = playerState.getPlayer();
                assertEquals(
                        List.of(new Move(
                                player,
                                null, null,
                                paths.get(player).get(roll - 1),
                                new Piece(player, roll - 1),
                                null
                        )),
                        rules.findAvailableMoves(board, playerState, SimpleRoll.of(roll))
                );
            }
        }
    }

    private static void assertGamesMatch(Game game, FastSimpleGame fastGame) {
        assertEquals(game.isFinished(), fastGame.isFinished);
        if (game.isFinished()) {
            assertEquals(game.getWinner() == PlayerType.LIGHT, fastGame.isLightTurn);
            return;
        }

        assertEquals(game.isWaitingForRoll(), fastGame.isWaitingForRoll());
        assertEquals(game.isWaitingForMove(), fastGame.isWaitingForMove());

        Board board = game.getBoard();
        int[] fastBoard = fastGame.board.pieces;
        for (Tile tile : board.getShape().getTiles()) {
            Piece piece = board.get(tile);
            int fastIndex = fastGame.board.calcTileIndex(tile.getXIndex(), tile.getYIndex());
            int fastPiece = fastBoard[fastIndex];
            if (piece == null) {
                assertEquals(0, fastPiece);
            } else if (piece.getOwner() == PlayerType.LIGHT) {
                assertEquals(piece.getPathIndex() + 1, fastPiece);
            } else {
                assertEquals(-(piece.getPathIndex() + 1), fastPiece);
            }
        }

        PlayerState light = game.getLightPlayer();
        PlayerState dark = game.getDarkPlayer();
        assertEquals(light.getPieceCount(), fastGame.light.pieces);
        assertEquals(dark.getPieceCount(), fastGame.dark.pieces);
        assertEquals(light.getScore(), fastGame.light.score);
        assertEquals(dark.getScore(), fastGame.dark.score);
    }

    /**
     * Tests whether the FastGame rules match the StandardRuleSet rules.
     */
    @ParameterizedTest
    @ArgumentsSource(StandardGameSettingsProvider.class)
    public void testFastGameIsCompliant(NamedGameSettings nrs) {
        GameSettings settings = nrs.settings;
        SimpleRuleSet rules = RuleSet.createSimple(settings);

        int tests = 100;
        Random moveChoiceRandom = new Random(43);
        FastSimpleGame fastGame = rules.createCompatibleFastGame();
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        Dice dice = rules.getDiceFactory().createDice();

        for (int test = 0; test < tests; ++test) {
            Game game = Game.create(settings);
            fastGame.copyFrom(game);

            while (true) {
                assertGamesMatch(game, fastGame);
                if (game.isFinished())
                    break;

                if (game.isWaitingForRoll()) {
                    Roll roll = dice.roll();
                    game.rollDice(roll);
                    fastGame.applyRoll(roll.value(), moveList);

                } else if (game.isWaitingForMove()) {
                    List<Move> moves = game.findAvailableMoves();
                    fastGame.findAvailableMoves(moveList);
                    assertEquals(moves.size(), moveList.moveCount);
                    for (int moveIndex = 0; moveIndex < moves.size(); ++moveIndex) {
                        Move move = moves.get(moveIndex);
                        int movePathIndex = moveList.moves[moveIndex];
                        if (move.isIntroducingPiece()) {
                            assertEquals(-1, movePathIndex);
                        } else {
                            assertEquals(move.getSourcePiece().getPathIndex(), movePathIndex);
                        }
                    }

                    int moveIndex = moveChoiceRandom.nextInt(moves.size());
                    game.makeMove(moves.get(moveIndex));
                    fastGame.applyMove(moveList.moves[moveIndex]);

                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
