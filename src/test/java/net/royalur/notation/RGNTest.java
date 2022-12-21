package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleGame;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RGNTest {

    public static class ProvidedRules implements Arguments {
        public final @Nonnull RuleSet<?, ?, ?> rules;

        public ProvidedRules(@Nonnull RuleSet<?, ?, ?> rules) {
            this.rules = rules;
        }

        @Override
        public @Nonnull String toString() {
            return rules.getDescriptor();
        }

        @Override
        public Object[] get() {
            return new Object[] {this};
        }
    }

    public static class RulesProvider implements ArgumentsProvider {
        public static @Nonnull List<ProvidedRules> get() {
            List<RuleSet<?, ?, ?>> rules = new ArrayList<>();
            rules.add(Game.builder().simpleRules().buildRules());
            rules.add(Game.builder().standard().buildRules());
            rules.add(Game.builder().standard().paths(new BellPathPair()).buildRules());
            rules.add(Game.builder().standard().paths(new MastersPathPair()).buildRules());
            rules.add(Game.builder().standard().paths(new MurrayPathPair()).buildRules());
            rules.add(Game.builder().standard().paths(new SkiriukPathPair()).buildRules());
            rules.add(Game.builder().aseb().buildRules());
            return rules.stream().map(ProvidedRules::new).collect(Collectors.toList());
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
            return name + " (" + game.rules.getDescriptor() + ")";
        }

        @Override
        public Object[] get() {
            return new Object[] {this};
        }
    }

    public static class GameProvider implements ArgumentsProvider {
        public static @Nonnull List<ProvidedGame> get() {
            List<ProvidedGame> games = new ArrayList<>();

            // Empty games.
            for (ProvidedRules rules : RulesProvider.get()) {
                games.add(new ProvidedGame("Empty", new Game<>(rules.rules)));
            }
            return games;
        }

        @Override
        public @Nonnull Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return get().stream();
        }
    }

    private void testMetadata(@Nonnull String encoded, @Nonnull RuleSet<?, ?, ?> rules) {
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

        assertTrue(lines.contains("[Rules " + RGN.escape(rules.getDescriptor()) + "]"));
        assertTrue(lines.contains("[Light \"Anonymous\"]"));
        assertTrue(lines.contains("[Dark \"Anonymous\"]"));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[Date \"") && line.endsWith("\"]")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[Time \"") && line.endsWith("\"]")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("[TimeZone \"") && line.endsWith("\"]")));

    }

    @ParameterizedTest
    @ArgumentsSource(GameProvider.class)
    public void testMetadata(ProvidedGame game) {
        RGN rgn = new RGN();
        String encoded = rgn.encodeGame(game.game);
        testMetadata(encoded, game.game.rules);
    }
}
