package net.royalur;

import net.royalur.model.Move;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class ReadmeExamplesTest {

    @Test
    public void testReadmeExample() {
        // Create a new game using the Finkel rules.
        Game<Piece, PlayerState, Roll> game = Game.createFinkel();

        // Play through a game making random moves.
        Random rand = new Random(42);

        while (!game.isFinished()) {
            String turnPlayerName = game.getTurn().getTextName();

            if (game.isWaitingForRoll()) {
                // Roll the dice!
                Roll roll = game.rollDice();
                System.out.println(turnPlayerName + ": Roll " + roll.value());
            } else {
                // Make a random move.
                List<Move<Piece>> moves = game.findAvailableMoves();
                Move<Piece> randomMove = moves.get(rand.nextInt(moves.size()));
                game.makeMove(randomMove);
                System.out.println(turnPlayerName + ": " + randomMove.describe());
            }
        }

        // Report the winner!
        System.out.println("\n" + game.getWinner().getTextName() + " won the game!");
    }
}
