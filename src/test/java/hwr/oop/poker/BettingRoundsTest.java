package hwr.oop.poker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        boolean finished = round.isFinished();
        Player player = round.turn();
        assertThat(finished).isFalse();
        assertThat(player).isSameAs(firstPlayer);
    }

    @Test
    void firstPlayerBets10Chips_SecondPlayersTurn() {
        // when
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);
        // then
        assertThat(updatedBettingRound.isFinished()).isFalse();
        assertThat(updatedBettingRound.turn()).isSameAs(secondPlayer);
    }

    @Test
    void asdf() {
        // when
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();
        // then
        assertThat(updatedBettingRound.isFinished()).isFalse();
        assertThat(updatedBettingRound.turn()).isSameAs(thirdPlayer);
    }
}
