package hwr.oop.poker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Parse cards from Strings (makes testing easier)")
class EasierCardImportsForTestsTest {

    @Test
    @DisplayName("7H -> SEVEN of HEARTS")
    void string7H_IsSevenOfHearts() {
        final String sevenOfHeartsString = "7H";
        final Card card = Converter.from(sevenOfHeartsString);
        assertThat(card.number()).isEqualTo(Symbol.SEVEN);
        assertThat(card.color()).isEqualTo(Color.HEARTS);
    }

    @Test
    @DisplayName("AS -> ACE of SPADES")
    void stringAS_IsSevenOfHearts() {
        final String aceOfSpadesString = "AS";
        final Card card = Converter.from(aceOfSpadesString);
        assertThat(card.number()).isEqualTo(Symbol.ACE);
        assertThat(card.color()).isEqualTo(Color.SPADES);
    }

    @Test
    @DisplayName("8C -> EIGHT of CLUBS")
    void string8C_IsEightOfClubs() {
        final String aceOfSpadesString = "8C";
        final Card card = Converter.from(aceOfSpadesString);
        assertThat(card.number()).isEqualTo(Symbol.EIGHT);
        assertThat(card.color()).isEqualTo(Color.CLUBS);
    }

    @Test
    @DisplayName("JD -> JACK of DIAMONDS")
    void stringJD_IsJackOfDiamonds() {
        final String aceOfSpadesString = "JD";
        final Card card = Converter.from(aceOfSpadesString);
        assertThat(card.number()).isEqualTo(Symbol.JACK);
        assertThat(card.color()).isEqualTo(Color.DIAMONDS);
    }
}
