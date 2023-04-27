package hwr.oop.poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        final List<Long> distinctValues = players.stream()
                .map(player -> plays.stream()
                        .filter(play -> play.player().equals(player))
                        .map(Play::chipValue)
                        .map(ChipValue::value)
                        .reduce(Long::sum)
                        .orElse(0L)
                )
                .distinct().collect(Collectors.toList());
        final int numberOfDistinctValues = distinctValues.size();
        final boolean containsZero = distinctValues.contains(0L);
        return numberOfDistinctValues == 1 && !containsZero;
    }

    public Optional<Player> turn() {
        if (isFinished()) {
            return Optional.empty();
        } else {
            return Optional.of(turn);
        }
    }

    public RoundInContext with(Player player) {
        return new RoundInContext(player, this);
    }

    public Optional<Play> lastPlay() {
        if (plays.isEmpty()) {
            return Optional.empty();
        } else {
            final Play lastPlay = plays.get(plays.size() - 1);
            return Optional.of(lastPlay);
        }
    }

    public ChipValue podSize() {
        final Long sumOfAllPlays = plays.stream()
                .map(Play::chipValue)
                .map(ChipValue::value)
                .reduce(Long::sum)
                .orElse(0L);
        return ChipValue.of(sumOfAllPlays);
    }

    public static class RoundInContext {

        private final Player player;
        private final BettingRound bettingRound;

        public RoundInContext(Player player, BettingRound bettingRound) {
            this.player = player;
            this.bettingRound = bettingRound;
        }

        public BettingRound bet(int chipCount) {
            final Play play = new Play(
                    player,
                    ChipValue.of(chipCount),
                    Play.Type.BET
            );
            return bettingRound.nextState(play);
        }

        public BettingRound call() {
            final ChipValue chipValueToCall = bettingRound.lastPlay().orElseThrow().chipValue();
            final Play play = new Play(
                    player,
                    chipValueToCall,
                    Play.Type.CALL
            );
            return bettingRound.nextState(play);
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
