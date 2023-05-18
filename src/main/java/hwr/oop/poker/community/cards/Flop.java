package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Collection;
import java.util.List;

public class Flop {

    private final List<Card> cards;

    static Flop of(List<Card> list) {
        return new Flop(list);
    }

    private Flop(List<Card> cards) {
        this.cards = cards;
    }

    public Collection<Card> cards() {
        return List.copyOf(cards);
    }

    @Override
    public String toString() {
        return "Flop{" + cards + '}';
    }

}
