package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.path.*;
import net.royalur.model.shape.BoardShape;
import net.royalur.model.shape.BoardType;
import net.royalur.rules.RuleSet;
import net.royalur.rules.RuleSetProvider;

/**
 * A builder to help in the creation of custom games of the Royal Game of Ur.
 */
public class GameBuilder {

    /**
     * The settings of the game being built.
     */
    private final GameSettings gameSettings;

    /**
     * The provider to use to construct the final rule set.
     */
    private final RuleSetProvider ruleSetProvider;

    /**
     * Instantiates a new game builder.
     * @param settings The settings of the game being built.
     * @param ruleSetProvider The provider to use to construct the final rule set.
     */
    public GameBuilder(
            GameSettings settings,
            RuleSetProvider ruleSetProvider
    ) {
        this.gameSettings = settings;
        this.ruleSetProvider = ruleSetProvider;
    }

    /**
     * Get the settings of the game being built.
     * @return The settings of the game being built.
     */
    public GameSettings getSettings() {
        return gameSettings;
    }

    /**
     * Gets the provider to use to construct the final rule set.
     * @return The provider to use to construct the final rule set.
     */
    public RuleSetProvider getRuleSetProvider() {
        return ruleSetProvider;
    }

    /**
     * Create a copy of this game builder with new settings.
     * @param settings The new settings.
     * @return A copy of this game builder with new settings.
     */
    public GameBuilder replaceSettings(GameSettings settings) {
        return new GameBuilder(settings, ruleSetProvider);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the simple rules proposed by Irving Finkel.
     * @return A new builder that allows the construction of games
     *         following the rules proposed by Irving Finkel.
     */
    public GameBuilder finkel() {
        return replaceSettings(GameSettings.FINKEL);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the rules proposed by James Masters.
     * @return A new builder that allows the construction of games
     *         following the rules proposed James Masters.
     */
    public GameBuilder masters() {
        return replaceSettings(GameSettings.MASTERS);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the Aseb rules.
     * @return A new builder that allows the construction of games
     *         following the Aseb rules.
     */
    public GameBuilder aseb() {
        return replaceSettings(GameSettings.ASEB);
    }

    /**
     * Copies this game builder with the shape of the board set
     * to {@code boardType}.
     * @param boardType The type of board shape to use for generated games.
     * @return A copy of this game builder with the shape of the board set
     *         to {@code boardType}.
     */
    public GameBuilder boardShape(BoardType boardType) {
        return boardShape(boardType.createBoardShape());
    }

    /**
     * Copies this game builder with the shape of the board set
     * to {@code boardShape}.
     * @param boardShape The shape of the board to use for generated games.
     * @return A copy of this game builder with the shape of the board set
     *         to {@code boardShape}.
     */
    public GameBuilder boardShape(BoardShape boardShape) {
        return replaceSettings(gameSettings.withBoardShape(boardShape));
    }

    /**
     * Copies this game builder with the paths taken by each player set
     * to {@code pathType}.
     * @param pathType The type of paths to be taken by each player.
     * @return A copy of this game builder with the paths taken by each
     *         player set to {@code pathType}.
     */
    public GameBuilder paths(PathType pathType) {
        return paths(pathType.createPathPair());
    }

    /**
     * Copies this game builder with the paths taken by each player set
     * to {@code paths}.
     * @param paths The paths to be taken by each player.
     * @return A copy of this game builder with the paths taken by each
     *         player set to {@code paths}.
     */
    public GameBuilder paths(PathPair paths) {
        return replaceSettings(gameSettings.withPaths(paths));
    }

    /**
     * Copies this game builder with the factory used to generate dice
     * for games set to {@code diceFactory}.
     * @param diceFactory The factory to use to generate dice for games.
     * @return A copy of this game builder with factory used to generate
     *         dice for games set to {@code diceFactory}.
     */
    public GameBuilder dice(DiceFactory diceFactory) {
        return replaceSettings(gameSettings.withDice(diceFactory));
    }

    /**
     * Copies this game builder with the number of starting pieces of each
     * player set to {@code startingPieceCount}.
     * @param startingPieceCount The number of pieces that each player starts
     *                           with in the game.
     * @return A copy of this game builder with the number of starting pieces
     *         of each player set to {@code startingPieceCount}.
     */
    public GameBuilder startingPieceCount(int startingPieceCount) {
        return replaceSettings(gameSettings.withStartingPieceCount(startingPieceCount));
    }

    /**
     * Copies this game builder with whether rosettes are safe from capture
     * set to {@code safeRosettes}.
     * @param safeRosettes Whether rosette tiles are safe squares for pieces.
     * @return A copy of this game builder with safe rosettes if
     *         {@code safeRosettes} is true, or else with unsafe rosettes.
     */
    public GameBuilder safeRosettes(boolean safeRosettes) {
        return replaceSettings(gameSettings.withSafeRosettes(safeRosettes));
    }

    /**
     * Copies this game builder with rosettes granting extra dice rolls
     * if {@code rosettesGrantExtraRolls} is true.
     * @param rosettesGrantExtraRolls Whether landing on a rosette grants
     *                                an extra roll.
     * @return A copy of this game builder with rosettes granting extra
     *         dice rolls set to {@code rosettesGrantExtraRolls}.
     */
    public GameBuilder rosettesGrantExtraRolls(
            boolean rosettesGrantExtraRolls
    ) {
        return replaceSettings(gameSettings.withRosettesGrantExtraRolls(rosettesGrantExtraRolls));
    }

    /**
     * Copies this game builder with capturing pieces granting extra dice
     * rolls if {@code capturesGrantExtraRolls} is true.
     * @param capturesGrantExtraRolls Whether capturing a piece grants an
     *                                extra roll.
     * @return A copy of this game builder with capturing pieces granting
     *         extra dice rolls set to {@code capturesGrantExtraRolls}.
     */
    public GameBuilder capturesGrantExtraRolls(boolean capturesGrantExtraRolls) {
        return replaceSettings(gameSettings.withCapturesGrantExtraRolls(capturesGrantExtraRolls));
    }

    /**
     * Generates a rule set to match the settings in this builder.
     * @return A rule set to match the settings in this builder.
     */
    public RuleSet buildRules() {
        return ruleSetProvider.create(gameSettings, new GameMetadata());
    }

    /**
     * Generates a new game using the rules set in this builder.
     * @return A new game using the rules set in this builder.
     */
    public Game build() {
        return new Game(buildRules());
    }
}
