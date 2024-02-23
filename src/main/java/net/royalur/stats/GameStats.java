package net.royalur.stats;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.rules.state.ActionGameState;
import net.royalur.rules.state.GameState;
import net.royalur.rules.state.MovedGameState;
import net.royalur.rules.state.RolledGameState;

/**
 * Statistics about a game of the Royal Game of Ur.
 */
public class GameStats {

    /**
     * Whether the light player won the game.
     */
    private final boolean didLightWin;

    /**
     * The number of rolls performed in the game, indexed by
     * the ordinal of an element of {@link GameStatsTarget}.
     */
    private final int[] rolls;

    /**
     * The number of moves made in the game, indexed by the
     * ordinal of an element of {@link GameStatsTarget}.
     */
    private final int[] moves;

    /**
     * The number of turns in the game, indexed by the ordinal
     * of an element of {@link GameStatsTarget}.
     */
    private final int[] turns;

    /**
     * The number of times the lead player swapped during each game,
     * indexed by the ordinal of an element of {@link GameStatsTarget}.
     * The lead player is defined by the player with the higher count
     * of piece advancement. For the lead to have changed, the non-lead
     * player must hold an advantage for 2 turns (e.g., gain an advantage
     * on their turn, and then keep that advantage on their opponent's
     * turn).
     */
    private final int[] drama;

    /**
     * The number of turns that the winner held the lead before winning the game.
     */
    private final int turnsInLead;

    /**
     * Instantiates statistics about a game of the Royal Game of Ur.
     * @param didLightWin Whether the light player won the game.
     * @param rolls The number of rolls performed in the game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     * @param moves The number of moves made in the game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     * @param turns The number of turns in the game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     * @param drama The number of times the lead player swapped during each game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     * @param turnsInLead The number of turns that the winner held the lead before
     *                    winning the game.
     */
    protected GameStats(
            boolean didLightWin,
            int[] rolls,
            int[] moves,
            int[] turns,
            int[] drama,
            int turnsInLead
    ) {
        int targetCount = GameStatsTarget.values().length;
        if (rolls.length != targetCount)
            throw new IllegalArgumentException("rolls is wrong length");
        if (moves.length != targetCount)
            throw new IllegalArgumentException("moves is wrong length");
        if (turns.length != targetCount)
            throw new IllegalArgumentException("turns is wrong length");
        if (drama.length != targetCount)
            throw new IllegalArgumentException("drama is wrong length");

        this.didLightWin = didLightWin;
        this.rolls = rolls;
        this.moves = moves;
        this.turns = turns;
        this.drama = drama;
        this.turnsInLead = turnsInLead;
    }

    /**
     * Returns whether the light player won the game.
     * @return Whether the light player won the game.
     */
    public boolean didLightWin() {
        return didLightWin;
    }

    /**
     * Gets the number of rolls counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of rolls counted for the target {@code target}.
     */
    public int getRolls(GameStatsTarget target) {
        return rolls[target.ordinal()];
    }

    /**
     * Gets the number of rolls performed by {@code player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of rolls performed by {@code player}.
     */
    public int getRolls(PlayerType player) {
        return getRolls(GameStatsTarget.get(player));
    }

    /**
     * Gets the number of moves counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of moves counted for the target {@code target}.
     */
    public int getMoves(GameStatsTarget target) {
        return moves[target.ordinal()];
    }

    /**
     * Gets the number of moves made by {@code player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of moves made by {@code player}.
     */
    public int getMoves(PlayerType player) {
        return getMoves(GameStatsTarget.get(player));
    }

    /**
     * Gets the number of turns counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of turns counted for the target {@code target}.
     */
    public int getTurns(GameStatsTarget target) {
        return turns[target.ordinal()];
    }

    /**
     * Gets the number of turns that {@code player} had.
     * @param player The player to retrieve the statistic about.
     * @return The number of turns that {@code player} had.
     */
    public int getTurns(PlayerType player) {
        return getTurns(GameStatsTarget.get(player));
    }

    /**
     * Gets the drama counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The drama counted for the target {@code target}.
     */
    public int getDrama(GameStatsTarget target) {
        return drama[target.ordinal()];
    }

    /**
     * Gets the number of times that {@code player} took the lead from behind.
     * @param player The player to retrieve the statistic about.
     * @return The number of times that {@code player} took the lead from behind.
     */
    public int getDrama(PlayerType player) {
        return getDrama(GameStatsTarget.get(player));
    }

    /**
     * Gets the number of turns that the winner held the lead before winning.
     * @return The number of turns that the winner held the lead before winning.
     */
    public int getTurnsInLead() {
        return turnsInLead;
    }

    /**
     * Gets the percentage of turns that the winner held the lead before winning.
     * @return The percentage of turns that the winner held the lead before winning.
     */
    public double getPercentInLead() {
        return (double) turnsInLead / getTurns(GameStatsTarget.OVERALL);
    }

    /**
     * Gets the total number of rolls performed by both players.
     * @return The total number of rolls performed by both players.
     */
    public int getTotalRolls() {
        return getRolls(GameStatsTarget.OVERALL);
    }

    /**
     * Gets the total number of moves made by both players.
     * @return The total number of moves made by both players.
     */
    public int getTotalMoves() {
        return getMoves(GameStatsTarget.OVERALL);
    }

    /**
     * Gets the total number of turns in the game.
     * @return The total number of turns in the game.
     */
    public int getTotalTurns() {
        return getTurns(GameStatsTarget.OVERALL);
    }

    /**
     * Gets the total count of drama in the game.
     * @return The total count of drama in the game.
     */
    public int getTotalDrama() {
        return getDrama(GameStatsTarget.OVERALL);
    }

    private static int calculatePiecesAdvancedUtilityForLight(GameState state) {
        PlayerState light = state.getLightPlayer();
        PlayerState dark = state.getDarkPlayer();
        Board board = state.getBoard();

        int utility = light.getScore() - dark.getScore();

        for (Tile tile : board.getShape().getTiles()) {
            Piece piece = board.get(tile);
            if (piece != null) {
                switch (piece.getOwner()) {
                    case LIGHT -> {
                        utility += piece.getPathIndex() + 1;
                    }
                    case DARK -> {
                        utility -= piece.getPathIndex() + 1;
                    }
                }
            }
        }
        return utility;
    }

    /**
     * Gathers statistics about the game {@code game}.
     * @param game The game to gather statistics about.
     * @return The statistics gathered about the game.
     */
    public static GameStats gather(Game game) {
        int targetCount = GameStatsTarget.values().length;
        int[] rolls = new int[targetCount];
        int[] moves = new int[targetCount];
        int[] turns = new int[targetCount];
        int[] drama = new int[targetCount];

        ActionGameState lastAction = null;
        PlayerType currentLead = null;
        int losingLeadTurns = 0;
        int turnsInLead = 0;

        // Count all the rolls and moves.
        for (GameState state : game.getStates()) {
            if (!(state instanceof ActionGameState actionState))
                continue;

            PlayerType player = actionState.getTurnPlayer().getPlayer();

            if (actionState instanceof RolledGameState) {
                rolls[GameStatsTarget.OVERALL.ordinal()] += 1;
                rolls[GameStatsTarget.get(player).ordinal()] += 1;

            } else if (actionState instanceof MovedGameState move) {
                moves[GameStatsTarget.OVERALL.ordinal()] += 1;
                moves[GameStatsTarget.get(player).ordinal()] += 1;
            }

            if (lastAction != null) {
                if (lastAction.getTurnPlayer() != actionState.getTurnPlayer()) {
                    turns[GameStatsTarget.OVERALL.ordinal()] += 1;
                    turns[GameStatsTarget.get(player).ordinal()] += 1;

                    int currUtility = calculatePiecesAdvancedUtilityForLight(actionState);
                    if (currUtility != 0) {
                        PlayerType lead = (currUtility < 0 ? PlayerType.DARK : PlayerType.LIGHT);
                        turnsInLead += 1;
                        if (currentLead != lead) {
                            losingLeadTurns += 1;
                            if (losingLeadTurns >= 2) {
                                drama[GameStatsTarget.OVERALL.ordinal()] += 1;
                                drama[GameStatsTarget.get(lead).ordinal()] += 1;
                                currentLead = lead;
                                turnsInLead = losingLeadTurns;
                                losingLeadTurns = 0;
                            }
                        }
                    }
                }

            } else {
                turns[GameStatsTarget.OVERALL.ordinal()] += 1;
                turns[GameStatsTarget.get(player).ordinal()] += 1;

                double currUtility = calculatePiecesAdvancedUtilityForLight(actionState);
                currentLead = (currUtility < 0 ? PlayerType.DARK : PlayerType.LIGHT);
                losingLeadTurns = 0;
                turnsInLead += 1;
            }

            lastAction = actionState;
        }

        // Create the statistics container.
        return new GameStats(game.getWinner() == PlayerType.LIGHT, rolls, moves, turns, drama, turnsInLead);
    }

    /**
     * Summarises the statistics of all the given game statistics from {@code stats}.
     * This includes the generation of statistics such as sum, mean, variance, and
     * standard deviation.
     * @param stats The statistics to summarise.
     * @return The summarised statistics of all the statistics in {@code stats}.
     */
    public static GameStatsSummary summarise(GameStats... stats) {
        return GameStatsSummary.summarise(stats);
    }
}
