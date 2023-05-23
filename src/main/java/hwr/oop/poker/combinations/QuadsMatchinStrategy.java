package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;
import hwr.oop.poker.Symbol;

import java.util.List;
import java.util.Optional;

class QuadsMatchinStrategy implements CombinationDetectionStrategy {
    private final CombinationAnalysisSupport analysisSupport;

    public QuadsMatchinStrategy(CombinationAnalysisSupport analysisSupport) {
        this.analysisSupport = analysisSupport;
    }

    @Override
    public Result match(List<Card> cards) {
        final Optional<Symbol> optional = analysisSupport.symbolsWithQuads(cards).findFirst();
        if (optional.isEmpty()) {
            return Result.failure(Combination.Label.QUADS);
        } else {
            final Symbol symbolWithQuads = optional.get();
            final List<Card> quads = analysisSupport.cardsWith(cards, symbolWithQuads);
            return Result.success(Combination.Label.QUADS, List.of(quads));
        }
    }
}
