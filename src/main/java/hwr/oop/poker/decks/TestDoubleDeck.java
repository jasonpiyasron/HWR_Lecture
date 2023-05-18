package hwr.oop.poker.decks;

import hwr.oop.poker.Card;
import hwr.oop.poker.Deck;

import java.util.ArrayList;
import java.util.List;

public class TestDoubleDeck implements Deck {
    private final List<Card> cards;

    public TestDoubleDeck(Card... cards) {
        this.cards = new ArrayList<>(List.of(cards));
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public Card top() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot peek at top Card if Deck is empty");
        }
        return cards.get(0);
    }

    @Override
    public void burn() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot burn card if Deck is empty");
        }
        cards.remove(0);
    }
}
