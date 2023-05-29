package net.royalur.notation;

import net.royalur.BaseGame;
import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.RuleSet;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RGNTest {

    public static class ProvidedRules implements Arguments {
        public final @Nonnull String name;
        public final @Nonnull RuleSet<?, ?, ?> rules;

        public ProvidedRules(@Nonnull String name, @Nonnull RuleSet<?, ?, ?> rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }

        @Override
        public Object[] get() {
            return new Object[] {this};
        }
    }

    public static class RulesProvider implements ArgumentsProvider {
        public static @Nonnull List<ProvidedRules> get() {
            List<ProvidedRules> rules = new ArrayList<>();
            rules.add(new ProvidedRules(
                    "Standard", Game.builder().standard().buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Bell", Game.builder().standard().paths(new BellPathPair()).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Masters", Game.builder().standard().paths(new MastersPathPair()).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Murray", Game.builder().standard().paths(new MurrayPathPair()).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Skiriuk", Game.builder().standard().paths(new SkiriukPathPair()).buildRules()
            ));
            rules.add(new ProvidedRules(
                    "Aseb", Game.builder().aseb().buildRules()
            ));
            return rules;
        }

        @Override
        public @Nonnull Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return get().stream();
        }
    }

    public static class ProvidedGame implements Arguments {
        public final @Nonnull String name;
        public final @Nonnull Game<?, ?, ?> game;

        public ProvidedGame(@Nonnull String name, @Nonnull Game<?, ?, ?> game) {
            this.name = name;
            this.game = game;
        }

        @Override
        public @Nonnull String toString() {
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
        private static <P extends Piece, S extends PlayerState, R extends Roll> void
        playRiggedGame(@Nonnull String name, @Nonnull Game<P, S, R> game) {

            while (!game.isFinished()) {
                Player player = game.getTurnPlayer().player;
                switch (player) {
                    case LIGHT -> {
                        if (game.isWaitingForRoll()) {
                            game.rollDice(1);

                            try {
                                // Try to detect a deadlock where pieces can block themselves on a path.
                                // This can happen on paths that loop back on themselves.
                                assert game.findAvailableMoves().size() > 0;
                            } catch (Exception e) {
                                System.err.println("Failed due to deadlock in the following state:");
                                System.err.println("* Name: " + name);
                                System.err.println("* Board:\n" + game.getBoard());
                                throw e;
                            }
                        } else {
                            List<Move<P>> moves = game.findAvailableMoves();
                            // Moving the last piece helps to avoid deadlocks, although
                            // this does assume that the available moves list is ordered.
                            Move<P> move = moves.get(moves.size() - 1);
                            game.makeMove(move);
                        }
                    }
                    case DARK -> game.rollDice(0);
                    default -> throw new IllegalStateException("Unexpected player " + player);
                }
            }
        }

        public static @Nonnull List<ProvidedGame> get() {
            List<ProvidedGame> games = new ArrayList<>();

            // Empty games.
            for (ProvidedRules rules : RulesProvider.get()) {
                games.add(new ProvidedGame("Empty", new BaseGame<>(rules.rules)));
            }

            // Empty games with player names.
            for (ProvidedRules rules : RulesProvider.get()) {
                games.add(new ProvidedGame("Empty", new BaseGame<>(rules.rules)));
            }

            // One roll by light.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<?, ?, ?> game = new BaseGame<>(rules.rules);
                game.rollDice(1);
                games.add(new ProvidedGame("One Roll", game));
            }

            // One move by light.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<?, ?, ?> game = new BaseGame<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("One Move", game));
            }

            // One move by light, and one roll.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<?, ?, ?> game = new BaseGame<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("One Move, One Roll", game));
            }

            // One move by light, and one move by dark.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<?, ?, ?> game = new BaseGame<>(rules.rules);
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                game.rollDice(1);
                game.makeMoveIntroducingPiece();
                games.add(new ProvidedGame("Two Moves", game));
            }

            // Game where light always rolls 1, and dark always rolls 0.
            for (ProvidedRules rules : RulesProvider.get()) {
                Game<?, ?, ?> game = new BaseGame<>(rules.rules);
                playRiggedGame(rules.name, game);
                games.add(new ProvidedGame("Rigged", game));
            }
            return games;
        }

        @Override
        public @Nonnull Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return get().stream();
        }
    }

    private void testMetadata(@Nonnull String encoded, @Nonnull Game<?, ?, ?> game) {
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
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[Date \"") && line.endsWith("\"]")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[Time \"") && line.endsWith("\"]")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[TimeZone \"") && line.endsWith("\"]")));
    }

    @ParameterizedTest
    @ArgumentsSource(GameProvider.class)
    public void testMetadata(ProvidedGame providedGame) {
        Game<?, ?, ?> game = providedGame.game;
        RGN rgn = new RGN();
        String encoded = rgn.encodeGame(game);
        testMetadata(encoded, game);
    }
}
