package hwr.oop.poker.decks;

import hwr.oop.poker.Card;
import hwr.oop.poker.Color;
import hwr.oop.poker.Deck;
import hwr.oop.poker.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomDeck implements Deck {

    private final List<Card> cards;

    public RandomDeck() {
        this.cards = new ArrayList<>();
        for (Color color : Color.values()) {
            for (Symbol symbol : Symbol.values()) {
                final Card card = new Card(color, symbol);
                this.cards.add(card);
            }
        }
        Collections.shuffle(cards);
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public Card topCard() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot peek at top Card if Deck #isEmpty");
        }
        return cards.get(0);
    }

    @Override
    public void popFirstCard() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot remove top card if Deck #isEmpty");
        }
        cards.remove(0);
    }
}
