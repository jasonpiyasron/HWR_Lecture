package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

public interface Turn {
    static Turn of(Card card) {
        return new SimpleTurn(card);
    }

    Card card();

    class SimpleTurn implements Turn {
        private final Card card;

        public SimpleTurn(Card card) {
            this.card = card;
        }

        @Override
        public Card card() {
            return card;
        }

        @Override
        public String toString() {
            return "Turn{" + card + '}';
        }
    }
}
