package hwr.oop.poker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Creating Cards")
class CardsTest {

    @ParameterizedTest
    @DisplayName("create all 4 cards with symbol SEVEN")
    @ValueSource(strings = {"SPADES", "HEARTS", "DIAMONDS", "CLUBS"})
    void canCreateSevenForEachOfTheFourColors(String colorString) {
        final Symbol seven = Symbol.SEVEN;
        final Color expectedColor = Color.valueOf(colorString);

        final Card card = new Card(expectedColor, seven);
        final Color color = card.color();
        final Symbol number = card.number();

        assertThat(color).isEqualTo(expectedColor);
        assertThat(number).isEqualTo(seven);
    }


    @ParameterizedTest
    @DisplayName("create all cards with color HEARTS")
    @ValueSource(strings = {
            "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING", "ACE"
    })
    void canCreateAllCardsWithHearts(String numberString) {
        final Symbol expectedNumber = Symbol.valueOf(numberString);
        final Color hearts = Color.HEARTS;

        final Card card = new Card(hearts, expectedNumber);
        final Color color = card.color();
        final Symbol number = card.number();

        assertThat(color).isEqualTo(hearts);
        assertThat(number).isEqualTo(expectedNumber);
    }
}
