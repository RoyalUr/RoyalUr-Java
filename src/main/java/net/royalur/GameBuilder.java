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
import net.royalur.rules.simple.SimpleGame;
import net.royalur.rules.simple.SimplePiece;
import net.royalur.rules.simple.SimpleRuleSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Builders to help create games more easily, without sacrificing
 * the ability to replace several components of the games as desired.
 */
public class GameBuilder {

    /**
     * A builder for constructing games that follow the simple rules, without custom dice.
     * @param <R> The type of rolls that the dice produce.
     * @param <SELF> The type of the subclass of this builder.
     */
    public static abstract class BaseSimpleGameBuilder<R extends Roll, SELF extends BaseSimpleGameBuilder<R, SELF>> {

        /**
         * The shape of the board.
         */
        protected final @Nonnull BoardShape boardShape;

        /**
         * The paths that each player must take around the board.
         */
        protected final @Nonnull PathPair paths;

        /**
         * The number of pieces that each player starts with.
         */
        protected final int startingPieceCount;

        /**
         * The name of the light player.
         */
        protected final @Nullable String lightPlayerName;

        /**
         * The name of the dark player.
         */
        protected final @Nullable String darkPlayerName;

        /**
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param lightPlayerName The name of the light player.
         * @param darkPlayerName The name of the dark player.
         */
        public BaseSimpleGameBuilder(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            this.boardShape = boardShape;
            this.paths = paths;
            this.startingPieceCount = startingPieceCount;
            this.lightPlayerName = lightPlayerName;
            this.darkPlayerName = darkPlayerName;
        }

        /**
         * Creates a copy of this game builder with new settings.
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param lightPlayerName The name of the light player.
         * @param darkPlayerName The name of the dark player.
         * @return A copy of this game builder with updated settings.
         */
        protected abstract @Nonnull SELF copy(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        );

        /**
         * Returns a new game builder with the shape of the board in generated games
         * set to {@param boardShape}.
         * @param boardShape The shape of the board for generated games.
         * @return A new game builder with the shape of the board set to {@param boardShape}.
         */
        public @Nonnull SELF boardShape(@Nonnull BoardShape boardShape) {
            return copy(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        /**
         * Returns a new game builder with the paths taken by each player set to {@param paths}.
         * @param paths The paths to be taken by each player.
         * @return A new game builder with the shape of the board set to {@param boardShape}.
         */
        public @Nonnull SELF paths(@Nonnull PathPair paths) {
            return copy(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        public abstract <NR extends Roll> @Nonnull BaseSimpleGameBuilder<NR, ?> dice(@Nonnull Dice<NR> dice);

        public @Nonnull SELF startingPieceCount(int startingPieceCount) {
            return copy(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        public @Nonnull SELF lightPlayerName(@Nullable String lightPlayerName) {
            return copy(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        public @Nonnull SELF noLightPlayerName() {
            return lightPlayerName(null);
        }

        public @Nonnull SELF darkPlayerName(@Nullable String darkPlayerName) {
            return copy(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        public @Nonnull SELF noDarkPlayerName() {
            return darkPlayerName(null);
        }

        public abstract @Nonnull SimpleRuleSet<SimplePiece, PlayerState, R> buildRules();

        protected @Nonnull PlayerState buildPlayerState(
                SimpleRuleSet<SimplePiece, PlayerState, R> rules,
                @Nonnull Player player,
                @Nullable String name
        ) {
            if (name != null)
                return rules.generateNewPlayerState(player, name);
            else
                return rules.generateNewPlayerState(player);
        }

        public abstract @Nonnull Game<SimplePiece, PlayerState, R> build();
    }

    /**
     * TODO
     */
    public static class SimpleGameBuilder extends BaseSimpleGameBuilder<Roll, SimpleGameBuilder> {

        /**
         * @param boardShape         The shape of the board.
         * @param paths              The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param lightPlayerName    The name of the light player.
         * @param darkPlayerName     The name of the dark player.
         */
        public SimpleGameBuilder(@Nonnull BoardShape boardShape, @Nonnull PathPair paths, int startingPieceCount, @Nullable String lightPlayerName, @Nullable String darkPlayerName) {
            super(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        @Override
        protected @Nonnull SimpleGameBuilder copy(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            return new SimpleGameBuilder(
                    boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        @Override
        public <R extends Roll> @Nonnull SimpleWithDiceGameBuilder<R> dice(@Nonnull Dice<R> dice) {
            return new SimpleWithDiceGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        @Override
        public @Nonnull SimpleRuleSet<SimplePiece, PlayerState, Roll> buildRules() {
            return new ConcreteSimpleRuleSet<>(boardShape, paths, new StandardDice(), startingPieceCount);
        }

        @Override
        public @Nonnull SimpleGame build() {
            SimpleRuleSet<SimplePiece, PlayerState, Roll> rules = buildRules();
            return new SimpleGame(rules, List.of(
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
     * TODO
     * @param <R> The type of rolls that the dice produce.
     */
    public static class SimpleWithDiceGameBuilder<R extends Roll>
            extends BaseSimpleGameBuilder<R, SimpleWithDiceGameBuilder<R>> {

        /**
         * The dice that is used to generate rolls.
         */
        private final @Nonnull Dice<R> dice;

        /**
         * @param boardShape         The shape of the board.
         * @param paths              The paths that each player must take around the board.
         * @param dice The dice that is used to generate rolls.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param lightPlayerName    The name of the light player.
         * @param darkPlayerName     The name of the dark player.
         */
        public SimpleWithDiceGameBuilder(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                @Nonnull Dice<R> dice,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            super(boardShape, paths, startingPieceCount, lightPlayerName, darkPlayerName);
            this.dice = dice;
        }

        protected <NR extends Roll> @Nonnull SimpleWithDiceGameBuilder<NR> copy(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                @Nonnull Dice<NR> dice,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            return new SimpleWithDiceGameBuilder<>(
                    boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName
            );
        }

        @Override
        protected @Nonnull SimpleWithDiceGameBuilder<R> copy(
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount,
                @Nullable String lightPlayerName,
                @Nullable String darkPlayerName
        ) {
            return copy(boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        @Override
        public <NR extends Roll> @Nonnull SimpleWithDiceGameBuilder<NR> dice(@Nonnull Dice<NR> dice) {
            return copy(boardShape, paths, dice, startingPieceCount, lightPlayerName, darkPlayerName);
        }

        @Override
        public @Nonnull SimpleRuleSet<SimplePiece, PlayerState, R> buildRules() {
            return new ConcreteSimpleRuleSet<>(boardShape, paths, dice, startingPieceCount);
        }

        @Override
        public @Nonnull Game<SimplePiece, PlayerState, R> build() {
            SimpleRuleSet<SimplePiece, PlayerState, R> rules = buildRules();
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
    public @Nonnull SimpleGameBuilder standard() {
        return new SimpleGameBuilder(
                new StandardBoardShape(),
                new BellPathPair(),
                7,
                null, null
        );
    }

    /**
     * Creates a new builder that allows the construction of games following the simple rules.
     * @return A new builder that allows the construction of games following the simple rules.
     */
    public @Nonnull SimpleGameBuilder simpleRules() {
        return standard();
    }

    /**
     * Creates a new builder that allows the construction of games following the Aseb rules.
     * @return A new builder that allows the construction of games following the Aseb rules.
     */
    public @Nonnull SimpleGameBuilder aseb() {
        return new SimpleGameBuilder(
                new AsebBoardShape(),
                new AsebPathPair(),
                5,
                null, null
        );
    }
}
