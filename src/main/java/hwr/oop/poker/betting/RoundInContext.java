package hwr.oop.poker.betting;

import hwr.oop.poker.ChipValue;
import hwr.oop.poker.Player;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class RoundInContext {

    private final Player player;
    private final Supplier<ChipValue> chipsPutIntoPotByPlayer;
    private final Supplier<Optional<Play>> lastChipCountIncreasingPlay;
    private final Function<Play, BettingRound> stateTransition;
    private final Function<Player, ChipValue> remainingChipsProvider;

    public RoundInContext(Player player, BettingRound bettingRound) {
        this.player = player;
        this.chipsPutIntoPotByPlayer = () -> bettingRound.chipsPutIntoPotBy(player);
        this.lastChipCountIncreasingPlay = bettingRound::lastChipCountIncreasingPlay;
        this.stateTransition = bettingRound::nextState;
        this.remainingChipsProvider = bettingRound::remainingChips;
    }

    public BettingRound fold() {
        final Play play = Play.fold(player);
        return stateTransition.apply(play);
    }

    public BettingRound check() {
        final Optional<Play> lastIncreasingPlay = lastChipCountIncreasingPlay.get();
        if (lastIncreasingPlay.isPresent()) {
            throw new BettingRound.InvalidPlayOnStateException(
                    "Cannot CHECK, need to CALL/RAISE/FOLD to: " + lastIncreasingPlay.get()
            );
        }
        final Play play = Play.check(player);
        return stateTransition.apply(play);
    }

    public BettingRound bet(long value) {
        final Optional<Play> lastIncreasingPlay = lastChipCountIncreasingPlay.get();
        if (lastIncreasingPlay.isPresent()) {
            final Play play = lastIncreasingPlay.get();
            throw new BettingRound.InvalidPlayOnStateException("Cannot BET, need to CALL/RAISE/FOLD to: " + play);
        } else {
            final ChipValue amount = ChipValue.of(value);
            final Play play = Play.bet(player, amount);
            return stateTransition.apply(play);
        }
    }

    public BettingRound call() {
        final Optional<Play> lastIncreasingPlay = lastChipCountIncreasingPlay.get();
        if (lastIncreasingPlay.isEmpty()) {
            throw new BettingRound.InvalidPlayOnStateException("Cannot CALL, no BET to CALL/RAISE/FOLD on");
        } else {
            final Play bettingPlay = lastIncreasingPlay.get();
            final Play play = playUsedToCall(bettingPlay);
            return stateTransition.apply(play);
        }
    }

    public BettingRound raiseTo(long value) {
        final Optional<Play> lastIncreasingPlay = lastChipCountIncreasingPlay.get();
        if (lastIncreasingPlay.isEmpty()) {
            throw new BettingRound.InvalidPlayOnStateException("Cannot RAISE, no BET to CALL/RAISE/FOLD on");
        } else {
            final ChipValue target = ChipValue.of(value);
            final ChipValue previousBet = lastIncreasingPlay.get().totalChipValue();
            final ChipValue minRaise = ChipValue.minRaise(previousBet);
            if (target.isLessThan(minRaise)) {
                throw new BettingRound.InvalidPlayOnStateException("Cannot RAISE, BET is 42," +
                        " expected RAISE to 82 or higher," +
                        " got 60");
            }
            final Play play = playUsedToGetTo(target);
            return stateTransition.apply(play);
        }
    }

    public BettingRound allIn() {
        final Optional<Play> lastIncreasingPlay = lastChipCountIncreasingPlay.get();
        final ChipValue chipValue = remainingChipsProvider.apply(player);
        if (lastIncreasingPlay.isEmpty()) {
            return bet(chipValue.value());
        } else {
            return raiseTo(chipValue.value());
        }
    }

    private Play playUsedToCall(Play bettingPlay) {
        final ChipValue target = bettingPlay.totalChipValue();
        final ChipValue alreadyPlayed = chipsPutIntoPotByPlayer.get();
        final ChipValue amount = target.minus(alreadyPlayed);
        return Play.call(player, target, amount);
    }

    private Play playUsedToGetTo(ChipValue target) {
        final ChipValue alreadyPlayed = chipsPutIntoPotByPlayer.get();
        final ChipValue amount = target.minus(alreadyPlayed);
        return Play.raiseBy(player, target, amount);
    }
}
