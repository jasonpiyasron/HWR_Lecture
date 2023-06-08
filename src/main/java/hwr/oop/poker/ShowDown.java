package hwr.oop.poker;

import hwr.oop.poker.community.cards.CommunityCardsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowDown {

    private final CommunityCardsProvider communityCardsProvider;
    private final HoleCards holeCards;
    private final List<Player> players;

    public static ShowDown create(CommunityCardsProvider communityCardsProvider, HoleCards holeCards, List<Player> players) {
        return new ShowDown(communityCardsProvider, holeCards, players);
    }

    private ShowDown(CommunityCardsProvider communityCardsProvider, HoleCards holeCards, List<Player> players) {
        this.communityCardsProvider = communityCardsProvider;
        this.holeCards = holeCards;
        this.players = players;
    }

    public Combination combination(Player player) {
        final var playersHoleCards = this.holeCards.of(player);
        final var communityCards = communityCardsProvider.cardsDealt();
        final var allCards = Stream.concat(playersHoleCards.stream(), communityCards.stream())
                .collect(Collectors.toList());
        return Combination.of(allCards);
    }

    public Player winner() {
        final Map<Player, Combination> combinationMap = players.stream()
                .collect(Collectors.toMap(
                        p -> p,
                        p -> combination(p))
                );
        return players.get(0);
    }
}
