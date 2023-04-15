package hwr.oop.poker;

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
    public Card topCard() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot retrieve #topCard if Deck #isEmpty");
        }
        return cards.get(0);
    }

    @Override
    public void popFirstCard() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot #popFirstCard if Deck #isEmpty");
        }
        cards.remove(0);
    }
}
