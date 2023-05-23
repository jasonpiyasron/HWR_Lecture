package hwr.oop.poker;

import hwr.oop.poker.combinations.CombinationDetectionStrategy;
import hwr.oop.poker.combinations.MatchingStrategyFactory;
import hwr.oop.poker.testing.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @ParameterizedTest(name = "pair strategy, cards ({0}), combination ({1})")
    @CsvSource(delimiter = '-', value = {
            "2H,4S,6D,8C,TH,TS,QD - TH,TS",
            "AS,AC,KD,JH,TS,2C,4C - AS,AC",
            "KC,JD,9C,7H,5D,TS,KH - KC,KH",
    })
    void singlePairs_IndividualStrategy(String inputCards, String combination) {
        final List<Card> cards = converter.convert(inputCards);
        final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
        final CombinationDetectionStrategy pairStrategy = factory.createSinglePair();
        CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

        // match successful
        final boolean success = result.successful();
        assertThat(success).isTrue();

        // correct label
        final Combination.Label label = result.label();
        assertThat(label).isEqualTo(Combination.Label.PAIR);

        // correct combination cards in correct order
        final List<Card> expectedBestCombination = converter.convert(combination);
        final List<Card> combinationCards = result.winner();
        assertThat(combinationCards)
                .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
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
            "9C,2H,TS,8H,JD,QH,AS - QH,JD,TS,9C,8H",
            "5D,2H,4S,6H,7C,3C,8S - 8S,7C,6H,5D,4S",
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

    @Nested
    class IndividualStrategyTest {
        @ParameterizedTest(name = "pair strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "2H,4S,6D,8C,TH,TS,QD - TH,TS",
                "AS,AC,KD,JH,TS,2C,4C - AS,AC",
                "KC,JD,9C,7H,5D,TS,KH - KC,KH",
        })
        void singlePairs_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createSinglePair();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.PAIR);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "two pair strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "AS,AD,KS,KD,2C,3C,4C - AS,AD,KS,KD",
                "2S,3S,6C,JC,JH,QS,QD - QS,QD,JC,JH",
                "2C,2H,6C,6H,KC,KH,AS - KC,KH,6C,6H",
        })
        void twoPairs_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createTwoPair();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.TWO_PAIRS);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "trip strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "AS,AD,AC,2C,3C,5C,6S - AS,AD,AC",
                "AH,KD,8H,8D,8S,3C,2S - 8H,8D,8S",
                "JH,QD,KC,AS,2H,2D,2S - 2H,2D,2S",
        })
        void trips_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createTrips();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.TRIPS);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "straight strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "AS,KC,QH,JD,TS,3C,4H - AS,KC,QH,JD,TS",
                "9C,2H,TS,8H,JD,QH,AS - QH,JD,TS,9C,8H",
                "5D,2H,4S,6H,7C,3C,8S - 8S,7C,6H,5D,4S",
        })
        void straights_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createStraight();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.STRAIGHT);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "flush strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "JS,AS,7S,KS,QS,8S,9S - AS,KS,QS,JS,9S",
                "3D,TD,KD,6D,2D,QD,7D - KD,QD,TD,7D,6D",
                "JH,TH,KD,AH,QD,9H,3H - AH,JH,TH,9H,3H",
        })
        void flush_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createFlush();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.FLUSH);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "full house strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "AH,2C,KD,KS,AD,3C,AS - AS,AH,AD,KD,KS",
                "KH,QD,2C,QH,KD,KS,QS - KS,KD,KH,QD,QH",
                "2D,AD,TC,AH,2S,AS,TH - AD,AH,AS,TC,TH"
        })
        void fullHouse_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createFullHouse();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.FULL_HOUSE);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "full house strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "3D,2S,AD,AS,AH,AC,4C - AD,AS,AH,AC",
                "2S,2D,QS,AS,2C,KS,2H - 2S,2D,2C,2H",
                "TH,TC,TS,4S,TD,2C,3D - TH,TC,TS,TD",
        })
        void quads_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createQuads();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.QUADS);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }

        @ParameterizedTest(name = "straight flush strategy, cards ({0}), combination ({1})")
        @CsvSource(delimiter = '-', value = {
                "AS,KS,QS,JS,TS,9S,8S - AS,KS,QS,JS,TS",
                "TS,8S,AD,QS,KD,JS,9S - QS,JS,TS,9S,8S",
                "6S,2S,5S,3S,8D,7D,4S - 6S,5S,4S,3S,2S",
        })
        void straightFlush_IndividualStrategy(String inputCards, String combination) {
            final List<Card> cards = converter.convert(inputCards);
            final MatchingStrategyFactory factory = MatchingStrategyFactory.create();
            final CombinationDetectionStrategy pairStrategy = factory.createStraightFlush();
            CombinationDetectionStrategy.Result result = pairStrategy.match(cards);

            // match successful
            final boolean success = result.successful();
            assertThat(success).isTrue();

            // correct label
            final Combination.Label label = result.label();
            assertThat(label).isEqualTo(Combination.Label.STRAIGHT_FLUSH);

            // correct combination cards in correct order
            final List<Card> expectedBestCombination = converter.convert(combination);
            final List<Card> combinationCards = result.winner();
            assertThat(combinationCards)
                    .containsExactlyInAnyOrderElementsOf(expectedBestCombination);
        }
    }
}

