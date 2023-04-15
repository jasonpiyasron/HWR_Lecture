package hwr.oop.poker;

import java.util.ArrayList;
import java.util.List;

public interface Deck {
    boolean isEmpty();

    Card topCard();

    void popFirstCard();

    default Card draw() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot #draw if Deck #isEmpty");
        }
        final Card card = topCard();
        popFirstCard();
        return card;
    }

    default List<Card> drawAllCards() {
        List<Card> cards = new ArrayList<>();
        while (!isEmpty()) {
            Card card = draw();
            cards.add(card);
        }
        return cards;
    }

    class DrawFromEmptyDeckException extends RuntimeException {
        public DrawFromEmptyDeckException(String message) {
            super(message);
        }
    }
}
