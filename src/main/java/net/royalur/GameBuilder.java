package net.royalur;

import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.*;
import net.royalur.model.shape.BoardShape;
import net.royalur.model.shape.BoardType;
import net.royalur.rules.RuleSet;
import net.royalur.rules.RuleSetProvider;

import javax.annotation.Nonnull;

/**
 * A builder to help in the creation of custom games of the Royal Game of Ur.
 */
public class GameBuilder<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> {

    /**
     * The settings of the game being built.
     */
    private final @Nonnull GameSettings<R> gameSettings;

    /**
     * The provider to use to construct the final rule set.
     */
    private final @Nonnull RuleSetProvider<P, S> ruleSetProvider;

    /**
     * Instantiates a new game builder.
     * @param settings The settings of the game being built.
     * @param ruleSetProvider The provider to use to construct the final rule set.
     */
    public GameBuilder(
            @Nonnull GameSettings<R> settings,
            @Nonnull RuleSetProvider<P, S> ruleSetProvider
    ) {
        this.gameSettings = settings;
        this.ruleSetProvider = ruleSetProvider;
    }

    /**
     * Get the settings of the game being built.
     * @return The settings of the game being built.
     */
    public @Nonnull GameSettings<R> getSettings() {
        return gameSettings;
    }

    /**
     * Gets the provider to use to construct the final rule set.
     * @return The provider to use to construct the final rule set.
     */
    public @Nonnull RuleSetProvider<P, S> getRuleSetProvider() {
        return ruleSetProvider;
    }

    /**
     * Create a copy of this game builder with new settings.
     * @param settings The new settings.
     * @param <NEW_R> The type of the dice rolls generated.
     * @return A copy of this game builder with new settings.
     */
    public <NEW_R extends Roll> @Nonnull GameBuilder<P, S, NEW_R>
    replaceSettings(@Nonnull GameSettings<NEW_R> settings) {
        return new GameBuilder<>(settings, ruleSetProvider);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the simple rules proposed by Irving Finkel.
     * @return A new builder that allows the construction of games
     *         following the rules proposed by Irving Finkel.
     */
    public @Nonnull GameBuilder<P, S, Roll> finkel() {
        return replaceSettings(GameSettings.FINKEL);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the rules proposed by James Masters.
     * @return A new builder that allows the construction of games
     *         following the rules proposed James Masters.
     */
    public @Nonnull GameBuilder<P, S, Roll> masters() {
        return replaceSettings(GameSettings.MASTERS);
    }

    /**
     * Creates a new builder that allows the construction of games
     * following the Aseb rules.
     * @return A new builder that allows the construction of games
     *         following the Aseb rules.
     */
    public @Nonnull GameBuilder<P, S, Roll> aseb() {
        return replaceSettings(GameSettings.ASEB);
    }

    /**
     * Copies this game builder with the shape of the board set
     * to {@code boardType}.
     * @param boardType The type of board shape to use for generated games.
     * @return A copy of this game builder with the shape of the board set
     *         to {@code boardType}.
     */
    public @Nonnull GameBuilder<P, S, R> boardShape(@Nonnull BoardType boardType) {
        return boardShape(boardType.createBoardShape());
    }

    /**
     * Copies this game builder with the shape of the board set
     * to {@code boardShape}.
     * @param boardShape The shape of the board to use for generated games.
     * @return A copy of this game builder with the shape of the board set
     *         to {@code boardShape}.
     */
    public @Nonnull GameBuilder<P, S, R> boardShape(@Nonnull BoardShape boardShape) {
        return replaceSettings(gameSettings.withBoardShape(boardShape));
    }

    /**
     * Copies this game builder with the paths taken by each player set
     * to {@code pathType}.
     * @param pathType The type of paths to be taken by each player.
     * @return A copy of this game builder with the paths taken by each
     *         player set to {@code pathType}.
     */
    public @Nonnull GameBuilder<P, S, R> paths(@Nonnull PathType pathType) {
        return paths(pathType.createPathPair());
    }

    /**
     * Copies this game builder with the paths taken by each player set
     * to {@code paths}.
     * @param paths The paths to be taken by each player.
     * @return A copy of this game builder with the paths taken by each
     *         player set to {@code paths}.
     */
    public @Nonnull GameBuilder<P, S, R> paths(@Nonnull PathPair paths) {
        return replaceSettings(gameSettings.withPaths(paths));
    }

    /**
     * Copies this game builder with the factory used to generate dice
     * for games set to {@code diceFactory}.
     * @param diceFactory The factory to use to generate dice for games.
     * @param <NEW_R> The type of the dice rolls generated.
     * @return A copy of this game builder with factory used to generate
     *         dice for games set to {@code diceFactory}.
     */
    public <NEW_R extends Roll> @Nonnull GameBuilder<P, S, NEW_R>
    dice(@Nonnull DiceFactory<NEW_R> diceFactory) {
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
    public @Nonnull GameBuilder<P, S, R> startingPieceCount(int startingPieceCount) {
        return replaceSettings(gameSettings.withStartingPieceCount(startingPieceCount));
    }

    /**
     * Copies this game builder with whether rosettes are safe from capture
     * set to {@code safeRosettes}.
     * @param safeRosettes Whether rosette tiles are safe squares for pieces.
     * @return A copy of this game builder with safe rosettes if
     *         {@code safeRosettes} is true, or else with unsafe rosettes.
     */
    public @Nonnull GameBuilder<P, S, R> safeRosettes(boolean safeRosettes) {
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
    public @Nonnull GameBuilder<P, S, R> rosettesGrantExtraRolls(
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
    public @Nonnull GameBuilder<P, S, R> capturesGrantExtraRolls(
            boolean capturesGrantExtraRolls
    ) {
        return replaceSettings(gameSettings.withCapturesGrantExtraRolls(capturesGrantExtraRolls));
    }

    /**
     * Generates a rule set to match the settings in this builder.
     * @return A rule set to match the settings in this builder.
     */
    public @Nonnull RuleSet<P, S, R> buildRules() {
        return ruleSetProvider.create(gameSettings, new GameMetadata());
    }

    /**
     * Generates a new game using the rules set in this builder.
     * @return A new game using the rules set in this builder.
     */
    public @Nonnull Game<P, S, R> build() {
        return new Game<>(buildRules());
    }
}
