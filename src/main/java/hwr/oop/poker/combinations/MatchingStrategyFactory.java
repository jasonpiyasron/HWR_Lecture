package hwr.oop.poker.combinations;

import java.util.List;

public class MatchingStrategyFactory {
    private final AnalysisFlyweightFactory analysisFlyweightFactory;

    public static MatchingStrategyFactory create() {
        return new MatchingStrategyFactory();
    }

    public MatchingStrategyFactory() {
        this.analysisFlyweightFactory = new AnalysisFlyweightFactory();
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
        return new PairMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createTwoPair() {
        return new TwoPairMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createTrips() {
        return new TripMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createStraight() {
        return new StraightMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createFlush() {
        return new FlushMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createFullHouse() {
        return new FullHouseMatchingStrategy(createSinglePair(), createTrips());
    }

    public CombinationDetectionStrategy createQuads() {
        return new QuadsMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createStraightFlush() {
        return new StraightFlushMatchingStrategy(createStraight());
    }
}
