package net.royalur.notation;

import net.royalur.Game;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class JsonNotationTest {

    @ParameterizedTest
    @ArgumentsSource(RGNTest.GameProvider.class)
    public void testRunsWithoutError(RGNTest.ProvidedGame providedGame) {
        Game game = providedGame.game;
        JsonNotation notation = new JsonNotation();
        String encoded = notation.encodeGame(game);
        System.out.println(encoded);
        System.out.println(notation.decodeGame(encoded));
    }
}
