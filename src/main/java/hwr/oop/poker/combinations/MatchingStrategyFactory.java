package hwr.oop.poker.combinations;

import java.util.List;

public class MatchingStrategyFactory {
    private final CombinationAnalysisSupport analysisSupport;

    public static MatchingStrategyFactory create() {
        return new MatchingStrategyFactory();
    }

    public MatchingStrategyFactory() {
        this.analysisSupport = new CombinationAnalysisSupport();
    }

    public List<CombinationDetectionStrategy> all() {
        return List.of(
                createSinglePair(),
                createTwoPair(),
                createTrips(),
                createStraight(),
                createFlush(),
                createFullHouse(),
                createQuads(),
                createStraightFlush()
        );
    }

    public CombinationDetectionStrategy createSinglePair() {
        return new PairMatchingStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createTwoPair() {
        return new TwoPairMatchingStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createTrips() {
        return new TripMatchingStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createStraight() {
        return new StraightMatchingStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createFlush() {
        return new FlushMatchingStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createFullHouse() {
        return new FullHouseMatchingStrategy(createSinglePair(), createTrips());
    }

    public CombinationDetectionStrategy createQuads() {
        return new QuadsMatchinStrategy(analysisSupport);
    }

    public CombinationDetectionStrategy createStraightFlush() {
        return new StraightFlushMatchingStrategy(createStraight());
    }
}
