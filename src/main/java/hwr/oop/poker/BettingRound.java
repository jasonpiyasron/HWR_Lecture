package hwr.oop.poker;

import java.util.ArrayList;
import java.util.Collection;
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

    public RoundInContext with(Player player) {
        return new RoundInContext(player, this);
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
                .map(this::chipsPutIntoPotBy)
                .distinct()
                .collect(Collectors.toList());
        final int numberOfDistinctValues = distinctValues.size();
        final boolean containsZero = distinctValues.contains(ChipValue.zero());
        return numberOfDistinctValues == 1 && !containsZero;
    }

    public Optional<Player> turn() {
        if (isFinished()) {
            return Optional.empty();
        } else {
            return Optional.of(turn);
        }
    }

    public Optional<Play> lastPlay() {
        if (plays.isEmpty()) {
            return Optional.empty();
        } else {
            final Play lastPlay = plays.get(plays.size() - 1);
            return Optional.of(lastPlay);
        }
    }

    public Optional<Play> lastChipCountIncreasingPlay() {
        return plays.stream()
                .filter(Play::increasedChips)
                .reduce((a, b) -> b);
    }

    public ChipValue pot() {
        final Long sumOfAllPlays = plays.stream()
                .map(Play::chipValue)
                .map(ChipValue::value)
                .reduce(Long::sum)
                .orElse(0L);
        return ChipValue.of(sumOfAllPlays);
    }

    public ChipValue chipsPutIntoPotBy(Player player) {
        return plays.stream()
                .filter(play -> play.playedBy(player))
                .map(Play::chipValue)
                .reduce(ChipValue::plus)
                .orElse(ChipValue.zero());
    }

    public BettingRound nextState(Play play) {
        assertCorrectPlayer(play);
        return new BettingRound(
                players,
                Stream.concat(plays.stream(), Stream.of(play)),
                next(turn)
        );
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

    private boolean allPlaysAreChecks() {
        return plays.stream().allMatch(Play::isCheck);
    }

    private boolean allPlayersHavePlayed() {
        return players.stream().allMatch(
                player -> plays.stream()
                        .anyMatch(play -> play.playedBy(player))
        );
    }


    private void assertCorrectPlayer(Play play) {
        final boolean correctPlayer = play.playedBy(turn);
        if (!correctPlayer) {
            throw new InvalidPlayOnStateException(
                    "Cannot play " + play +
                            ", wrong player: " + play.player() +
                            ", next player is: " + turn);
        }
    }

    private Player next(Player current) {
        final Player candidate = nextCandidatePlayer(current);
        final boolean candidateFolded = playerHasFolded(candidate);
        if (candidateFolded) {
            return next(candidate);
        } else {
            return candidate;
        }
    }

    private Player nextCandidatePlayer(Player current) {
        final int indexOfCandidatePlayer =
                (players.indexOf(current) + 1) % players.size();
        return players.get(indexOfCandidatePlayer);
    }

    private boolean playerHasFolded(Player candidate) {
        return plays.stream()
                .filter(Play::isFold)
                .anyMatch(play -> play.playedBy(candidate));
    }

    public Collection<Player> remainingPlayers() {
        return players.stream()
                .filter(this::hasPlayerNotFolded)
                .collect(Collectors.toList());
    }

    private boolean hasPlayerNotFolded(Player player) {
        return plays.stream().noneMatch(play -> play.playedBy(player) && play.isFold());
    }

    public static class InvalidPlayOnStateException extends RuntimeException {
        public InvalidPlayOnStateException(String message) {
            super(message);
        }
    }
}
