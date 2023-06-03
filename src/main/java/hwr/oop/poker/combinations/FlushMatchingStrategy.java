package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Color;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hwr.oop.poker.Combination.Label.FLUSH;

class FlushMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public FlushMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {

        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public Result match(List<Card> cards) {
        final var helper = flyweightFactory.get(cards);
        final var mostCommonColor = helper.mostCommonColor();
        final var cardsOfColor = cardsOfColor(mostCommonColor, cards);
        if (cardsOfColor.size() >= 5) {
            final int range = cardsOfColor.size() - 4;
            final List<List<Card>> candidates = IntStream.range(0, range)
                    .mapToObj(i -> cardsOfColor.subList(i, i + 5))
                    .collect(Collectors.toList());
            assertCombinationIsValidFlush(mostCommonColor, candidates);
            return Result.success(FLUSH, candidates);
        } else {
            return Result.failure(FLUSH);
        }
    }

    private void assertCombinationIsValidFlush(Color mostCommonColor, List<List<Card>> candidates) {
        assert candidates.stream()
                .allMatch(candidate -> candidate.size() == 5 &&
                        candidate.stream().allMatch(card -> card.color().equals(mostCommonColor)));
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
