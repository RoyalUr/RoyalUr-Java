package net.royalur.rules.simple.fast;

import java.util.Arrays;

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
}
