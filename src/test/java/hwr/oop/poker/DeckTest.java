package hwr.oop.poker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Provide a Deck that Cards can be drawn from")
class DeckTest {

    @Test
    @DisplayName("new deck, contains 52 cards")
    void newDeck_ContainsEachCardExactlyOnce() {
        final Deck deck = new RandomDeck();
        final List<Card> cards = deck.drawAllCards();
        assertThat(cards).allMatch(c -> {
            final long numberOfEqualCards = cards.stream().filter(c::equals).count();
            return numberOfEqualCards == 1;
        });
    }

    @Test
    @DisplayName("new deck, contains each card exactly once")
    void newDeck_52Cards() {
        final Deck deck = new RandomDeck();
        final List<Card> cards = deck.drawAllCards();
        assertThat(cards).size().isEqualTo(52);
    }
}
