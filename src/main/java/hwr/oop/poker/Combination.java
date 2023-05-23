package hwr.oop.poker;

import hwr.oop.poker.combinations.CombinationDetectionStrategy;
import hwr.oop.poker.combinations.MatchingStrategyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Combination {
    private final List<Card> nonKickers;
    private final List<Card> kickers;
    private Label label;

    public static Combination of(List<Card> cards) {
        return new Combination(cards);
    }

    public Combination(List<Card> cards) {
        this.nonKickers = selectNonKickerCards(cards);
        this.kickers = selectKickers(cards);
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

    private List<Card> selectNonKickerCards(List<Card> cards) {
        MatchingStrategyFactory factory = new MatchingStrategyFactory();
        final List<CombinationDetectionStrategy> strategies = List.of(
                factory.createFlush(),
                factory.createStraight(),
                factory.createTrips(),
                factory.createTwoPair(),
                factory.createSinglePair()
        );
        for (CombinationDetectionStrategy strategy : strategies) {
            final CombinationDetectionStrategy.Result result = strategy.match(cards);
            if (result.successful()) {
                this.label = result.label();
                return result.winner();
            }
        }
        this.label = Label.HIGH_CARD;
        return List.of();
    }

    private List<Card> selectKickers(List<Card> cards) {
        final List<Card> kickerCandidates = cards.stream()
                .filter(c -> !nonKickers.contains(c))
                .sorted(Card.DESCENDING_BY_SYMBOL_STRENGTH)
                .collect(Collectors.toList());
        return kickerCandidates.subList(0, numberOfKickersRequired());
    }

    private int numberOfKickersRequired() {
        return 5 - nonKickers.size();
    }

    public enum Label {
        HIGH_CARD(0), PAIR(1), TWO_PAIRS(2), TRIPS(3), STRAIGHT(4), FLUSH(5), FULL_HOUSE(6), QUADS(7), STRAIGHT_FLUSH(8);

        private final int strength;

        Label(int strength) {
            this.strength = strength;
        }

        public int strength() {
            return strength;
        }
    }
}
