package hwr.oop.poker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        hand = new Hand(deck, List.of(firstPlayer, secondPlayer));
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
}
