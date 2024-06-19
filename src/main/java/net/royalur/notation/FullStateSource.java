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
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new RolledGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, turn, roll, availableMoves
        );
    }

    @Override
    public MovedGameState createMovedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll,
            Move move
    ) {
        return new MovedGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, turn, roll, move
        );
    }

    @Override
    public WaitingForRollGameState createWaitingForRollState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn
    ) {
        return new WaitingForRollGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, turn
        );
    }

    @Override
    public WaitingForMoveGameState createWaitingForMoveState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    ) {
        List<Move> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new WaitingForMoveGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, turn, roll, availableMoves
        );
    }

    @Override
    public ResignedGameState createResignedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType player
    ) {
        return new ResignedGameState(
                board, lightPlayer, darkPlayer, timeSinceGameStartMs, player
        );
    }

    @Override
    public AbandonedGameState createAbandonedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            AbandonReason abandonReason,
            @Nullable PlayerType player
    ) {
        return new AbandonedGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, abandonReason, player
        );
    }

    @Override
    public EndGameState createEndState(
            RuleSet rules,
            long timeSinceGameStartMs,
            @Nullable PlayerType winner
    ) {
        return new EndGameState(
                board, lightPlayer, darkPlayer,
                timeSinceGameStartMs, winner
        );
    }
}
