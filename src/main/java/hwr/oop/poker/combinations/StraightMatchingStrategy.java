package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Color;
import hwr.oop.poker.Combination;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class StraightMatchingStrategy implements MatchingStrategy {
    private final AnalysisHelper helper;

    public StraightMatchingStrategy(AnalysisHelper helper) {
        this.helper = helper;
    }

    @Override
    public Result match(List<Card> cards) {
        final List<Symbol> symbols = helper.distinctSymbolsDesc(cards);
        final int numberOfSymbols = symbols.size();
        final boolean enoughSymbolsForStraight = numberOfSymbols >= 5;
        if (!enoughSymbolsForStraight) {
            return Result.failure(Combination.Label.STRAIGHT);
        } else {
            final var candidates = straightCandidates(cards);
            if (candidates.isEmpty()) {
                return Result.failure(Combination.Label.STRAIGHT);
            } else {
                return Result.success(Combination.Label.STRAIGHT, candidates);
            }
        }
    }

    private List<List<Card>> straightCandidates(List<Card> cards) {
        final List<Symbol> symbols = helper.distinctSymbolsDesc(cards);
        return straightCandidateRanges(symbols, symbols.size())
                .mapToObj(i -> symbols.subList(i, i + 5))
                .map(candidateSymbols -> {
                    final var cardsWithSymbols = helper.cardsWith(cards, candidateSymbols);
                    final var mostCommonColor = mostCommonColor(cardsWithSymbols);
                    return nestedListWithCardsOfSingleSymbol(cards, candidateSymbols)
                            .map(list -> pickCardOfCorrectColor(mostCommonColor, list))
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
    }

    private Color mostCommonColor(List<Card> cardsWithSymbols) {
        final var colorsAndCounts = colorCountMap(cardsWithSymbols);
        return colorsAndCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
    }

    private Card pickCardOfCorrectColor(Color mostCommonColor, List<Card> list) {
        final boolean cardOfPreferredColorAvailable = list.stream()
                .anyMatch(c -> c.color().equals(mostCommonColor));
        if (cardOfPreferredColorAvailable) {
            return list.stream()
                    .filter(c -> c.color().equals(mostCommonColor))
                    .findFirst().orElseThrow();
        } else {
            return list.get(0);
        }
    }

    private Stream<List<Card>> nestedListWithCardsOfSingleSymbol(List<Card> cards, List<Symbol> candidateSymbols) {
        return candidateSymbols.stream()
                .map(symbol -> helper.cardsWith(cards, symbol));
    }

    private Map<Color, Long> colorCountMap(List<Card> cardsWithSymbols) {
        return cardsWithSymbols.stream()
                .map(Card::color)
                .distinct()
                .collect(Collectors.toMap(
                        color -> color,
                        color -> countOfColorIn(color, cardsWithSymbols)
                ));
    }

    private long countOfColorIn(Color color, List<Card> cards) {
        return cards.stream()
                .filter(card -> card.color().equals(color))
                .count();
    }

    private IntStream straightCandidateRanges(List<Symbol> symbols, int numberOfSymbols) {
        final int top = numberOfSymbols - 4;
        return IntStream.range(0, top)
                .filter(i -> {
                    final Symbol big = symbols.get(i);
                    final Symbol low = symbols.get(i + 4);
                    return big.strength() - low.strength() == 4;
                });
    }
}
