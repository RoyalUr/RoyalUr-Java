package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class JsonNotationTest {

    @ParameterizedTest
    @ArgumentsSource(RGNTest.GameProvider.class)
    public void testRunsWithoutError(RGNTest.ProvidedGame providedGame) {
        Game<Piece, PlayerState, Roll> game = providedGame.game;
        JsonNotation<Piece, PlayerState, Roll> notation = JsonNotation.createSimple();
        String encoded = notation.encodeGame(game);
        System.out.println(encoded);
        System.out.println(notation.decodeGame(encoded));
    }
}
