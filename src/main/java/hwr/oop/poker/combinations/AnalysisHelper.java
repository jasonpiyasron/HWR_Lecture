package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Color;
import hwr.oop.poker.Symbol;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalysisHelper {


    private Map<Symbol, List<Card>> map;

    public AnalysisHelper() {
        this.map = null;
    }

    public Stream<Symbol> symbolsWithPairs(List<Card> cards) {
        lazyInitializeMapIfRequired(cards);
        return symbolsThatArePresent(2);
    }

    public Stream<Symbol> symbolsWithTrips(List<Card> cards) {
        lazyInitializeMapIfRequired(cards);
        return symbolsThatArePresent(3);
    }

    public List<Card> cardsWith(List<Card> cards, Symbol... symbols) {
        lazyInitializeMapIfRequired(cards);
        return Arrays.stream(symbols)
                .map(s -> map.get(s))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Card> cardsWith(List<Card> cards, List<Symbol> symbols) {
        lazyInitializeMapIfRequired(cards);
        return symbols.stream()
                .map(s -> map.get(s))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Stream<Symbol> symbolsThatArePresent(int numberOfTimesPresent) {
        return map.entrySet().stream()
                .filter(e -> e.getValue().size() == numberOfTimesPresent)
                .map(Map.Entry::getKey);
    }

    private void lazyInitializeMapIfRequired(List<Card> cards) {
        if (map == null) {
            this.map = createMap(cards);
        }
    }

    private Map<Symbol, List<Card>> createMap(List<Card> cards) {
        final Map<Symbol, List<Card>> mutableMap = new EnumMap<>(Symbol.class);
        final Stream<Symbol> symbols = cards.stream()
                .map(Card::symbol)
                .distinct();
        symbols.forEach(s -> mutableMap.put(s, new ArrayList<>()));
        cards.forEach(c -> mutableMap.get(c.symbol()).add(c));
        return Collections.unmodifiableMap(mutableMap);
    }

    public List<Symbol> distinctSymbolsDesc(List<Card> cards) {
        lazyInitializeMapIfRequired(cards);
        return cards.stream()
                .map(Card::symbol)
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .distinct()
                .collect(Collectors.toList());
    }

    public Color mostCommonColor(List<Card> cards) {
        final var colorsAndCounts = colorCountMap(cards);
        return colorsAndCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
    }

    private Map<Color, Long> colorCountMap(List<Card> cards) {
        return cards.stream()
                .map(Card::color)
                .distinct()
                .collect(Collectors.toMap(
                        color -> color,
                        color -> countOfColorIn(color, cards)
                ));
    }

    private long countOfColorIn(Color color, List<Card> cards) {
        return cards.stream()
                .filter(card -> card.color().equals(color))
                .count();
    }
}
