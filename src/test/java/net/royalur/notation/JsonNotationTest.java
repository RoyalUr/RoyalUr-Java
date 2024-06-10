package net.royalur.notation;

import net.royalur.Game;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNotationTest {

    @ParameterizedTest
    @ArgumentsSource(RGNTest.GameProvider.class)
    public void testEqual(RGNTest.ProvidedGame providedGame) {
        Game game = providedGame.game;
        JsonNotation notation = new JsonNotation();
        String encoded = notation.encodeGame(game);
        Game decoded = notation.decodeGame(encoded);
        assertEquals(game.getStates(), decoded.getStates());
    }
}
