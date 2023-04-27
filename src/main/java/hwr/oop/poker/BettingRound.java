package hwr.oop.poker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BettingRound {
    private final List<Player> players;
    private final List<Play> plays;
    private final Player turn;

    public BettingRound(List<Player> players) {
        this.players = players;
        this.plays = new ArrayList<>();
        this.turn = players.get(0);
    }

    private BettingRound(List<Player> players, Stream<Play> plays, Player turn) {
        this.players = players;
        this.plays = plays.collect(Collectors.toList());
        this.turn = turn;
    }

    public boolean isFinished() {
        return false;
    }

    public Player turn() {
        return turn;
    }

    public RoundInContext with(Player player) {
        return new RoundInContext(player, this);
    }

    public static class RoundInContext {

        private final Player player;
        private final BettingRound bettingRound;

        public RoundInContext(Player player, BettingRound bettingRound) {
            this.player = player;
            this.bettingRound = bettingRound;
        }

        public BettingRound bet(int chipCount) {
            return bettingRound.nextState(new Play(player, chipCount));
        }

        public BettingRound call() {
            return bettingRound.nextState(new Play(player, 0));
        }
    }

    private BettingRound nextState(Play play) {
        return new BettingRound(
                players,
                Stream.concat(plays.stream(), Stream.of(play)),
                next(turn)
        );
    }

    private Player next(Player current) {
        final int currentIndex = players.indexOf(current);
        final int assumedNext = currentIndex + 1;
        return players.get(assumedNext % players.size());
    }
}
