package net.royalur.rules.simple.fast;

import net.royalur.model.PlayerType;
import net.royalur.model.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A list of moves that may be reused without allocating memory.
 */
public class FastSimpleMoveList {

    /**
     * Path indices of tiles to move. -1 represents introducing a piece.
     * Only {@link #moveCount} moves are populated in this array.
     */
    public int[] moves;

    public int moveCount;

    public FastSimpleMoveList(int initialCapacity) {
        this.moves = new int[initialCapacity];

    }

    public FastSimpleMoveList() {
        this(8);
    }

    public void clear() {
        this.moveCount = 0;
    }

    public void add(int pathIndex) {
        if (moveCount >= moves.length) {
            this.moves = Arrays.copyOf(moves, 2 * moves.length);
        }
        moves[moveCount] = pathIndex;
        moveCount += 1;
    }

    public static String moveToString(FastSimpleGame game, int pathIndex) {
        if (pathIndex < -1)
            return "No move";

        PlayerType turn = game.isLightTurn ? PlayerType.LIGHT : PlayerType.DARK;
        List<Tile> path = game.settings.getPaths().getWithStartEnd(turn);
        Tile from = path.get(pathIndex + 1);
        Tile to = path.get(pathIndex + 1 + game.rollValue);
        return from + " -> " + to;
    }

    public String toString(FastSimpleGame game) {
        List<String> movesAsStrings = new ArrayList<>(moveCount);
        for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
            movesAsStrings.add(moveToString(game, moves[moveIndex]));
        }
        return movesAsStrings.toString();
    }
}
