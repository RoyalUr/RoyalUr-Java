package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleBoard;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nonnull;

public class FinkelGameStateEncoding extends SimpleGameStateEncoding {

    public FinkelGameStateEncoding() {
        super(GameSettings.FINKEL);
    }

    private int encodeMiddleLane(@Nonnull FastSimpleBoard board) {
        int width = board.width;
        int[] pieces = board.pieces;

        int state = 0;
        for (int index = 0; index < 8; ++index) {
            int piece = pieces[1 + index * width];
            int occupant = (piece == 0 ? 0 : (piece < 0 ? 1 : 2));
            state = (state << 2) | occupant;
        }

        int compressed = warTileCompression[state];
        if (compressed == -1)
            throw new IllegalArgumentException("Illegal board state!");

        return compressed;
    }

    private int encodeSideLane(@Nonnull FastSimpleBoard board, int boardX) {
        int width = board.width;
        int[] pieces = board.pieces;

        int state = 0;
        for (int index = 0; index < 6; ++index) {
            int boardY = index;
            if (index >= 4) {
                boardY += 2;
            }

            int boardIndex = boardX + boardY * width;
            int piece = pieces[boardIndex];
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
        if (!game.isLightTurn) {
            throw new IllegalArgumentException(
                    "Only game states where it is the light player's turn are supported by this encoding"
            );
        }
        int state = 0;
        state |= encodeBoard(game.board);
        state |= game.dark.pieces << 25;
        state |= game.light.pieces << 28;
        return Integer.toUnsignedLong(state);
    }
}
