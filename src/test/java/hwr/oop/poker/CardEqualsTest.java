package hwr.oop.poker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Equality of Cards")
class CardEqualsTest {

    @Test
    @DisplayName("same Color, different Symbol, not equal")
    void sameColorDifferentSymbol_NotEqual() {
        final Card tenOfHearts = new Card(Color.HEARTS, Symbol.TEN);
        final Card jackOfHearts = new Card(Color.HEARTS, Symbol.JACK);
        assertThat(tenOfHearts).isNotEqualTo(jackOfHearts);
    }

    @Test
    @DisplayName("different Color, same Symbol, not equal")
    void differentColorSameSymbol_NotEqual() {
        final Card tenOfHearts = new Card(Color.HEARTS, Symbol.TEN);
        final Card tenOfClubs = new Card(Color.CLUBS, Symbol.TEN);
        assertThat(tenOfHearts).isNotEqualTo(tenOfClubs);
    }

    @Test
    @DisplayName("different Color and Symbol, not equal")
    void differentColorDifferentSymbol_NotEqual() {
        final Card nineOfHearts = new Card(Color.HEARTS, Symbol.NINE);
        final Card tenOfClubs = new Card(Color.CLUBS, Symbol.TEN);
        assertThat(nineOfHearts).isNotEqualTo(tenOfClubs);
    }

    @Test
    @DisplayName("same Color and Symbol, is equal")
    void sameColorSameSymbol_Equal() {
        final Card first = new Card(Color.HEARTS, Symbol.TEN);
        final Card second = new Card(Color.HEARTS, Symbol.TEN);
        assertThat(first).isEqualTo(second);
    }

}
