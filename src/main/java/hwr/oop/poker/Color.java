package hwr.oop.poker;

public enum Color {
    SPADES("S"), HEARTS("H"), DIAMONDS("D"), CLUBS("C");

    private final String stringRepresentation;

    Color(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String stringRepresentation() {
        return stringRepresentation;
    }
}
