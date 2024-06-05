package net.royalur;

import net.royalur.model.Move;
import net.royalur.model.dice.Roll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class ReadmeExamplesTest {

    @Test
    public void testReadmeExample() {
        // Create a new game using the Finkel rules.
        Game game = Game.createFinkel();

        // Play through a game making random moves.
        Random rand = new Random();

        while (!game.isFinished()) {
            String turnPlayerName = game.getTurn().getName();

            if (game.isWaitingForRoll()) {
                // Roll the dice!
                Roll roll = game.rollDice();
                System.out.println(turnPlayerName + ": Roll " + roll.value());
            } else {
                // Make a random move.
                List<Move> moves = game.findAvailableMoves();
                Move randomMove = moves.get(rand.nextInt(moves.size()));
                game.move(randomMove);
                System.out.println(turnPlayerName + ": " + randomMove.describe());
            }
        }

        // Report the winner!
        System.out.println("\n" + game.getWinner().getName() + " won the game!");
    }
}
