package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;

import java.util.List;

import static hwr.oop.poker.Combination.Label.QUADS;

class QuadsMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public QuadsMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public Result match(List<Card> cards) {
        final var analysisSupport = flyweightFactory.get(cards);
        final var optional = analysisSupport.symbolsWithQuads().findFirst();
        if (optional.isEmpty()) {
            return Result.failure(QUADS);
        } else {
            final var symbolWithQuads = optional.get();
            final var quads = analysisSupport.cardsWith(symbolWithQuads);
            return Result.success(QUADS, List.of(quads));
        }
    }
}
