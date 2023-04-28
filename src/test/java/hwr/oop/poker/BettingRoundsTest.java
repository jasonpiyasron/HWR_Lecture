package hwr.oop.poker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class BettingRoundsTest {

    private Player firstPlayer;
    private Player secondPlayer;
    private Player thirdPlayer;
    private List<Player> players;
    private BettingRound round;

    @BeforeEach
    void setUp() {
        firstPlayer = new Player("1");
        secondPlayer = new Player("2");
        thirdPlayer = new Player("3");
        players = List.of(firstPlayer, secondPlayer, thirdPlayer);
        round = new BettingRound(players);
    }

    @Test
    void newBettingRound_IsNotFinished() {
        final boolean finished = round.isFinished();
        assertThat(finished).isFalse();
    }

    @Test
    void newBettingRound_PodIsEmpty() {
        final ChipValue potSize = round.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(0));
    }

    @Test
    void newBettingRound_LastPlayIsEmpty() {
        final Optional<Play> play = round.lastPlay();
        assertThat(play).isEmpty();
    }

    @Test
    void newBettingRound_ActionIsOnFirstPlayer() {
        final Optional<Player> player = round.turn();
        assertThat(player).isPresent().get().isSameAs(firstPlayer);
    }

    @Test
    void firstPlayerBets10Chips_RoundIsNotFinished() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();
    }

    @Test
    void firstPlayerBets10Chips_ActionOnSecondPlayer() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(secondPlayer);
    }

    @Test
    void firstPlayerBets10Chips_LastPlayIsBetOf10Chips() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(firstPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.BET);
    }

    @Test
    void firstPlayerBets10Chips_PodSize_10Chips() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(10));
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_RoundIsNotFinished() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_ActionIsOnThirdPlayer() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(thirdPlayer);
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_LastPlayIsCallOf10Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(secondPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_PodSizeOf20Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(20));
    }

    @Test
    void thirdAndSecondPlayerCalled_RoundIsFinished() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
    }

    @Test
    void thirdAndSecondPlayerCalled_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isNotPresent();
    }

    @Test
    void thirdAndSecondPlayerCalled_LastPlayIsCallOf10Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void thirdAndSecondPlayerCalled_PotSizeOf30Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(30));
    }

    @Test
    @Disabled("BET, FOLD, RAISE, CALL finishes round, not yet implemented")
    void betFoldRaiseCall() {
        fail("BET, FOLD, RAISE, CALL finishes round, not yet implemented");
    }


    @Test
    @Disabled("CHECK, CHECK, CHECK finishes round, not yet implemented")
    void checkCheckCheck() {
        fail("CHECK, CHECK, CHECK finishes round, not yet implemented");
    }

}
