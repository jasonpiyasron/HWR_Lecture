package hwr.oop.poker;

import hwr.oop.poker.blinds.BigBlind;
import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.CommunityCards;
import hwr.oop.poker.community.cards.Flop;
import hwr.oop.poker.community.cards.River;
import hwr.oop.poker.community.cards.Turn;
import hwr.oop.poker.decks.TestDoubleDeck;
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

    @BeforeEach
    void setUp() {
        final Deck deck = new TestDoubleDeck(
                new Card(Color.HEARTS, Symbol.ACE),  // p1, c1
                new Card(Color.SPADES, Symbol.TEN),
                new Card(Color.CLUBS, Symbol.ACE),  // p1, c2
                new Card(Color.SPADES, Symbol.TWO),
                new Card(Color.CLUBS, Symbol.THREE),  // burned, flop following
                new Card(Color.CLUBS, Symbol.TEN),
                new Card(Color.HEARTS, Symbol.TEN),
                new Card(Color.HEARTS, Symbol.TWO),
                new Card(Color.SPADES, Symbol.THREE),  // burned, turn following
                new Card(Color.SPADES, Symbol.KING),
                new Card(Color.DIAMONDS, Symbol.THREE),  // burned, river following
                new Card(Color.SPADES, Symbol.ACE)
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
        @DisplayName("Flop has not been dealt, is not present")
        void flopIsEmptyBecauseItWasNotDealt() {
            Optional<Flop> flop = hand.flop();
            assertThat(flop).isNotPresent();
        }

        @Test
        @DisplayName("Turn has not been dealt, is not present")
        void turnIsEmptyBecauseItWasNotDealt() {
            Optional<Turn> turn = hand.turn();
            assertThat(turn).isNotPresent();
        }

        @Test
        @DisplayName("River has not been dealt, is not present")
        void riverIsEmptyBecauseItWasNotDealt() {
            Optional<River> river = hand.river();
            assertThat(river).isNotPresent();
        }

        @Test
        @DisplayName("no community cards dealt, response is empty collection")
        void communityCardsAreEmptyBecauseItWasNotDealtYet() {
            CommunityCards communityCards = hand.communityCards();
            Collection<Card> cards = communityCards.cardsDealt();
            assertThat(cards).isEmpty();
        }

        @Test
        @Disabled("before start: four betting rounds missing, not yet implemented")
        void beforeStart_FourMoreBettingRounds() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Pre Flop finished: Flop becomes visible, not yet implemented")
        void preFlopFinished_FlopNoLongerEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Pre Flop finished: Turn is still hidden, not yet implemented")
        void preFlopFinished_TurnIsStillEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Pre Flop finished: River is still hidden, not yet implemented")
        void preFlopFinished_RiverIsStillEmpty() {
            Assertions.fail("Not yet implemented");
        }

        @Test
        @Disabled("Pre Flop finished: Three more betting rounds, not yet implemented")
        void preFlopFinished_ThreeMoreBettingRound() {
            Assertions.fail("Not yet implemented");
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
