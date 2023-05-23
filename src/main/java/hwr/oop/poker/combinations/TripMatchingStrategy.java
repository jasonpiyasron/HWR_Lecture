package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;

class TripMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationAnalysisSupport helper;

    public TripMatchingStrategy(CombinationAnalysisSupport helper) {
        this.helper = helper;
    }

    @Override
    public Result match(List<Card> cards) {
        final List<Symbol> symbols = helper.symbolsWithTrips(cards)
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        if (symbols.isEmpty()) {
            return Result.failure(Combination.Label.TRIPS);
        } else {
            final List<List<Card>> candidates = symbols.stream()
                    .map(s -> helper.cardsWith(cards, s))
                    .collect(Collectors.toList());
            assert candidates.stream().allMatch(c -> c.size() == 3);
            return Result.success(Combination.Label.TRIPS, candidates);
        }
    }
}
