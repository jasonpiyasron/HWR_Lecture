package hwr.oop.poker;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class Card {
    private final Color color;
    private final Symbol number;

    public Card(Color color, Symbol number) {
        this.color = color;
        this.number = number;
    }

    public Color color() {
        return color;
    }

    public Symbol symbol() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return color == card.color && number == card.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, number);
    }

    @Override
    public String toString() {
        return number.toString() + " of " + color.toString();
    }

    public interface Provider {
        Stream<Card> cards();
    }

    public static final Comparator<Card> DESCENDING_BY_SYMBOL_STRENGTH =
            (o1, o2) -> Integer.compare(o2.symbol().strength(), o1.symbol().strength());
}
