package hwr.oop.poker;

public class Converter {
    public static Card from(String singleCardString) {
        assertValidLength(singleCardString);
        final String symbolString = singleCardString.substring(0, 1);
        final String colorString = singleCardString.substring(1, 2);
        return new Card(
                parseColor(colorString),
                parseSymbol(symbolString)
        );
    }

    private static Symbol parseSymbol(String symbolString) {
        // TODO This is ugly!
        switch (symbolString) {
            case "2":
                return Symbol.TWO;
            case "3":
                return Symbol.THREE;
            case "4":
                return Symbol.FOUR;
            case "5":
                return Symbol.FIVE;
            case "6":
                return Symbol.SIX;
            case "7":
                return Symbol.SEVEN;
            case "8":
                return Symbol.EIGHT;
            case "9":
                return Symbol.NINE;
            case "T":
                return Symbol.TEN;
            case "J":
                return Symbol.JACK;
            case "Q":
                return Symbol.QUEEN;
            case "K":
                return Symbol.KING;
            case "A":
                return Symbol.ACE;
            default:
                throw new IllegalArgumentException("Can not parse symbol, expected: [23456789TJQKA], actual: " + symbolString);
        }
    }

    private static Color parseColor(String colorString) {
        // TODO This is ugly!
        switch (colorString) {
            case "H":
                return Color.HEARTS;
            case "D":
                return Color.DIAMONDS;
            case "S":
                return Color.SPADES;
            case "C":
                return Color.CLUBS;
            default:
                throw new IllegalArgumentException("Can not parse color from string, expected: [HDSC], actual: " + colorString);
        }
    }

    private static void assertValidLength(String singleCardString) {
        if (singleCardString.length() > 2) {
            throw new IllegalArgumentException("Can not create card from string: " + singleCardString);
        }
    }
}
