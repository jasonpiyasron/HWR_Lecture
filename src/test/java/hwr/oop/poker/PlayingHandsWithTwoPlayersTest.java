package hwr.oop.poker;

import hwr.oop.poker.betting.BettingRound;
import hwr.oop.poker.blinds.BigBlind;
import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.Flop;
import hwr.oop.poker.community.cards.River;
import hwr.oop.poker.community.cards.Turn;
import hwr.oop.poker.decks.TestDoubleDeck;
import hwr.oop.poker.testing.Converter;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Scripted Hand, two Players, Tens full vs. Aces full")
class PlayingHandsWithTwoPlayersTest {

    private Player firstPlayer;
    private Player secondPlayer;
    private Hand hand;
    private List<Card> cardsOnFlop;
    private List<Card> cardsOnTurn;
    private List<Card> cardsOnRiver;

    @BeforeEach
    void setUp() {
        Converter converter = Converter.create();
        cardsOnFlop = List.of(
                converter.from("TC"),
                converter.from("TH"),
                converter.from("2H")
        );
        cardsOnTurn = List.of(
                converter.from("KS")
        );
        cardsOnRiver = List.of(
                converter.from("AS")
        );
        final Deck deck = new TestDoubleDeck(
                new Card(Color.HEARTS, Symbol.ACE),  // p1, c1
                new Card(Color.SPADES, Symbol.TEN),
                new Card(Color.CLUBS, Symbol.ACE),  // p1, c2
                new Card(Color.SPADES, Symbol.TWO),
                new Card(Color.CLUBS, Symbol.THREE),  // burned, flop following
                cardsOnFlop.get(0),
                cardsOnFlop.get(1),
                cardsOnFlop.get(2),
                new Card(Color.SPADES, Symbol.THREE),  // burned, turn following
                cardsOnTurn.get(0),
                new Card(Color.DIAMONDS, Symbol.THREE),  // burned, river following
                cardsOnRiver.get(0)
        );
        firstPlayer = new Player("1");
        secondPlayer = new Player("2");
        hand = Hand.newBuilder()
                .deck(deck)
                .players(List.of(firstPlayer, secondPlayer))
                .smallBlind(SmallBlind.of(42))
                .build();
    }

    @Test
    @DisplayName("#holeCards for Player 1, has bullets (aces) of hearts and clubs")
    void firstPlayerHoleCards_Bullets_HeartsAndClubs() {
        List<Card> firstPlayerHoleCards = hand.holeCards(firstPlayer);
        assertThat(firstPlayerHoleCards).containsExactlyInAnyOrder(
                new Card(Color.HEARTS, Symbol.ACE),
                new Card(Color.CLUBS, Symbol.ACE)
        );
    }

    @Test
    @DisplayName("#holeCards for Player 2, has 'Brunson' (ten-deuce) of spades")
    void secondPlayerHoleCards_TenDeuceOfSpades() {
        List<Card> secondPlayerHoleCards = hand.holeCards(secondPlayer);
        assertThat(secondPlayerHoleCards).containsExactlyInAnyOrder(
                new Card(Color.SPADES, Symbol.TEN),
                new Card(Color.SPADES, Symbol.TWO)
        );
    }

    @Test
    @DisplayName("First Player has to pay the Small Blind")
    void smallBlind_IsFirstPlayer() {
        final Player smallBlind = hand.smallBlind();
        assertThat(smallBlind).isSameAs(firstPlayer);
    }

    @Test
    @DisplayName("Second Player has to pay the Big Blind")
    void bigBlind_IsSecondPlayer() {
        final Player bigBlind = hand.bigBlind();
        assertThat(bigBlind).isSameAs(secondPlayer);
    }

    @Test
    @DisplayName("Second Player sits on the Button")
    void buttonOrDealer_IsSecondPlayer() {
        final Player button = hand.button();
        assertThat(button).isSameAs(secondPlayer);
    }

    @Test
    @DisplayName("First Player is 'under the gun'")
    void underTheGun_IsFirstPlayer() {
        final Player underTheGun = hand.underTheGun();
        assertThat(underTheGun).isSameAs(firstPlayer);
    }

    @Test
    @Disabled("Messages asking whether players have position on each other is not yet implemented")
    void positionalRelationsBetweenPlayers() {
        Assertions.fail("Not yet implemented");
    }

    @Nested
    @DisplayName("has blinds: Small blind is 42, Big Blind is 84")
    class BlindTest {
        @Test
        @DisplayName("Big Blind has the correct size (84)")
        void bigBlind_IsEqualToTwo() {
            BlindConfiguration blinds = hand.blinds();
            BigBlind bigBlind = blinds.bigBlind();
            long bigBlindValue = bigBlind.value();
            assertThat(bigBlindValue)
                    .isNotZero()
                    .isEqualTo(84);
        }

        @Test
        @DisplayName("Small Blind has the correct size, halve of the Big Blind")
        void smallBlind_IsExactlyHalfOfTheBigBlind() {
            final BlindConfiguration blinds = hand.blinds();
            final long bigBlindValue = blinds.bigBlind().value();
            final double expectedSmallBlind = 1.0 * bigBlindValue / 2;
            final long actualSmallBlindValue = blinds.smallBlind().value();
            assertThat(expectedSmallBlind)
                    .isNotZero()
                    .isEqualTo(actualSmallBlindValue);
        }

        @Test
        @Disabled("Antes (mandatory costs for all players not paying blinds) are not yet supported")
        void antes() {
            Assertions.fail("Not yet implemented");
        }
    }

    @Nested
    @DisplayName("has Community Cards, however, none are dealt yet")
    class CommunityCardsTest {

        @Test
        @DisplayName("no action: no community cards (no flop, no turn, no river)")
        void noRoundPlayed_NoCommunityCards_FlopEmpty_TurnEmpty_RiverEmpty() {
            final Optional<Flop> flop = hand.flop();
            final Optional<Turn> turn = hand.turn();
            final Optional<River> river = hand.river();
            final Collection<Card> communityCards = hand.communityCards().cardsDealt();
            assertThat(flop).isNotPresent();
            assertThat(turn).isNotPresent();
            assertThat(river).isNotPresent();
            assertThat(communityCards).isEmpty();
        }

        @Test
        @DisplayName("no action: all four betting rounds are not played")
        void noRoundPlayed_AllFourBettingRoundsAreNotPlayed() {
            final boolean preFlopRoundPlayed = hand.preFlopRoundPlayed();
            final boolean flopRoundPlayed = hand.flopRoundPlayed();
            final boolean turnRoundPlayed = hand.turnRoundPlayed();
            final boolean riverRoundPlayed = hand.riverRoundPlayed();
            assertThat(preFlopRoundPlayed).isFalse();
            assertThat(flopRoundPlayed).isFalse();
            assertThat(turnRoundPlayed).isFalse();
            assertThat(riverRoundPlayed).isFalse();
        }

        @Test
        @DisplayName("pre-flop played (all check): pre-flop marked as 'played', other rounds are not played")
        void preFlopFinished_PreFlopPlayed_IsTrue() {
            // given
            final BettingRound preFlop = hand.currentRound();
            final BettingRound preFlopPlayed = preFlop
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand updatedHand = hand.accept(preFlopPlayed);
            // then
            final boolean isPreFlopPlayed = updatedHand.preFlopRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();

            final boolean isFlopRoundPlayed = updatedHand.flopRoundPlayed();
            final boolean isTurnPlayed = updatedHand.turnRoundPlayed();
            final boolean isRiverPlayed = updatedHand.riverRoundPlayed();
            assertThat(isFlopRoundPlayed).isFalse();
            assertThat(isTurnPlayed).isFalse();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("pre-flop played (all check): flop is dealt, turn and river not dealt")
        void preFlopFinished_FlopDealt_TurnEmpty_RiverEmpty_CommunityCardsConsistOfOnlyTheFlop() {
            // given
            final BettingRound preFlop = hand.currentRound();
            final BettingRound preFlopPlayed = preFlop
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand updatedHand = hand.accept(preFlopPlayed);
            // then
            final Optional<Flop> flop = updatedHand.flop();
            assertThat(flop)
                    .isPresent().get()
                    .matches(fl -> fl.cards().containsAll(cardsOnFlop));

            final Optional<Turn> turn = updatedHand.turn();
            final Optional<River> river = updatedHand.river();
            assertThat(turn).isNotPresent();
            assertThat(river).isNotPresent();

            final Collection<Card> dealtCommunityCards =
                    updatedHand.communityCards().cardsDealt();
            assertThat(dealtCommunityCards).containsExactlyInAnyOrderElementsOf(cardsOnFlop);
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): pre-flop and flop marked as 'played', other rounds are not played")
        void flopFinished_FlopAndPreFlopMarkedAsPlayed_OtherRoundsNotMarked() {
            // given
            final BettingRound preFlopPlayed = hand.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterPreFlop = hand.accept(preFlopPlayed);
            final BettingRound flopPlayed = handAfterPreFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand handAfterFlop = handAfterPreFlop.accept(flopPlayed);
            // then
            final boolean isPreFlopPlayed = handAfterFlop.preFlopRoundPlayed();
            final boolean isFlopRoundPlayed = handAfterFlop.flopRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();
            assertThat(isFlopRoundPlayed).isTrue();

            final boolean isTurnPlayed = handAfterFlop.turnRoundPlayed();
            final boolean isRiverPlayed = handAfterFlop.riverRoundPlayed();
            assertThat(isTurnPlayed).isFalse();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): flop and turn dealt, river not dealt")
        void flopFinished_FlopAndTurnDealt_RiverNotDealt() {
            // given
            final BettingRound preFlopPlayed = hand.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterPreFlop = hand.accept(preFlopPlayed);
            final BettingRound flopPlayed = handAfterPreFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand handAfterFlop = handAfterPreFlop.accept(flopPlayed);
            // then
            final Optional<Flop> flop = handAfterFlop.flop();
            final Optional<Turn> turn = handAfterFlop.turn();
            assertThat(flop)
                    .isPresent().get()
                    .matches(f -> f.cards().containsAll(cardsOnFlop));
            assertThat(turn)
                    .isPresent().get()
                    .matches(t -> cardsOnTurn.contains(t.card()));

            final Optional<River> river = handAfterFlop.river();
            assertThat(river).isNotPresent();

            final Collection<Card> communityCards =
                    handAfterFlop.communityCards().cardsDealt();
            assertThat(communityCards)
                    .containsAll(cardsOnFlop)
                    .containsAll(cardsOnTurn)
                    .doesNotContainAnyElementsOf(cardsOnRiver);
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): pre-flop, flop and turn marked as 'played', river round not played")
        void turnFinished_PreFlopAndFlopAndTurnMarkedAsPlayed_RiverMarkedAsNotPlayed() {
            // given
            final BettingRound preFlopPlayed = hand.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterPreFlop = hand.accept(preFlopPlayed);
            final BettingRound flopPlayed = handAfterPreFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterFlop = handAfterPreFlop.accept(flopPlayed);
            final BettingRound turnPlayed = handAfterFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand handAfterTurn = handAfterFlop.accept(turnPlayed);
            // then
            final boolean isPreFlopPlayed = handAfterTurn.preFlopRoundPlayed();
            final boolean isFlopRoundPlayed = handAfterTurn.flopRoundPlayed();
            final boolean isTurnPlayed = handAfterTurn.turnRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();
            assertThat(isFlopRoundPlayed).isTrue();
            assertThat(isTurnPlayed).isTrue();

            final boolean isRiverPlayed = handAfterTurn.riverRoundPlayed();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): flop, turn, river dealt")
        void turnFinished_PreFlopAndFlopAndTurnAndRiverDealt() {
            // given
            final BettingRound preFlopPlayed = hand.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterPreFlop = hand.accept(preFlopPlayed);
            final BettingRound flopPlayed = handAfterPreFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            final Hand handAfterFlop = handAfterPreFlop.accept(flopPlayed);
            final BettingRound turnPlayed = handAfterFlop.currentRound()
                    .with(firstPlayer).check()
                    .with(secondPlayer).check();
            // when
            final Hand handAfterTurn = handAfterFlop.accept(turnPlayed);
            // then
            final Optional<Flop> flop = handAfterTurn.flop();
            final Optional<Turn> turn = handAfterTurn.turn();
            final Optional<River> river = handAfterTurn.river();
            assertThat(flop)
                    .isPresent().get()
                    .matches(f -> f.cards().containsAll(cardsOnFlop));
            assertThat(turn)
                    .isPresent().get()
                    .matches(t -> cardsOnTurn.contains(t.card()));
            assertThat(river)
                    .isPresent().get()
                    .matches(r -> cardsOnRiver.contains(r.card()));

            final Collection<Card> communityCards =
                    handAfterTurn.communityCards().cardsDealt();
            assertThat(communityCards)
                    .containsAll(cardsOnFlop)
                    .containsAll(cardsOnTurn)
                    .containsAll(cardsOnRiver);
        }

        @Test
        @Disabled("Flop finished: Turn becomes visible, not yet implemented")
        void flopFinished_TurnNoLongerEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Flop finished: River is still hidden, not yet implemented")
        void flopFinished_RiverIsStillEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Flop finished: Two more betting rounds, not yet implemented")
        void flopFinished_TwoMoreBettingRound() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Turn finished: River becomes visible, not yet implemented")
        void turnFinished_RiverNoLongerEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Turn finished: One more betting rounds, not yet implemented")
        void turnFinished_OneMoreBettingRound() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("River finished: All cards visible, not yet implemented")
        void turnFinished_AllCommunityCardsVisible() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("River finished: No more betting rounds, not yet implemented")
        void riverFinished_NoMoreBettingRoundsRequired() {
            Assertions.fail("Not yet implemented");
        }
    }

    @Test
    @DisplayName("#podSize, is equal to sum of Small Blind and Big Blind")
    void podSize_IsEqualToSumOfSmallBlindAndBigBlind() {
        // given
        final BlindConfiguration config = hand.blinds();
        final long sbValue = config.smallBlind().value();
        final long bbValue = config.bigBlind().value();
        // when
        final long podSize = hand.podSize().value();
        // then
        assertThat(podSize).isEqualTo(sbValue + bbValue);
    }

    @Test
    @Disabled("Stacks for individual players are not yet implemented")
    void stacksForPlayers() {
        Assertions.fail("Not yet implemented");
    }

    @Test
    @Disabled("Message asking for the current player on action is not yet implemented")
    void turn_IsOnUnderTheGun() {
        Assertions.fail("Not yet implemented");
    }

}
