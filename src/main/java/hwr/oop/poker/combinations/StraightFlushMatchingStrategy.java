package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;

import java.util.List;
import java.util.stream.Collectors;

class StraightFlushMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationDetectionStrategy straights;

    public StraightFlushMatchingStrategy(CombinationDetectionStrategy straights) {
        this.straights = straights;
    }

    @Override
    public Result match(List<Card> cards) {
        final Result straightResult = straights.match(cards);
        if (!straightResult.successful()) {
            return Result.failure(Combination.Label.STRAIGHT_FLUSH);
        } else {
            final List<List<Card>> candidates = straightResult.alternatives();
            final List<List<Card>> straightFlushes = candidates.stream()
                    .filter(this::isDistinctColor)  // straight strategy always prefers most common color
                    .collect(Collectors.toList());
            return Result.success(Combination.Label.STRAIGHT_FLUSH, straightFlushes);
        }
    }

    private boolean isDistinctColor(List<Card> candidate) {
        return candidate.stream().map(Card::color).distinct().count() == 1;
    }
}
