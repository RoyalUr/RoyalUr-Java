package net.royalur;

import net.royalur.model.*;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.model.state.WaitingForRollGameState;
import net.royalur.rules.Dice;
import net.royalur.rules.StandardDice;
import net.royalur.rules.simple.ConcreteSimpleRuleSet;
import net.royalur.rules.simple.SimplePiece;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * A builder for helping to create custom games more easily.
 */
public class GameBuilder {

    /**
     * A builder for constructing games that follow the simple rules.
     * @param <R> The type of rolls that the dice produce.
     */
    public static class SimpleGameBuilder<R extends Roll> {

        /**
         * The shape of the board.
         */
        private final @Nonnull BoardShape boardShape;

        /**
         * The paths that each player must take around the board.
         */
        private final @Nonnull PathPair paths;

        /**
         * The dice that is used to generate rolls.
         */
        private final @Nonnull Dice<R> dice;

        /**
         * The number of pieces that each player starts with.
         */
        private final int startingPieceCount;

        /**
         * The name of the light player.
         */
        private final @Nullable String lightPlayerName;

        /**
         * The name of the dark player.
         */
        private final @Nullable String darkPlayerName;

        /**
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param dice The dice that is used to generate rolls.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param lightPlayerName The name of the light player.
         * @param darkPlayerName The name of the dark player.
         */
        public SimpleGameBuilder(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                @Nonnull Dice<R> dice,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            this.boardShape = boardShape;
            this.paths = paths;
            this.dice = dice;
            this.startingPieceCount = startingPieceCount;
            this.lightPlayerName = lightPlayerName;
            this.darkPlayerName = darkPlayerName;
        }

        /**
         * Returns a new game builder with the shape of the board in generated games
         * set to {@param boardShape}.
         * @param boardShape The shape of the board for generated games.
         * @return A new game builder with the shape of the board set to {@param boardShape}.
         */
        public @Nonnull SimpleGameBuilder<R> boardShape(@Nonnull BoardShape boardShape) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        /**
         * Returns a new game builder with the paths taken by each player set to {@param paths}.
         * @param paths The paths to be taken by each player.
         * @return A new game builder with the shape of the board set to {@param boardShape}.
         */
        public @Nonnull SimpleGameBuilder<R> paths(@Nonnull PathPair paths) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        public <NR extends Roll> @Nonnull SimpleGameBuilder<NR> dice(@Nonnull Dice<NR> dice) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        public @Nonnull SimpleGameBuilder<R> startingPieceCount(int startingPieceCount) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        public @Nonnull SimpleGameBuilder<R> lightPlayerName(@Nullable String lightPlayerName) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        public @Nonnull SimpleGameBuilder<R> noLightPlayerName() {
            return lightPlayerName(null);
        }

        public @Nonnull SimpleGameBuilder<R> darkPlayerName(@Nullable String darkPlayerName) {
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        public @Nonnull SimpleGameBuilder<R> noDarkPlayerName() {
            return darkPlayerName(null);
        }

        public @Nonnull SimpleGameBuilder<R> playerNames(
                @Nullable String playerOneName, @Nullable String playerTwoName, @Nonnull Random random) {

            // Randomly set the player that will be light or dark.
            if (random.nextBoolean()) {
                String temp = playerOneName;
                playerOneName = playerTwoName;
                playerTwoName = temp;
            }
            return new SimpleGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, playerOneName, playerTwoName
            );
        }

        public @Nonnull ConcreteSimpleRuleSet<R> buildRules() {
            return new ConcreteSimpleRuleSet<>(boardShape, paths, dice, startingPieceCount);
        }

        private @Nonnull PlayerState buildPlayerState(
                ConcreteSimpleRuleSet<R> rules, @Nonnull Player player, @Nullable String name) {

            if (name != null)
                return rules.generateNewPlayerState(player, name);
            else
                return rules.generateNewPlayerState(player);
        }

        public @Nonnull Game<SimplePiece, PlayerState, R> build() {
            ConcreteSimpleRuleSet<R> rules = buildRules();
            return new Game<>(rules, List.of(
                    new WaitingForRollGameState<>(
                            rules.generateEmptyBoard(),
                            buildPlayerState(rules, Player.LIGHT, lightPlayerName),
                            buildPlayerState(rules, Player.DARK, darkPlayerName),
                            Player.LIGHT
                    )
            ));
        }
    }

    /**
     * Creates a new builder that allows the construction of games following the standard rules
     * from RoyalUr.net.
     * @return A new builder that allows the construction of games following the standard rules.
     */
    public @Nonnull SimpleGameBuilder<Roll> standard() {
        return new SimpleGameBuilder<>(
                new StandardBoardShape(),
                new BellPathPair(),
                new StandardDice(),
                7,
                null, null
        );
    }

    /**
     * Creates a new builder that allows the construction of games following the simple rules.
     * @return A new builder that allows the construction of games following the simple rules.
     */
    public @Nonnull SimpleGameBuilder<Roll> simpleRules() {
        return standard();
    }

    /**
     * Creates a new builder that allows the construction of games following the Aseb rules.
     * @return A new builder that allows the construction of games following the Aseb rules.
     */
    public @Nonnull SimpleGameBuilder<Roll> aseb() {
        return new SimpleGameBuilder<>(
                new AsebBoardShape(),
                new AsebPathPair(),
                new StandardDice(),
                5,
                null, null
        );
    }
}
