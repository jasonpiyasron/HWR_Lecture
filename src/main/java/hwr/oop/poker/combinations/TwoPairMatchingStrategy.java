package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hwr.oop.poker.Combination.Label.TWO_PAIRS;

class TwoPairMatchingStrategy implements MatchingStrategy {
    private final AnalysisHelper helper;

    public TwoPairMatchingStrategy(AnalysisHelper helper) {
        this.helper = helper;
    }

    @Override
    public Result match(List<Card> cards) {

        final List<Symbol> pairedSymbols = helper.symbolsWithPairs(cards)
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        if (pairedSymbols.size() < 2) {
            return Result.failure(TWO_PAIRS);
        } else {
            final List<List<Card>> candidates = pairedSymbols.stream()
                    .flatMap(symbol -> buildCandidatesFor(symbol, pairedSymbols, cards).stream())
                    .collect(Collectors.toList());
            assert candidates.stream().allMatch(l -> l.size() == 4);
            return Result.success(TWO_PAIRS, candidates);
        }
    }

    private List<List<Card>> buildCandidatesFor(Symbol symbol, List<Symbol> pairedSymbols, List<Card> cards) {
        final List<Card> firstPair = helper.cardsWith(cards, symbol);
        return pairedSymbols.stream()
                .filter(s -> !symbol.equals(s))
                .map(s -> helper.cardsWith(cards, s))
                .map(secondPair -> combinePairs(firstPair, secondPair))
                .collect(Collectors.toList());
    }

    private List<Card> combinePairs(List<Card> firstPair, List<Card> secondPair) {
        List<Card> combinedList = new ArrayList<>();
        combinedList.addAll(firstPair);
        combinedList.addAll(secondPair);
        return combinedList;
    }

}
