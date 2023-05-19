package hwr.oop.poker;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Combination {
    private final List<Card> nonKickers;
    private final List<Card> kickers;
    private Label label;

    public static Combination of(List<Card> cards) {
        return new Combination(cards);
    }

    public Combination(List<Card> cards) {
        final Map<Symbol, List<Card>> map = createMap(cards);
        this.nonKickers = selectNonKickerCards(map);
        this.kickers = selectKickers(map);
    }

    private List<Card> selectNonKickerCards(Map<Symbol, List<Card>> map) {
        final List<Card> straightCards = straightInMap(map);
        if (!straightCards.isEmpty()) {
            this.label = Label.STRAIGHT;
            return straightCards;
        } else {
            final List<Card> cards = selectTrippedCards(map);
            if (cards.isEmpty()) {
                final List<Card> selected = selectPairedCards(map);
                if (selected.size() == 4) {
                    this.label = Label.TWO_PAIRS;
                } else if (selected.size() == 2) {
                    this.label = Label.PAIR;
                } else {
                    this.label = Label.HIGH_CARD;
                }
                return selected;
            } else {
                this.label = Label.TRIPS;
                return cards;
            }
        }
    }

    private List<Card> straightInMap(Map<Symbol, List<Card>> map) {
        final List<Symbol> symbolsDescByStrength = map.keySet().stream()
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .collect(Collectors.toList());
        int last = -1;
        int count = 0;
        for (Symbol symbol : symbolsDescByStrength) {
            if (last != -1) {
                final int strength = symbol.strength();
                final int strengthDiff = last - strength;
                if (strengthDiff == 1) {
                    count += 1;
                } else {
                    count = 0;
                }
                last = symbol.strength();
                if (count >= 4) {
                    break;
                }
            } else {
                last = symbol.strength();
            }
        }
        if (count == 4) {
            return IntStream.range(last, last + 5)
                    .mapToObj(Symbol::of)
                    .map(s -> map.get(s).get(0))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    public Combination.Label label() {
        return label;
    }

    public List<Card> cards() {
        final List<Card> cards = new ArrayList<>();
        cards.addAll(nonKickers);
        cards.addAll(kickers);
        return cards;
    }

    public List<Card> kickers() {
        return kickers;
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

    private List<Card> selectPairedCards(Map<Symbol, List<Card>> map) {
        final List<Symbol> symbolsWithPairs = symbolsWithPairs(map)
                .sorted((first, second) -> Integer.compare(second.strength(), first.strength()))
                .collect(Collectors.toList());
        final boolean moreThanTwoPairs = symbolsWithPairs.size() > 2;
        if (moreThanTwoPairs) {
            final List<Symbol> selectedSymbols = symbolsWithPairs.subList(0, 2);
            return cardsOfSymbols(selectedSymbols, map);
        } else {
            return cardsOfSymbols(symbolsWithPairs, map);
        }
    }

    private List<Card> cardsOfSymbols(List<Symbol> symbols, Map<Symbol, List<Card>> map) {
        return symbols.stream()
                .map(map::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Card> selectTrippedCards(Map<Symbol, List<Card>> map) {
        return symbolsWithTrips(map)
                .map(map::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Stream<Symbol> selectNonPairs(Map<Symbol, List<Card>> map) {
        return map.entrySet().stream()
                .filter(e -> e.getValue().size() == 1)
                .map(Map.Entry::getKey);
    }

    private Stream<Symbol> symbolsWithPairs(Map<Symbol, List<Card>> map) {
        return map.entrySet().stream()
                .filter(e -> e.getValue().size() == 2)
                .map(Map.Entry::getKey);
    }

    private Stream<Symbol> symbolsWithTrips(Map<Symbol, List<Card>> map) {
        return map.entrySet().stream()
                .filter(e -> e.getValue().size() == 3)
                .map(Map.Entry::getKey);
    }

    private List<Card> selectKickers(Map<Symbol, List<Card>> mutableMap) {
        final List<Symbol> descSortedNonPairedSymbols = selectNonPairs(mutableMap)
                .sorted((first, second) -> Integer.compare(second.strength(), first.strength()))
                .collect(Collectors.toList());
        final List<Card> allKickers = descSortedNonPairedSymbols.stream()
                .map(s -> mutableMap.get(s).get(0))
                .collect(Collectors.toList());
        return allKickers.subList(0, numberOfKickersRequired());
    }

    private int numberOfKickersRequired() {
        return 5 - nonKickers.size();
    }

    public enum Label {
        HIGH_CARD(0), PAIR(1), TWO_PAIRS(2), TRIPS(3), STRAIGHT(4);

        private final int strength;

        Label(int strength) {
            this.strength = strength;
        }

        public int strength() {
            return strength;
        }
    }
}
