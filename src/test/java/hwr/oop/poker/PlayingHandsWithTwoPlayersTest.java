package hwr.oop.poker;

import hwr.oop.poker.betting.BettingRound;
import hwr.oop.poker.blinds.BigBlind;
import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.CommunityCardsProvider;
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
        final Stacks stacks = Stacks.newBuilder()
                .of(firstPlayer).is(1000)
                .of(secondPlayer).is(1000)
                .build();
        hand = Hand.newBuilder()
                .deck(deck)
                .players(List.of(firstPlayer, secondPlayer))
                .smallBlind(SmallBlind.of(42))
                .stacks(stacks)
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
            BlindConfiguration blinds = hand.blindConfiguration();
            BigBlind bigBlind = blinds.bigBlind();
            long bigBlindValue = bigBlind.value();
            assertThat(bigBlindValue)
                    .isNotZero()
                    .isEqualTo(84);
        }

        @Test
        @DisplayName("Small Blind has the correct size, halve of the Big Blind")
        void smallBlind_IsExactlyHalfOfTheBigBlind() {
            final BlindConfiguration blinds = hand.blindConfiguration();
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
    @DisplayName("has Community Cards, more are dealt once rounds are played")
    class CommunityCardsTest {

        @Test
        @DisplayName("no action: no community cards (no flop, no turn, no river)")
        void noRoundPlayed_NoCommunityCards_FlopEmpty_TurnEmpty_RiverEmpty() {
            final Optional<Flop> flop = hand.flop();
            final Optional<Turn> turn = hand.turn();
            final Optional<River> river = hand.river();
            final Collection<Card> communityCards = hand.cardsDealt();
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
        @DisplayName("pre-flop played (all check): pre-flop marked as 'played'")
        void preFlopFinished_PreFlopPlayed() {
            final Hand updatedHand = hand
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isPreFlopPlayed = updatedHand.preFlopRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();
        }

        @Test
        @DisplayName("pre-flop played (all check): other rounds not marked as 'played'")
        void preFlopFinished_OtherRoundsNotPlayed() {
            final Hand updatedHand = hand
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isFlopRoundPlayed = updatedHand.flopRoundPlayed();
            final boolean isTurnPlayed = updatedHand.turnRoundPlayed();
            final boolean isRiverPlayed = updatedHand.riverRoundPlayed();
            assertThat(isFlopRoundPlayed).isFalse();
            assertThat(isTurnPlayed).isFalse();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("pre-flop played (all check): flop is dealt")
        void preFlopFinished_FlopDealt() {
            final CommunityCardsProvider afterPreFlop = hand
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<Flop> flop = afterPreFlop.flop();
            final Collection<Card> dealtCommunityCards = afterPreFlop.cardsDealt();

            assertThat(flop)
                    .isPresent().get()
                    .satisfies(fl -> assertContainsCards(fl, cardsOnFlop));
            assertThat(dealtCommunityCards).containsExactlyInAnyOrderElementsOf(cardsOnFlop);
        }

        @Test
        @DisplayName("pre-flop played (all check): turn and river not dealt")
        void preFlopFinished_TurnAndRiverNotDealt() {
            final CommunityCardsProvider afterPreFlop = hand
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<Turn> turn = afterPreFlop.turn();
            final Optional<River> river = afterPreFlop.river();
            final Collection<Card> dealtCommunityCards = afterPreFlop.cardsDealt();

            assertThat(turn).isNotPresent();
            assertThat(river).isNotPresent();
            assertThat(dealtCommunityCards)
                    .isNotEmpty()
                    .doesNotContainAnyElementsOf(cardsOnTurn)
                    .doesNotContainAnyElementsOf(cardsOnRiver);
        }

        @Test
        @DisplayName("pre-flop played (all check): hand not finished")
        void preFlopFinished_HandNotFinished() {
            final Hand handAfterPreFlop = hand
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isHandFinished = handAfterPreFlop.isFinished();
            assertThat(isHandFinished).isFalse();
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): both rounds marked as 'played'")
        void flopFinished_PreFlopAndFlopMarkedAsPlayed() {
            final Hand handAfterFlop = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isPreFlopPlayed = handAfterFlop.preFlopRoundPlayed();
            final boolean isFlopRoundPlayed = handAfterFlop.flopRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();
            assertThat(isFlopRoundPlayed).isTrue();
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): other rounds are not played")
        void flopFinished_TurnAndRiverNotMarked() {
            final Hand handAfterFlop = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isTurnPlayed = handAfterFlop.turnRoundPlayed();
            final boolean isRiverPlayed = handAfterFlop.riverRoundPlayed();
            assertThat(isTurnPlayed).isFalse();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): flop and turn dealt")
        void flopFinished_FlopAndTurnDealt() {
            final CommunityCardsProvider afterFlop = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<Flop> flop = afterFlop.flop();
            final Optional<Turn> turn = afterFlop.turn();
            final Collection<Card> communityCards = afterFlop.cardsDealt();

            assertThat(flop)
                    .isPresent().get()
                    .satisfies(f -> assertContainsCards(f, cardsOnFlop));
            assertThat(turn)
                    .isPresent().get()
                    .satisfies(t -> assertContainsCards(t, cardsOnTurn));
            assertThat(communityCards)
                    .containsAll(cardsOnFlop)
                    .containsAll(cardsOnTurn);
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): river not dealt")
        void flopFinished_RiverNotDealt() {
            final CommunityCardsProvider afterFlop = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<River> river = afterFlop.river();
            final Collection<Card> communityCards = afterFlop.cardsDealt();

            assertThat(river).isNotPresent();
            assertThat(communityCards)
                    .isNotEmpty()
                    .doesNotContainAnyElementsOf(cardsOnRiver);
        }

        @Test
        @DisplayName("flop played (pre-flop & flop: all checks): hand not finished")
        void flopFinished_HandNotFinished() {
            final Hand handAfterFlop = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isHandFinished = handAfterFlop.isFinished();
            assertThat(isHandFinished).isFalse();
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): pre-flop, flop and turn marked as 'played'")
        void turnFinished_PreFlopAndFlopAndTurnMarkedAsPlayed() {
            final Hand handAfterTurn = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isPreFlopPlayed = handAfterTurn.preFlopRoundPlayed();
            final boolean isFlopRoundPlayed = handAfterTurn.flopRoundPlayed();
            final boolean isTurnPlayed = handAfterTurn.turnRoundPlayed();
            assertThat(isPreFlopPlayed).isTrue();
            assertThat(isFlopRoundPlayed).isTrue();
            assertThat(isTurnPlayed).isTrue();
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): river round not played")
        void turnFinished_RiverMarkedAsNotPlayed() {
            final Hand handAfterTurn = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isRiverPlayed = handAfterTurn.riverRoundPlayed();
            assertThat(isRiverPlayed).isFalse();
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): flop, turn, river dealt")
        void turnFinished_PreFlopAndFlopAndTurnAndRiverDealt() {
            final CommunityCardsProvider afterTurn = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<Flop> flop = afterTurn.flop();
            final Optional<Turn> turn = afterTurn.turn();
            final Optional<River> river = afterTurn.river();
            final Collection<Card> communityCards = afterTurn.cardsDealt();

            assertThat(flop)
                    .isPresent().get()
                    .satisfies(t -> assertContainsCards(t, cardsOnFlop));
            assertThat(turn)
                    .isPresent().get()
                    .satisfies(t -> assertContainsCards(t, cardsOnTurn));
            assertThat(river)
                    .isPresent().get()
                    .satisfies(r -> assertContainsCards(r, cardsOnRiver));
            assertThat(communityCards)
                    .containsAll(cardsOnFlop)
                    .containsAll(cardsOnTurn)
                    .containsAll(cardsOnRiver);
        }

        @Test
        @DisplayName("turn played (pre-flop, turn & flop: all checks): hand not finished")
        void turnFinished_HandNotFinished() {
            final Hand handAfterTurn = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isHandFinished = handAfterTurn.isFinished();
            assertThat(isHandFinished).isFalse();
        }

        @Test
        @DisplayName("river played (all rounds: all checks): river round is marked as 'played'")
        void riverFinished_RiverMarkedAsPlayed() {
            final Hand handAfterRiver = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final boolean isRiverPlayed = handAfterRiver.riverRoundPlayed();
            assertThat(isRiverPlayed).isTrue();
        }

        @Test
        @DisplayName("river played (all rounds: all checks): flop, turn, river dealt")
        void riverFinished_PreFlopAndFlopAndTurnAndRiverDealt() {
            final CommunityCardsProvider afterRiver = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            final Optional<Flop> flop = afterRiver.flop();
            final Optional<Turn> turn = afterRiver.turn();
            final Optional<River> river = afterRiver.river();
            final Collection<Card> communityCards = afterRiver.cardsDealt();

            assertThat(flop)
                    .isPresent().get()
                    .satisfies(f -> assertContainsCards(f, cardsOnFlop));
            assertThat(turn)
                    .isPresent().get()
                    .satisfies(t -> assertContainsCards(t, cardsOnTurn));
            assertThat(river)
                    .isPresent().get()
                    .satisfies(r -> assertContainsCards(r, cardsOnRiver));
            assertThat(communityCards)
                    .containsAll(cardsOnFlop)
                    .containsAll(cardsOnTurn)
                    .containsAll(cardsOnRiver);
        }

        @Test
        @DisplayName("river played (all rounds: all checks): hand finished")
        void riverFinished_HandIsFinished() {
            final Hand handAfterRiver = hand
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking)
                    .onCurrentRound(this::bothPlayersChecking);
            // then
            boolean isHandFinished = handAfterRiver.isFinished();
            assertThat(isHandFinished).isTrue();
        }

        private void assertContainsCards(Card.Provider cardProvider, Collection<Card> expected) {
            assertThat(cardProvider.cards())
                    .isNotEmpty()
                    .allMatch(expected::contains);
        }

        private BettingRound bothPlayersChecking(BettingRound round) {
            return round.with(firstPlayer).check().with(secondPlayer).check();
        }
    }

    @Nested  // TODO Write displayname!
    class ShowDownTest {
        private Hand finishedHand;

        @BeforeEach
        void setUp() {
            final Hand finishedHand = hand
                    .onCurrentRound(this::bothPlayersCheck)
                    .onCurrentRound(this::bothPlayersCheck)
                    .onCurrentRound(this::bothPlayersCheck)
                    .onCurrentRound(this::bothPlayersCheck);
            assert finishedHand.isFinished();
            this.finishedHand = finishedHand;
        }

        @Test
        void showDown_BothPlayers_HaveFullHouses() {
            final Optional<ShowDown> optional = finishedHand.showDown();
            assertThat(optional).isPresent().get().satisfies(showDown -> {
                final Combination comboFirstPlayer = showDown.combination(firstPlayer);
                final Combination.Label firstPlayerLabel = comboFirstPlayer.label();
                assertThat(firstPlayerLabel).isEqualTo(Combination.Label.FULL_HOUSE);
                final Combination comboSecondPlayer = showDown.combination(secondPlayer);
                final Combination.Label secondPlayerLabel = comboSecondPlayer.label();
                assertThat(secondPlayerLabel).isEqualTo(Combination.Label.FULL_HOUSE);
            });
        }

        @Test
        void showDown_Winner_FirstPlayer() {
            final Optional<ShowDown> optional = finishedHand.showDown();
            assertThat(optional).isPresent().get().satisfies(showDown -> {
                final Player player = showDown.winner();
                assertThat(player).isEqualTo(firstPlayer);
            });
        }

        private BettingRound bothPlayersCheck(BettingRound r) {
            return r.with(firstPlayer).check().with(secondPlayer).check();
        }
    }

    @Test
    @DisplayName("#potSize, is equal to sum of Small Blind and Big Blind")
    void potSize_IsEqualToSumOfSmallBlindAndBigBlind() {
        // given
        final BlindConfiguration config = hand.blindConfiguration();
        final long sbValue = config.smallBlind().value();
        final long bbValue = config.bigBlind().value();
        // when
        final long potSize = hand.potSize().value();
        // then
        assertThat(potSize).isEqualTo(sbValue + bbValue);
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
