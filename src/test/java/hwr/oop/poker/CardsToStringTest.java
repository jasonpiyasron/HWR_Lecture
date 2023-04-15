package hwr.oop.poker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cards should be described in simple text if logged as string representation")
class CardsToStringTest {
    @Test
    @DisplayName("#toString: seven of hearts")
    void sevenOfHearts_ShouldHaveExactlyThisToStringResult() {
        final Card card = new Card(Color.HEARTS, Symbol.SEVEN);
        final String toString = card.toString().toLowerCase();
        assertThat(toString).isEqualTo("seven of hearts");
    }

    @Test
    @DisplayName("#toString: ace of spades")
    void aceOfSpades_ShouldHaveExactlyThisToStringResult() {
        final Card card = new Card(Color.SPADES, Symbol.ACE);
        final String toString = card.toString().toLowerCase();
        assertThat(toString).isEqualTo("ace of spades");
    }
}
