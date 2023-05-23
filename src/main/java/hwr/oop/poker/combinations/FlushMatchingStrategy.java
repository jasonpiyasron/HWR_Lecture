package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Color;
import hwr.oop.poker.Combination;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FlushMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationAnalysisSupport helper;

    public FlushMatchingStrategy(CombinationAnalysisSupport helper) {
        this.helper = helper;
    }

    @Override
    public Result match(List<Card> cards) {
        final Color mostCommonColor = helper.mostCommonColor(cards);
        final List<Card> cardsOfColor = cardsOfColor(mostCommonColor, cards);
        if (cardsOfColor.size() >= 5) {
            final int range = cardsOfColor.size() - 4;
            final List<List<Card>> candidates = IntStream.range(0, range)
                    .mapToObj(i -> cardsOfColor.subList(i, i + 5))
                    .collect(Collectors.toList());
            assert candidates.stream()
                    .allMatch(candidate -> candidate.size() == 5 &&
                            candidate.stream().allMatch(card -> card.color().equals(mostCommonColor)));
            return Result.success(Combination.Label.FLUSH, candidates);
        } else {
            return Result.failure(Combination.Label.FLUSH);
        }
    }

    private List<Card> cardsOfColor(Color mostCommonColor, List<Card> cards) {
        return cards.stream()
                .filter(c -> c.color().equals(mostCommonColor))
                .sorted(FlushMatchingStrategy::compareCardsBySymbol)
                .collect(Collectors.toList());
    }

    private static int compareCardsBySymbol(Card o1, Card o2) {
        return Symbol.DESCENDING_BY_STRENGTH.compare(o1.symbol(), o2.symbol());
    }

}
