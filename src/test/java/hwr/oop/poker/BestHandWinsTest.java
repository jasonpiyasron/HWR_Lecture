package hwr.oop.poker;

import hwr.oop.poker.testing.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BestHandWinsTest {

    private Converter converter;

    @BeforeEach
    void setUp() {
        converter = Converter.create();
    }

    @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
    @CsvSource(delimiter = '-', value = {
            "2H,4S,6D,8C,TH,QS,AD - AD,QS,TH,8C,6D - AD,QS,TH,8C,6D",
            "3S,5D,7C,9H,JS,QD,KC - KC,QD,JS,9H,7C - KC,QD,JS,9H,7C",
            "AS,KS,QC,JC,9H,8H,7D - AS,KS,QC,JC,9H - AS,KS,QC,JC,9H",
    })
    void highCard(String inputCards, String bestCombinationString, String kickersString) {
        final List<Card> cards = converter.convert(inputCards);
        final Combination combination = Combination.of(cards);

        // correct label
        final Combination.Label label = combination.label();
        assertThat(label).isEqualTo(Combination.Label.HIGH_CARD);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(bestCombinationString);
        final List<Card> combinationCards = combination.cards();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);

        // correct kickers
        final List<Card> expectedKickers = converter.convert(kickersString);
        final List<Card> kickers = combination.kickers();
        assertThat(kickers).containsExactlyElementsOf(expectedKickers);
    }

    @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
    @CsvSource(delimiter = '-', value = {
            "2H,4S,6D,8C,TH,TS,QD - TH,TS,QD,8C,6D - QD,8C,6D",
            "AS,AC,KD,JH,TS,2C,4C - AS,AC,KD,JH,TS - KD,JH,TS",
            "KC,JD,9C,7H,5D,TS,KH - KC,KH,JD,TS,9C - JD,TS,9C",
    })
    void singlePairs(String inputCards, String bestCombinationString, String kickersString) {
        final List<Card> cards = converter.convert(inputCards);
        final Combination combination = Combination.of(cards);

        // correct label
        final Combination.Label label = combination.label();
        assertThat(label).isEqualTo(Combination.Label.PAIR);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(bestCombinationString);
        final List<Card> combinationCards = combination.cards();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);

        // correct kickers
        final List<Card> expectedKickers = converter.convert(kickersString);
        final List<Card> kickers = combination.kickers();
        assertThat(kickers).containsExactlyElementsOf(expectedKickers);
    }

    @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
    @CsvSource(delimiter = '-', value = {
            "AS,AD,KS,KD,2C,3C,4C - AS,AD,KS,KD,4C - 4C",
            "2S,3S,6C,JC,JH,QS,QD - QS,QD,JC,JH,6C - 6C",
            "2C,2H,6C,6H,KC,KH,AS - KC,KH,6C,6H,AS - AS",
    })
    void twoPairs(String inputCards, String bestCombinationString, String kickersString) {
        final List<Card> cards = converter.convert(inputCards);
        final Combination combination = Combination.of(cards);

        // correct label
        final Combination.Label label = combination.label();
        assertThat(label).isEqualTo(Combination.Label.TWO_PAIRS);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(bestCombinationString);
        final List<Card> combinationCards = combination.cards();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);

        // correct kickers
        final List<Card> expectedKickers = converter.convert(kickersString);
        final List<Card> kickers = combination.kickers();
        assertThat(kickers).containsExactlyElementsOf(expectedKickers);
    }

    @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
    @CsvSource(delimiter = '-', value = {
            "AS,AD,AC,2C,3C,5C,6S - AS,AD,AC,6S,5C - 6S,5C",
            "AH,KD,8H,8D,8S,3C,2S - 8H,8D,8S,AH,KD - AH,KD",
            "JH,QD,KC,AS,2H,2D,2S - 2H,2D,2S,AS,KC - AS,KC",
    })
    void trips(String inputCards, String bestCombinationString, String kickersString) {
        final List<Card> cards = converter.convert(inputCards);
        final Combination combination = Combination.of(cards);

        // correct label
        final Combination.Label label = combination.label();
        assertThat(label).isEqualTo(Combination.Label.TRIPS);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(bestCombinationString);
        final List<Card> combinationCards = combination.cards();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);

        // correct kickers
        final List<Card> expectedKickers = converter.convert(kickersString);
        final List<Card> kickers = combination.kickers();
        assertThat(kickers).containsExactlyElementsOf(expectedKickers);
    }

    @ParameterizedTest(name = "cards ({0}), combination ({1}), no kickers")
    @CsvSource(delimiter = '-', value = {
            "AS,KC,QH,JD,TS,3C,4H - AS,KC,QH,JD,TS",
    })
    void straights(String inputCards, String bestCombinationString) {
        final List<Card> cards = converter.convert(inputCards);
        final Combination combination = Combination.of(cards);

        // correct label
        final Combination.Label label = combination.label();
        assertThat(label).isEqualTo(Combination.Label.STRAIGHT);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(bestCombinationString);
        final List<Card> combinationCards = combination.cards();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);

        // correct kickers
        final List<Card> kickers = combination.kickers();
        assertThat(kickers).isEmpty();
    }
}
