package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

public class River {

    public static River of(Card card) {
        return new River(card);
    }

    private final Card card;

    private River(Card card) {
        this.card = card;
    }

    public Card card() {
        return card;
    }

    @Override
    public String toString() {
        return "River{" + card + '}';
    }
}
