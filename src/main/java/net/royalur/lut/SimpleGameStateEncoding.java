package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleBoard;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Encoding for simple rule-sets.
 */
public class SimpleGameStateEncoding implements GameStateEncoding {

    protected final GameSettings<?> settings;
    protected final FastSimpleFlags flags;
    protected final int warTileCompressionTileCount;
    protected final int[] warTileCompression;
    protected final int warTileBits;
    protected final int safeTileBitsPerPlayer;
    protected final int boardBits;
    protected final int[] warBoardIndices;
    protected final int[] lightSafeBoardIndices;
    protected final int[] darkSafeBoardIndices;

    public SimpleGameStateEncoding(GameSettings<?> settings) {
        if (settings.getStartingPieceCount() > 7)
            throw new IllegalArgumentException("Starting piece counts above 7 are not supported");

        this.settings = settings;
        this.flags = new FastSimpleFlags(settings);
        this.warTileCompressionTileCount = estimateGoodWarTileCompressionTileCount(
                flags.warTileCount
        );
        this.warTileCompression = generateWarTileCompression(
                settings.getStartingPieceCount(), warTileCompressionTileCount
        );
        this.warTileBits = calculateBitsRequired(max(warTileCompression));
        this.safeTileBitsPerPlayer = flags.safeTileCountPerPlayer;
        this.boardBits = 2 * safeTileBitsPerPlayer + warTileBits;
        this.warBoardIndices = gatherBoardIndices(flags.tileFlags, flag -> {
            return (flag & FastSimpleFlags.OCCUPANTS_MASK) == 3;
        });
        this.lightSafeBoardIndices = gatherBoardIndices(flags.tileFlags, flag -> {
            return (flag & FastSimpleFlags.OCCUPANTS_MASK) == 2
                    && (flag & FastSimpleFlags.LIGHT_ONLY_FLAG) != 0;
        });
        this.darkSafeBoardIndices = gatherBoardIndices(flags.tileFlags, flag -> {
            return (flag & FastSimpleFlags.OCCUPANTS_MASK) == 2
                    && (flag & FastSimpleFlags.LIGHT_ONLY_FLAG) == 0;
        });
    }

    private static int max(int[] values) {
        int maxValue = 0;
        for (int value : values) {
            maxValue = Math.max(maxValue, value);
        }
        return maxValue;
    }

    private static int calculateBitsRequired(int maxValue) {
        int bits = 1;
        while (maxValue > (1 << bits)) {
            bits += 1;
        }
        return bits;
    }

    /**
     * More based on testing and heuristics than anything else.
     * For 8 war tiles, we just want to encode it in one segment (13 bits).
     * For 12 war tiles, we want to encode it in two 6-tile segments (10 bits each, 20 bits total).
     */
    private static int estimateGoodWarTileCompressionTileCount(int warTileCount) {
        if (warTileCount <= 8)
            return warTileCount;

        int segments = 2;
        int tileCount;
        do {
            tileCount = (warTileCount + segments - 1) / segments;
            segments += 1;
        } while (tileCount > 8);

        return tileCount;
    }

    private static int[] generateWarTileCompression(int startingPieceCount, int tileCount) {
        int length = 1 << (tileCount * 2);
        int[] compression = new int[length];
        Arrays.fill(compression, -1);

        AtomicInteger index = new AtomicInteger(0);
        loopWarTileStates(
                startingPieceCount, startingPieceCount, 0, tileCount - 1,
                state -> {
                    compression[state] = index.getAndIncrement();
                }
        );
        return compression;
    }

    private static void loopWarTileStates(
            int lightPieces, int darkPieces,
            int state, int index,
            Consumer<Integer> stateConsumer
    ) {
        int nextIndex = index - 1;
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

            int newState = (state << 2) | occupant;
            if (nextIndex < 0) {
                stateConsumer.accept(newState);
            } else {
                loopWarTileStates(
                        newLightPieces, newDarkPieces,
                        newState, nextIndex,
                        stateConsumer
                );
            }
        }
    }

    private static int[] gatherBoardIndices(int[] tileFlags, Function<Integer, Boolean> tileFlagFilter) {
        List<Integer> boardIndicesList = new ArrayList<>();
        for (int boardIndex = 0; boardIndex < tileFlags.length; ++boardIndex) {
            if (!tileFlagFilter.apply(tileFlags[boardIndex]))
                continue;

            boardIndicesList.add(boardIndex);
        }

        int[] boardIndices = new int[boardIndicesList.size()];
        for (int index = 0; index < boardIndices.length; ++index) {
            boardIndices[index] = boardIndicesList.get(index);
        }
        return boardIndices;
    }

    private int encodeWarTiles(FastSimpleBoard board) {
        int[] warBoardIndices = this.warBoardIndices;
        int[] warTileCompression = this.warTileCompression;
        int tileCount = this.warTileCompressionTileCount;
        int warTileBits = this.warTileBits;
        int[] pieces = board.pieces;

        int tileIndex = 0;
        int result = 0;
        do {
            int state = 0;
            for (int index = tileCount - 1; index >= 0; --index) {
                int piece = pieces[warBoardIndices[tileIndex]];
                int occupant = (piece == 0 ? 0 : (piece < 0 ? 1 : 2));
                state = (state << 2) | occupant;

                tileIndex += 1;
                if (tileIndex >= warBoardIndices.length)
                    break;
            }

            int compressed = warTileCompression[state];
            if (compressed == -1)
                throw new IllegalArgumentException("Illegal board state!");

            result = (result << warTileBits) | compressed;

        } while (tileIndex < warBoardIndices.length);

        return result;
    }

    private int encodeSafeTiles(FastSimpleBoard board, int[] safeBoardIndices) {
        int[] pieces = board.pieces;

        int state = 0;
        for (int index = 0; index < safeBoardIndices.length; ++index) {
            int boardIndex = safeBoardIndices[index];
            int piece = pieces[boardIndex];
            int occupant = (piece == 0 ? 0 : 1);
            state |= occupant << index;
        }
        return state;
    }

    private long encodeBoard(FastSimpleBoard board) {
        int safeBits = this.safeTileBitsPerPlayer;
        int warBits = this.warTileBits;

        int lightSafeZone = encodeSafeTiles(board, lightSafeBoardIndices);
        int darkSafeZone = encodeSafeTiles(board, darkSafeBoardIndices);
        int warZone = encodeWarTiles(board);
        return darkSafeZone
                | ((long) warZone << safeBits)
                | ((long) lightSafeZone << (safeBits + warBits));
    }

    @Override
    public long encodeGameState(FastSimpleGame game) {
        int boardBits = this.boardBits;

        if (!game.isLightTurn) {
            throw new IllegalArgumentException(
                    "Only game states where it is the light player's turn are supported by this encoding"
            );
        }

        long state = 0;
        state |= encodeBoard(game.board);
        state |= (long) game.dark.pieces << boardBits;
        state |= (long) game.light.pieces << (boardBits + 3);
        return state;
    }
}
