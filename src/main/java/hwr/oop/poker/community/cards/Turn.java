package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

public class Turn {
    static Turn of(Card card) {
        return new Turn(card);
    }

    private final Card card;

    public Turn(Card card) {
        this.card = card;
    }

    public Card card() {
        return card;
    }

    @Override
    public String toString() {
        return "Turn{" + card + '}';
    }

}
