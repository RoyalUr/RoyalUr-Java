package net.royalur;

import net.royalur.model.Move;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.rules.standard.StandardPiece;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class ReadmeExamplesTest {

    @Test
    public void testReadmeExample() {
        // Create a new game using the Finkel rules.
        Game<StandardPiece, PlayerState, Roll> game = Game.createRoyalUrNet();

        // Play through a game making random moves.
        Random rand = new Random(42);

        while (!game.isFinished()) {
            String turnPlayerName = game.getTurnPlayer().getName();

            if (game.isWaitingForRoll()) {
                // Roll the dice!
                Roll roll = game.rollDice();
                System.out.println(turnPlayerName + ": Roll " + roll.getValue());
            } else {
                // Make a random move.
                List<Move<StandardPiece>> moves = game.findAvailableMoves();
                Move<StandardPiece> randomMove = moves.get(rand.nextInt(moves.size()));
                game.makeMove(randomMove);
                System.out.println(turnPlayerName + ": " + randomMove.describe());
            }
        }

        // Report the winner!
        System.out.println("\n" + game.getWinningPlayer().getName() + " won the game!");
    }
}
