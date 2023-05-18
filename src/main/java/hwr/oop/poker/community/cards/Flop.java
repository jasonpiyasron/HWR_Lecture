package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.List;
import java.util.stream.Stream;

public class Flop implements CommunityCardProvider {

    private final List<Card> cards;

    public static Flop of(List<Card> list) {
        return new Flop(list);
    }

    private Flop(List<Card> cards) {
        assertValidFlop(cards);
        this.cards = cards;
    }

    private void assertValidFlop(List<Card> cards) {
        if (cards == null || cards.size() != 3) {
            throw new InvalidFlopException(cards);
        }
    }

    @Override
    public Stream<Card> cards() {
        return cards.stream();
    }

    @Override
    public String toString() {
        return "Flop{" + cards + '}';
    }

    private static class InvalidFlopException extends RuntimeException {
        public InvalidFlopException(List<Card> cards) {
            super("Cannot create flop, requires 3 cards, got: " + cards);
        }
    }
}
