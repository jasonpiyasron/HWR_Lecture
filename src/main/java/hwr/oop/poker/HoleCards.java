package hwr.oop.poker;

import java.util.*;

public class HoleCards {

    private final Map<Player, List<Card>> assignment;

    public static HoleCards create(Deck deck, List<Player> players) {
        return new HoleCards(deck, players);
    }

    public HoleCards(Deck deck, List<Player> players) {
        this.assignment = drawHoleCardsFromDeck(deck, players);
    }

    public List<Card> get(Player player) {
        return assignment.get(player);
    }

    private Map<Player, List<Card>> drawHoleCardsFromDeck(Deck deck, List<Player> players) {
        final var mutableMap = drawCardsForPlayers(deck, players);
        forceCardsToBeImmutableLists(mutableMap);
        return Collections.unmodifiableMap(mutableMap);
    }

    private Map<Player, List<Card>> drawCardsForPlayers(Deck deck, List<Player> players) {
        final Map<Player, List<Card>> mutableMap = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                final var cards = mutableMap
                        .computeIfAbsent(player, p -> new ArrayList<>());
                final Card drawnCard = deck.draw();
                cards.add(drawnCard);
            }
        }
        return mutableMap;
    }

    private void forceCardsToBeImmutableLists(Map<Player, List<Card>> mutableMap) {
        for (Map.Entry<Player, List<Card>> entry : mutableMap.entrySet()) {
            final var player = entry.getKey();
            final var cards = entry.getValue();
            final var immutableLIst = List.copyOf(cards);
            mutableMap.put(player, immutableLIst);
        }
    }
}
