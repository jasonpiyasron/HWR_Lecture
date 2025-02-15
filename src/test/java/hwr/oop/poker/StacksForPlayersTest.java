package hwr.oop.poker;

import hwr.oop.poker.betting.BettingRound;
import hwr.oop.poker.betting.Play;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.decks.RandomDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

class StacksForPlayersTest {

    private Player firstPlayer;
    private Player secondPlayer;

    @BeforeEach
    void setUp() {
        this.firstPlayer = new Player("1");
        this.secondPlayer = new Player("2");
    }

    @Nested
    class OnBettingRoundIsolatedTest {
        private BettingRound round;

        @BeforeEach
        void setUp() {
            final Stacks stacks = Stacks.newBuilder()
                    .of(firstPlayer).is(100)
                    .of(secondPlayer).is(100)
                    .build();
            this.round = BettingRound.create(stacks, firstPlayer, secondPlayer);
        }

        @Test
        void name() {
            ChipValue initialChipCount = round.remainingChips(firstPlayer);
            assertThat(initialChipCount).isEqualTo(ChipValue.of(100));
        }

        @Test
        void bet10OnRound_RemainingChipsIs90() {
            // when
            final BettingRound afterBetRound = round.with(firstPlayer).bet(10);
            // then
            ChipValue remainingChips = afterBetRound.remainingChips(firstPlayer);
            assertThat(remainingChips).isEqualTo(ChipValue.of(90));
        }

        @Test
        void bet10RaiseTo30Call_RemainingChipsForEveryOneAre70() {
            // when
            final BettingRound roundAfterCall = round
                    .with(firstPlayer).bet(10)
                    .with(secondPlayer).raiseTo(30)
                    .with(firstPlayer).call();
            // then
            final var remainingChipsFirstPlayer = roundAfterCall.remainingChips(firstPlayer);
            final var remainingChipsSecondPlayer = roundAfterCall.remainingChips(secondPlayer);
            assertThat(remainingChipsFirstPlayer).isEqualTo(ChipValue.of(70));
            assertThat(remainingChipsSecondPlayer).isEqualTo(ChipValue.of(70));
        }

        @Test
        void bet20RaiseTo80_FirstPlayer_Has80ChipsLeft() {
            // when
            final BettingRound roundAfterRaise = round
                    .with(firstPlayer).bet(20)
                    .with(secondPlayer).raiseTo(80);
            // then
            final var remainingChipsFirstPlayer = roundAfterRaise.remainingChips(firstPlayer);
            assertThat(remainingChipsFirstPlayer).isEqualTo(ChipValue.of(80));
        }

        @Test
        void bet20RaiseTo80_SecondPlayer_Has20ChipsLeft() {
            // when
            final BettingRound roundAfterRaise = round
                    .with(firstPlayer).bet(20)
                    .with(secondPlayer).raiseTo(80);
            // then
            final var remainingChipsSecondPlayer = roundAfterRaise.remainingChips(secondPlayer);
            assertThat(remainingChipsSecondPlayer).isEqualTo(ChipValue.of(20));
        }

        @Test
        void betAllIn_FirstPlayer_HasNoChipsLeft() {
            // when
            final BettingRound roundAfterBet = round
                    .with(firstPlayer).allIn();
            // then
            final var remainingChips = roundAfterBet.remainingChips(firstPlayer);
            assertThat(remainingChips).isEqualTo(ChipValue.zero());
        }

        @Test
        void betRaiseAllIn_SecondPlayer_HasNoChipsLeft() {
            // when
            final BettingRound roundAfterBet = round
                    .with(firstPlayer).bet(20)
                    .with(secondPlayer).allIn();
            // then
            final var remainingChips = roundAfterBet.remainingChips(secondPlayer);
            assertThat(remainingChips).isEqualTo(ChipValue.zero());
        }

        @Test
        void bet20_RaiseAllIn_FirstPlayer_Has80ChipsLeft() {
            // when
            final BettingRound roundAfterBet = round
                    .with(firstPlayer).bet(20)
                    .with(secondPlayer).allIn();
            // then
            final var remainingChips = roundAfterBet.remainingChips(firstPlayer);
            assertThat(remainingChips).isEqualTo(ChipValue.of(80));
        }


        @Test
        @Disabled("Player 1 has more than Player 2, all in + call," +
                " Player 1 has 0 chips left, Player 2 has some chips left not yet implemented")
        void player1LessChipsThanPlayer2_AllInCalled_Player1HasNoChipsLeftWhilePlayer2StillHasSome() {
            fail("Player 1 has more than Player 2, all in + call," +
                    " Player 1 has 0 chips left, Player 2 has some chips left not yet implemented");
        }

        @Test
        @Disabled("Player 1 has more than Player 2, bet + all in + call," +
                " both players have 0 chips left not yet implemented")
        void player1LassChipsThanPlayer2_betAllInCalled_BothPlayersHaveNoChipsLeft() {
            fail("Player 1 has more than Player 2, bet + all in + call," +
                    " both players have 0 chips left not yet implemented");
        }

    }

    @Nested
    class IsolatedStackTest {

        @Test
        void allPlayers100Chips_Player1Bets10Chips_Player1OnlyHas90Left() {
            final Stacks stacks = Stacks.newBuilder()
                    .of(firstPlayer).is(100)
                    .of(secondPlayer).is(100)
                    .build();
            final Play play = Play.bet(firstPlayer, ChipValue.of(10));
            final Stacks updatedStacks = stacks.apply(play);

            final ChipValue firstPlayerStack = updatedStacks.ofPlayer(firstPlayer);
            assertThat(firstPlayerStack).isEqualTo(ChipValue.of(90));
        }

        @Test
        void allPlayers100Chips_Player1Bets10Chips_Player2StillHas100Left() {
            final Stacks stacks = Stacks.newBuilder()
                    .of(firstPlayer).is(100)
                    .of(secondPlayer).is(100)
                    .build();
            final Play play = Play.bet(firstPlayer, ChipValue.of(10));
            final Stacks updatedStacks = stacks.apply(play);

            final ChipValue secondPlayerStack = updatedStacks.ofPlayer(secondPlayer);
            assertThat(secondPlayerStack).isEqualTo(ChipValue.of(100));
        }

        @Test
        void allPlayersHave100Chips_FirstPlayerBets101Chips_ThrowsException() {
            final Stacks stacks = Stacks.newBuilder()
                    .of(firstPlayer).is(100)
                    .of(secondPlayer).is(100)
                    .build();
            final var betSize = ChipValue.of(101);
            final var play = Play.bet(firstPlayer, betSize);
            assertThatThrownBy(() -> stacks.apply(play))
                    .isInstanceOf(Stacks.InvalidPlayForStackException.class)
                    .hasMessageContainingAll(
                            "only", ChipValue.of(100).toString(),
                            "tried", betSize.toString()
                    );
        }
    }

    @Nested
    class OnHandTest {
        private Hand hand;

        @BeforeEach
        void setUp() {
            final Deck deck = new RandomDeck();
            final Stacks stacks = Stacks.newBuilder()
                    .of(firstPlayer).is(100)
                    .of(secondPlayer).is(200)
                    .build();
            final List<Player> players = List.of(firstPlayer, secondPlayer);
            final SmallBlind smallBlind = SmallBlind.of(1);
            this.hand = Hand.newBuilder()
                    .deck(deck)
                    .players(players)
                    .stacks(stacks)
                    .smallBlind(smallBlind)
                    .build();
        }

        @Test
        void noPlay_FirstPlayerStillHas100Chips() {
            final var stacks = hand.stacks();
            final var firstPlayerStack = stacks.ofPlayer(firstPlayer);
            assertThat(firstPlayerStack).isEqualTo(ChipValue.of(100));
        }

        @Test
        void noPlay_SecondPlayerStillHas200Chips() {
            final var stacks = hand.stacks();
            final var secondPlayerStack = stacks.ofPlayer(secondPlayer);
            assertThat(secondPlayerStack).isEqualTo(ChipValue.of(200));
        }

        @Test
        void bet17Call_FirstPlayer_Has83ChipsLeft() {
            final var updated = hand.onCurrentRound(r -> r.with(firstPlayer).bet(17).with(secondPlayer).call());
            final var stacks = updated.stacks();
            final ChipValue first = stacks.ofPlayer(firstPlayer);
            assertThat(first).isEqualTo(ChipValue.of(83));
        }

        @Test
        void bet17Call_SecondPlayer_Has183ChipsLeft() {
            final var updated = hand.onCurrentRound(r -> r.with(firstPlayer).bet(17).with(secondPlayer).call());
            final var stacks = updated.stacks();
            final ChipValue second = stacks.ofPlayer(secondPlayer);
            assertThat(second).isEqualTo(ChipValue.of(183));
        }
    }
}
