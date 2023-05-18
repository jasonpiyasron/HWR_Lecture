package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Collection;
import java.util.List;

public interface Flop {

    static Flop of(List<Card> list) {
        return new SimpleFlop(list);
    }

    Collection<Card> cards();

    class SimpleFlop implements Flop {

        private final List<Card> cards;

        private SimpleFlop(List<Card> cards) {
            this.cards = cards;
        }

        @Override
        public Collection<Card> cards() {
            return List.copyOf(cards);
        }

        @Override
        public String toString() {
            return "Flop{" + cards + '}';
        }
    }
}
