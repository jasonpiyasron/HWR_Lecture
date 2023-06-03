package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;

import static hwr.oop.poker.Combination.Label.TRIPS;

class TripMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public TripMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public Result match(List<Card> cards) {
        final var helper = flyweightFactory.get(cards);
        final List<Symbol> symbols = helper.symbolsWithTrips()
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        if (symbols.isEmpty()) {
            return Result.failure(TRIPS);
        } else {
            final List<List<Card>> candidates = symbols.stream()
                    .map(helper::cardsWith)
                    .collect(Collectors.toList());
            assert candidates.stream().allMatch(c -> c.size() == 3);
            return Result.success(TRIPS, candidates);
        }
    }
}
