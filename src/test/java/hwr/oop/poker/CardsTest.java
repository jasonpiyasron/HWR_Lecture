package hwr.oop.poker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CardsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "SPADES",
            "HEARTS",
            "DIAMONDS",
            "CLUBS"
    })
    void sevenForAllColors(String colorString) {
        final Symbol seven = Symbol.SEVEN;
        final Color expectedColor = Color.valueOf(colorString);

        Card card = new Card(expectedColor, seven);
        Color color = card.color();
        Symbol number = card.number();

        assertThat(color).isEqualTo(expectedColor);
        assertThat(number).isEqualTo(seven);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "TWO",
            "THREE",
            "FOUR",
            "FIVE",
            "SIX",
            "SEVEN",
            "EIGHT",
            "NINE",
            "TEN",
            "JACK",
            "QUEEN",
            "KING",
            "ACE"
    })
    void allCardsOfHearts(String numberString) {
        final Symbol expectedNumber = Symbol.valueOf(numberString);
        final Color hearts = Color.HEARTS;

        Card card = new Card(hearts, expectedNumber);
        Color color = card.color();
        Symbol number = card.number();

        assertThat(color).isEqualTo(hearts);
        assertThat(number).isEqualTo(expectedNumber);
    }
}
