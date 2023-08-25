# RoyalUr-Java

This project provides a Java API for the play and analysis of games
of the Royal Game of Ur. This API is designed to support many rule
sets of the Royal Game of Ur. More documentation will be added
here as this API is developed.

* [GitHub](https://github.com/RoyalUr/RoyalUr-Java)
* [JavaDocs](apidocs)
## 🚀 Example

The following is a small example that shows the basics of creating
a game, autonomously playing through it by making random moves,
and reporting what happens in the game as it progresses.

```java
// Create a new game using the Finkel rules.
Game<Piece, PlayerState, Roll> game = Game.createFinkel();

// Play through a game making random moves.
Random rand = new Random();

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
```

Here is a snippet from the end of the output from running
the example above:
```
Dark: Score a piece from C7
Light: Roll 2
Light: Score a piece from A8
Dark: Roll 1
Dark: Move C4 to C3
Light: Roll 1
Light: Score a piece from A7

Light won the game!
```

# 🔧 Installation
Currently, the RoyalUr-Java library is only available through
building the source code using Maven. We plan to release the
library into the central library as well, but we have had
issues with that process that we still need to work out.
You can manually install from the source code by running
`maven install` in the root directory of the source code.

# 📜 Supported Rule Sets

This library supports a wide range of rule sets for the
Royal Game of Ur, although we are looking to add more.

**Rule Sets:**
- Rules used on RoyalUr.net.
- Rules proposed by Irving Finkel (simple version).
- Rules proposed by James Masters.
- Rules for Aseb (using different game board).

## Custom Rule Sets

The rule sets are created by selecting several components
that make up the rules. This includes selecting the board
shape, the path that pieces take around the board, the
dice that are used, and other features of the rules. The
provided components are given below, but new components
can also be created and used instead.

**Board Shapes:**
- Royal Game of Ur.
- Aseb.

<p align="center">
  <img alt="Supported Board Shapes" height="350" src="BoardShapes.png" />
</p>

**Paths:**
- Bell's path.
- Masters' path.
- Murray's path.
- Skiriuk's path.
- Aseb path proposed by Murray.

<p align="center">
  <img alt="Supported Paths" height="350" src="Paths.png" />
</p>

**Dice:**
- Four binary die.
- Three binary die where a roll of zero is treated as a four.
- N binary die.
- N binary die where a roll of zero is treated as a roll of N+1.

**Features:**
- Number of starting pieces for each player.
- Whether rosettes are safe tiles.


# 📝 License
This library is licensed under the MIT license. Read the
license [here](LICENSE).
