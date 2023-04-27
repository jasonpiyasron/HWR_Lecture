package hwr.oop.poker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    void newBettingRound_IsNotFinished_ActionIsOnFirstPlayer() {
        // finished
        final boolean finished = round.isFinished();
        assertThat(finished).isFalse();

        // turn
        final Optional<Player> player = round.turn();
        assertThat(player).isPresent().get().isSameAs(firstPlayer);

        // last play
        final Optional<Play> play = round.lastPlay();
        assertThat(play).isEmpty();

        // pod size
        final ChipValue podSize = round.podSize();
        assertThat(podSize).isEqualTo(ChipValue.of(0));
    }

    @Test
    void firstPlayerBets10Chips_SecondPlayersTurn() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        // finished
        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();

        // turn
        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(secondPlayer);

        // last play
        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(firstPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.BET);

        // pod value
        final ChipValue podSize = updatedBettingRound.podSize();
        assertThat(podSize).isEqualTo(ChipValue.of(10));
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_ThirdPlayersTurn() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        // finished
        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();

        // turn
        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(thirdPlayer);

        // last play
        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(secondPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);

        // pod value
        final ChipValue podSize = updatedBettingRound.podSize();
        assertThat(podSize).isEqualTo(ChipValue.of(20));
    }

    @Test
    void thirdAndSecondPlayerCalled_RoundFinished() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        // finished
        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();

        // turn
        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isNotPresent();

        // last play
        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);

        // pod value
        final ChipValue podSize = updatedBettingRound.podSize();
        assertThat(podSize).isEqualTo(ChipValue.of(30));
    }
}
