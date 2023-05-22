package hwr.oop.poker.combinations;

public class MatchingStrategyFactory {
    private final AnalysisHelper helper;

    public static MatchingStrategyFactory create() {
        return new MatchingStrategyFactory();
    }

    public MatchingStrategyFactory() {
        this.helper = new AnalysisHelper();
    }

    public MatchingStrategy createSinglePair() {
        return new PairMatchingStrategy(helper);
    }

    public MatchingStrategy createTwoPair() {
        return new TwoPairMatchingStrategy(helper);
    }

    public MatchingStrategy createTrips() {
        return new TripMatchingStrategy(helper);
    }

    public MatchingStrategy createStraight() {
        return new StraightMatchingStrategy(helper);
    }

    public MatchingStrategy createFlush() {
        return new FlushMatchingStrategy(helper);
    }
}
