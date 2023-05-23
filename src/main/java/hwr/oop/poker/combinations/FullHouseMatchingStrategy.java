package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FullHouseMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationDetectionStrategy pair;
    private final CombinationDetectionStrategy trips;

    public FullHouseMatchingStrategy(CombinationDetectionStrategy pair, CombinationDetectionStrategy trips) {
        this.pair = pair;
        this.trips = trips;
    }

    @Override
    public Result match(List<Card> cards) {
        final Result pairResult = pair.match(cards);
        final Result tripResult = trips.match(cards);
        final boolean isFullHouse = (pairResult.successful() && tripResult.successful()) || (tripResult.alternatives().size() > 1);
        if (isFullHouse) {
            final List<List<Card>> candidates = buildAllCandidates(
                    tripResult.alternatives(),
                    pairResult.alternatives()
            );
            return Result.success(Combination.Label.FULL_HOUSE, candidates);
        } else {
            return Result.failure(Combination.Label.FULL_HOUSE);
        }
    }

    private List<List<Card>> buildAllCandidates(List<List<Card>> allTrips, List<List<Card>> allPairs) {
        Stream<List<Card>> result = Stream.empty();
        if (allPairs != null) {
            final var simpleCandidates = allTrips.stream()
                    .map(setCandidate -> buildCandidates(setCandidate, allPairs))
                    .flatMap(Collection::stream);
            result = Stream.concat(result, simpleCandidates);
        }
        final var pairsFromTrips = allTrips.stream()
                .map(candidate -> candidate.subList(0, 2))
                .collect(Collectors.toList());
        final var complexCandidates = allTrips.stream()
                .map(setCandidate -> buildCandidates(setCandidate, pairsFromTrips))
                .flatMap(Collection::stream);
        return Stream.concat(result, complexCandidates)
                .collect(Collectors.toList());
    }

    private List<List<Card>> buildCandidates(List<Card> setCandidate, List<List<Card>> allPairs) {
        return allPairs.stream()
                .map(pairCandidate -> Stream.concat(setCandidate.stream(), pairCandidate.stream()).collect(Collectors.toList()))
                .filter(candidate -> candidate.stream().map(Card::symbol).distinct().count() > 1)
                .collect(Collectors.toList());
    }
}
