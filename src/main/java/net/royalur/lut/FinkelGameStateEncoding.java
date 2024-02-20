package net.royalur.lut;

import net.royalur.rules.simple.fast.FastSimpleBoard;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinkelGameStateEncoding implements GameStateEncoding {

    private final int[] middleLaneCompression;

    public FinkelGameStateEncoding() {
        this.middleLaneCompression = generateMiddleLaneCompression();

        int maxCompressed = 0;
        for (int compressed : middleLaneCompression) {
            maxCompressed = Math.max(maxCompressed, compressed);
        }
        int bits = 1;
        while (maxCompressed > (1 << bits)) {
            bits += 1;
        }
        if (bits != 13)
            throw new IllegalStateException("Expected the middle lane to take 13 bits");
    }

    private static int[] generateMiddleLaneCompression() {
        int[] middleLaneCompression = new int[0xffff];
        Arrays.fill(middleLaneCompression, -1);

        List<Integer> states = new ArrayList<>();
        addMiddleLaneStates(states, 0, 7, 7, 0);
        for (int index = 0; index < states.size(); ++index) {
            int state = states.get(index);
            middleLaneCompression[state] = index;
        }
        return middleLaneCompression;
    }

    private static void addMiddleLaneStates(
            @Nonnull List<Integer> states, int state, int lightPieces, int darkPieces, int index
    ) {
        int nextIndex = index + 1;
        for (int occupant = 0; occupant < 3; ++occupant) {
            int newLightPieces = lightPieces;
            int newDarkPieces = darkPieces;
            if (occupant == 1) {
                newDarkPieces -= 1;
                if (newDarkPieces < 0)
                    continue;

            } else if (occupant == 2) {
                newLightPieces -= 1;
                if (newLightPieces < 0)
                    continue;
            }

            int newState = state | (occupant << (2 * index));
            if (nextIndex == 8) {
                states.add(newState);
            } else {
                addMiddleLaneStates(states, newState, newLightPieces, newDarkPieces, nextIndex);
            }
        }
    }

    private int encodeMiddleLane(@Nonnull FastSimpleBoard board) {
        int state = 0;
        for (int index = 0; index < 8; ++index) {
            int piece = board.pieces[board.calcTileIndex(1, index)];
            int occupant = (piece == 0 ? 0 : (piece < 0 ? 1 : 2));
            state |= occupant << (2 * index);
        }
        int compressed = middleLaneCompression[state];
        if (compressed == -1)
            throw new IllegalArgumentException("Illegal board state!");

        return compressed;
    }

    private int encodeSideLane(@Nonnull FastSimpleBoard board, int boardX) {
        int state = 0;
        int baseBitIndex = (boardX == 0 ? 0 : 3);
        for (int index = 0; index < 6; ++index) {
            int boardY = index;
            int bitIndex = baseBitIndex + index * 4;
            if (index >= 4) {
                boardY += 2;
                baseBitIndex += 4;
            }

            int boardIndex = board.calcTileIndex(boardX, boardY);
            int piece = board.pieces[board.calcTileIndex(boardX, boardY)];
            int occupant = (piece == 0 ? 0 : 1);
            state |= occupant << index;
        }
        return state;
    }

    private int encodeBoard(@Nonnull FastSimpleBoard board) {
        int leftLane = encodeSideLane(board, 0);
        int rightLane = encodeSideLane(board, 2);
        int middleLane = encodeMiddleLane(board);
        return rightLane | (middleLane << 6) | (leftLane << 19);
    }

    @Override
    public long encodeGameState(@Nonnull FastSimpleGame game) {
        int lightPieces = game.light.pieces;
        int darkPieces = game.dark.pieces;
        int board = encodeBoard(game.board);

        int state = 0;
        state |= board;
        state |= darkPieces << 25;
        state |= lightPieces << 28;
        return state;
    }
}
