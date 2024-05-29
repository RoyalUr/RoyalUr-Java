package net.royalur.notation;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Produces game states from scratch.
 */
public class FullStateSource extends StateSource {

    private final Board board;
    private final PlayerState lightPlayer;
    private final PlayerState darkPlayer;

    public FullStateSource(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer
    ) {
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    private PlayerState getPlayer(PlayerType turn) {
        return switch (turn) {
            case LIGHT -> lightPlayer;
            case DARK -> darkPlayer;
        };
    }

    @Override
    public RolledGameState createRolledState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new RolledGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public MovedGameState createMovedState(
            RuleSet rules,
            PlayerType turn,
            Roll roll,
            Move move
    ) {
        return new MovedGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, move
        );
    }

    @Override
    public WaitingForRollGameState createWaitingForRollState(
            RuleSet rules,
            PlayerType turn
    ) {
        return new WaitingForRollGameState(
                board, lightPlayer, darkPlayer, turn
        );
    }

    @Override
    public WaitingForMoveGameState createWaitingForMoveState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new WaitingForMoveGameState(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public ResignedGameState createResignedState(
            RuleSet rules,
            PlayerType player
    ) {
        return new ResignedGameState(
                board, lightPlayer, darkPlayer, player
        );
    }

    @Override
    public AbandonedGameState createAbandonedState(
            RuleSet rules,
            AbandonReason abandonReason,
            @Nullable PlayerType player
    ) {
        return new AbandonedGameState(
                board, lightPlayer, darkPlayer, abandonReason, player
        );
    }

    @Override
    public EndGameState createEndState(
            RuleSet rules,
            @Nullable PlayerType winner
    ) {
        return new EndGameState(
                board, lightPlayer, darkPlayer, winner
        );
    }
}
