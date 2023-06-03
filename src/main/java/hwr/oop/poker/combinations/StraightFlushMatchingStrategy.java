package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;

import java.util.List;
import java.util.stream.Collectors;

import static hwr.oop.poker.Combination.Label.STRAIGHT_FLUSH;

class StraightFlushMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationDetectionStrategy straights;

    public StraightFlushMatchingStrategy(CombinationDetectionStrategy straights) {
        this.straights = straights;
    }

    @Override
    public Result match(List<Card> cards) {
        final var straightResult = straights.match(cards);
        final boolean noStraight = !straightResult.successful();
        if (noStraight) {
            return Result.failure(STRAIGHT_FLUSH);
        } else {
            return match(straightResult);
        }
    }

    private Result match(Result successfulStraightResult) {
        assertIsReallySuccessful(successfulStraightResult);
        final var straightFlushes = selectStraightsOfSameColor(successfulStraightResult);
        final var noStraightFlush = straightFlushes.isEmpty();
        if (noStraightFlush) {
            return Result.failure(STRAIGHT_FLUSH);
        } else {
            return Result.success(STRAIGHT_FLUSH, straightFlushes);
        }
    }

    private void assertIsReallySuccessful(Result successfulStraightResult) {
        assert successfulStraightResult.label() == Combination.Label.STRAIGHT;
        assert successfulStraightResult.successful();
    }

    private List<List<Card>> selectStraightsOfSameColor(Result straightResult) {
        final var candidates = straightResult.alternatives();
        return filterOutFlushes(candidates);
    }

    private List<List<Card>> filterOutFlushes(List<List<Card>> candidates) {
        return candidates.stream()
                .filter(this::isDistinctColor)  // straight strategy always prefers most common color
                .collect(Collectors.toList());
    }

    private boolean isDistinctColor(List<Card> candidate) {
        return numberOfDistinctColors(candidate) == 1;
    }

    private long numberOfDistinctColors(List<Card> candidate) {
        return candidate.stream()
                .map(Card::color)
                .distinct().count();
    }
}
