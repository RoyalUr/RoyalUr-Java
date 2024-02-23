package net.royalur.notation;

import net.royalur.Game;
import net.royalur.TestUtils;
import net.royalur.agent.Agent;
import net.royalur.agent.RandomAgent;
import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathType;
import net.royalur.rules.RuleSet;
import net.royalur.util.Cast;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RGNTest {

    public static class ProvidedRules implements Arguments {
        public final String name;
        public final RuleSet<Piece, PlayerState, Roll> rules;

        public ProvidedRules(
                String name,
                RuleSet<Piece, PlayerState, Roll> rules
        ) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public Object[] get() {
            return new Object[] {this};
        }
    }

    public static class RulesProvider implements ArgumentsProvider {
        public static List<ProvidedRules> get() {
            List<ProvidedRules> rules = new ArrayList<>();

            DiceFactory<Roll> dice = TestUtils.createDeterministicDice(
                    DiceType.FOUR_BINARY
            );

            rules.add(new ProvidedRules(
                    "Bell",
                    Game.builder().finkel().dice(dice).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Masters",
                    Game.builder().masters().dice(dice).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Murray",
                    Game.builder().finkel().paths(PathType.MURRAY)
                            .dice(dice).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Skiriuk",
                    Game.builder().finkel().paths(PathType.SKIRIUK)
                            .dice(dice).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Aseb",
                    Game.builder().aseb().dice(dice).buildRules()
            ));
            return rules;
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return get().stream();
        }
    }

    public static class ProvidedGame implements Arguments {
        public final String name;
        public final Game<Piece, PlayerState, Roll> game;

        public ProvidedGame(
                String name,
                Game<Piece, PlayerState, Roll> game
        ) {
            this.name = name;
            this.game = game;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public Object[] get() {
            return new Object[] {this};
        }
    }

    public static class GameProvider implements ArgumentsProvider {

        /**
         * Light always rolls 1, dark always rolls 0.
         */
        private static void playRiggedGame(
                String name,
                Game<Piece, PlayerState, Roll> game
        ) {

            while (!game.isFinished()) {
                PlayerType player = game.getTurnPlayer().getPlayer();
                switch (player) {
                    case LIGHT -> {
                        if (game.isWaitingForRoll()) {
                            game.rollDice(1);

                            try {
                                // Try to detect a deadlock where pieces can block themselves on a path.
                                // This can happen on paths that loop back on themselves.
                                assert !game.findAvailableMoves().isEmpty();
                            } catch (Exception e) {
                                System.err.println("Failed due to deadlock in the following state:");
                                System.err.println("* Name: " + name);
                                System.err.println("* Board:\n" + game.getBoard());
                                throw e;
                            }
                        } else {
                            List<Move<Piece>> moves = game.findAvailableMoves();
                            // Moving the last piece helps to avoid deadlocks, although
                            // this does assume that the available moves list is ordered.
                            Move<Piece> move = moves.get(moves.size() - 1);
                            game.makeMove(move);
                        }
                    }
                    case DARK -> game.rollDice(0);
                    default -> throw new IllegalStateException("Unexpected player " + player);
                }
            }
        }

        public static List<ProvidedGame> get() {
            List<ProvidedGame> games = new ArrayList<>();

            // Empty games.
            for (ProvidedRules rules : RulesProvider.get()) {
                games.add(new ProvidedGame(
                        "Empty, " + rules.name,
                        new Game<>(rules.rules)
                ));
            }

            // One roll by light.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                game.rollDice(1);
                games.add(new ProvidedGame("One Roll, " + rules.name, game));
            }

            // One move by light.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("One Move, " + rules.name, game));
            }

            // One move by light, and one roll.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("One Move One Roll, " + rules.name, game));
            }

            // One move by light, and one move by dark.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("Two Moves, " + rules.name, game));
            }

            // Game where light always rolls 1, and dark always rolls 0.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                playRiggedGame(rules.name, game);
                games.add(new ProvidedGame("Rigged, " + rules.name, game));
            }

            // Game with random moves.
            Random random = new Random(53);
            Agent<Piece, PlayerState, Roll> randomAgent = new RandomAgent<>(random);
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<Piece, PlayerState, Roll> game = new Game<>(rules.rules);
                Agent.playAutonomously(
                        Cast.unsafeCast(game),
                        Cast.unsafeCast(randomAgent),
                        Cast.unsafeCast(randomAgent)
                );
                games.add(new ProvidedGame("Random, " + rules.name, game));
            }
            return games;
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return get().stream();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(GameProvider.class)
    public void testMetadata(ProvidedGame providedGame) {
        Game<Piece, PlayerState, Roll> game = providedGame.game;
        RGN rgn = new RGN();
        String encoded = rgn.encodeGame(game);
        System.out.println(encoded);

        // Get a list of all the metadata lines, ignoring lines containing moves.
        String[] allLines = encoded.split("\\R");
        List<String> lines = new ArrayList<>();
        for (String line : allLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty())
                break;

            assertTrue(line.startsWith("["), line);
            assertTrue(line.endsWith("]"), line);
            lines.add(line);
        }

//        assertTrue(lines.contains("[Rules " + RGN.escape(game.rules.getDescriptor()) + "]"));
//        assertTrue(lines.contains("[Light " + RGN.escape(game.getLightPlayer().name) + "]"));
//        assertTrue(lines.contains("[Dark " + RGN.escape(game.getDarkPlayer().name) + "]"));
        assertTrue(lines.stream().anyMatch(
                line -> line.startsWith("[StartTime \"") && line.endsWith("\"]")
        ));
    }
}
