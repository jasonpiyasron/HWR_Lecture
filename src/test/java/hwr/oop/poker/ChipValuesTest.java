package hwr.oop.poker;

import hwr.oop.poker.blinds.SmallBlind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Stacks of chips have a Chip Value")
class ChipValuesTest {

    @Test
    @DisplayName("Small Blind of 1, has a Chip Value of 1")
    void smallBlindOfOne_ChipValueOfOne() {
        final ChipValue smallBlind = SmallBlind.of(1);
        long value = smallBlind.value();
        assertThat(value).isEqualTo(1);
    }

    @Test
    @DisplayName("Big Blind of Small Blind of 1, has a Chip Value of 2")
    void bigBlind_HasChipValue() {
        final SmallBlind smallBlind = SmallBlind.of(1);
        final ChipValue bigBlind = smallBlind.bigBlind();
        long value = bigBlind.value();
        assertThat(value).isEqualTo(2);
    }

    @Test
    @DisplayName("Big Blind of Small Blind of 1337, is (strictly) greater than Small Blind")
    void compareSmallBlindAndBigBlind_BigBlindIsStrictlyBigger() {
        final SmallBlind smallBlind = SmallBlind.of(1337);
        final ChipValue valuedBigBlind = smallBlind.bigBlind();
        assertThat(valuedBigBlind).isGreaterThan(smallBlind);
    }

    @Test
    @Disabled("Chip values outside of blinds are not yet implemented")
    void trueStacksIndependentOfBlindsAndAntes() {
        Assertions.fail("Not yet implemented");
    }
}
