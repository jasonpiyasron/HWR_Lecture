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
        if (allPlayersHavePlayed() && allPlaysAreChecks()) {
            return true;
        }
        if (isOnlyOnePlayerRemaining()) {
            return true;
        }
        final List<ChipValue> distinctValues = players.stream()
                .filter(player -> playersThatHaveFolded().noneMatch(p -> p.equals(player)))
                .map(this::sumOfChipsPlayedBy)
                .distinct()
                .collect(Collectors.toList());
        final int numberOfDistinctValues = distinctValues.size();
        final boolean containsZero = distinctValues.contains(ChipValue.zero());
        return numberOfDistinctValues == 1 && !containsZero;
    }

    private boolean isOnlyOnePlayerRemaining() {
        final long numberOfPlayersThatHaveNotFolded = players.stream()
                .filter(player -> playersThatHaveFolded().noneMatch(p -> p.equals(player)))
                .count();
        return numberOfPlayersThatHaveNotFolded < 2;
    }

    private Stream<Player> playersThatHaveFolded() {
        return plays.stream()
                .filter(p -> p.type() == Play.Type.FOLD)
                .map(Play::player);
    }

    private ChipValue sumOfChipsPlayedBy(Player player) {
        return plays.stream()
                .filter(play -> play.playedBy(player))
                .map(Play::chipValue)
                .reduce(ChipValue::sum)
                .orElse(ChipValue.zero());
    }

    private boolean allPlaysAreChecks() {
        return plays.stream()
                .allMatch(play -> play.type() == Play.Type.CHECK);
    }

    private boolean allPlayersHavePlayed() {
        return players.stream().allMatch(
                player -> plays.stream()
                        .anyMatch(play -> play.playedBy(player))
        );
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

    public ChipValue pot() {
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
                    ChipValue.of(chipCount),
                    Play.Type.BET
            );
            return bettingRound.nextState(play);
        }

        public BettingRound call() {
            final ChipValue alreadyPlayed = chipsAlreadyPlayedByPlayer();
            final ChipValue diff = chipValueToCall();
            final ChipValue target = ChipValue.sum(diff, alreadyPlayed);
            final Play play = new Play(
                    player,
                    target,
                    diff,
                    Play.Type.CALL
            );
            return bettingRound.nextState(play);
        }

        public BettingRound fold() {
            final Play play = Play.fold(player);
            return bettingRound.nextState(play);
        }

        public BettingRound raiseTo(int amount) {
            final ChipValue target = ChipValue.of(amount);
            final ChipValue alreadyPlayedByPlayer = chipsAlreadyPlayedByPlayer();
            final ChipValue diff = ChipValue.subtract(target, alreadyPlayedByPlayer);
            final Play play = Play.raiseBy(player, target, diff);
            return bettingRound.nextState(play);
        }

        public BettingRound check() {
            final Play play = Play.check(player);
            return bettingRound.nextState(play);
        }

        private ChipValue chipsAlreadyPlayedByPlayer() {
            return bettingRound.plays.stream()
                    .filter(play -> play.playedBy(player))
                    .map(Play::chipValue)
                    .reduce(ChipValue::sum)
                    .orElse(ChipValue.zero());
        }

        private ChipValue chipValueToCall() {
            final Optional<Play> lastIncreasingPlay = bettingRound.plays.stream()
                    .filter(Play::increasedChips)
                    .reduce((a, b) -> b);
            final Play bettingPlay = lastIncreasingPlay.orElseThrow();
            final ChipValue allChipsPutByPlayer = bettingPlay.totalChipValue();
            return ChipValue.subtract(allChipsPutByPlayer, chipsAlreadyPlayedByPlayer());
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
        final Player candidate = players.get(assumedNext % players.size());
        if (playerHasFolded(candidate)) {
            return next(candidate);
        } else {
            return candidate;
        }
    }

    private boolean playerHasFolded(Player candidate) {
        return plays.stream()
                .filter(play -> play.playedBy(candidate))
                .map(Play::type)
                .anyMatch(t -> t == Play.Type.FOLD);
    }
}
