package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.stream.Collectors;

class PairMatchingStrategy implements MatchingStrategy {
    private final AnalysisHelper helper;

    public PairMatchingStrategy(AnalysisHelper helper) {
        this.helper = helper;
    }

    @Override
    public Result match(List<Card> cards) {
        final List<Symbol> pairedSymbols = helper.symbolsWithPairs(cards)
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        if (pairedSymbols.isEmpty()) {
            return Result.failure(Combination.Label.PAIR);
        } else {
            final List<List<Card>> candidates = pairedSymbols.stream()
                    .map(s -> helper.cardsWith(cards, s))
                    .collect(Collectors.toList());
            assert candidates.stream().allMatch(c -> c.size() == 2);
            return Result.success(Combination.Label.PAIR, candidates);
        }
    }

}
