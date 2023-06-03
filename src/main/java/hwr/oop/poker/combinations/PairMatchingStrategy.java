package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;

import static hwr.oop.poker.Combination.Label.PAIR;

class PairMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public PairMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public Result match(List<Card> cards) {
        final AnalysisFlyweight helper = flyweightFactory.get(cards);
        final List<Symbol> pairedSymbols = helper.symbolsWithPairs()
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        if (pairedSymbols.isEmpty()) {
            return Result.failure(PAIR);
        } else {
            final List<List<Card>> candidates = pairedSymbols.stream()
                    .map(helper::cardsWith)
                    .collect(Collectors.toList());
            assert candidates.stream().allMatch(c -> c.size() == 2);
            return Result.success(PAIR, candidates);
        }
    }

}
