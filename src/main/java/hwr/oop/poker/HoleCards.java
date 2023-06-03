package hwr.oop.poker;

import java.util.*;

public class HoleCards {

    private final Map<Player, List<Card>> assignment;

    public static HoleCards create(Deck deck, List<Player> players) {
        return new HoleCards(deck, players);
    }

    private HoleCards(Deck deck, List<Player> players) {
        this.assignment = drawHoleCardsFromDeck(deck, players);
    }

    public List<Card> of(Player player) {
        return assignment.get(player);
    }

    private Map<Player, List<Card>> drawHoleCardsFromDeck(Deck deck, List<Player> players) {
        final var mutableMap = drawCardsToMutableMap(deck, players);
        return convertToImmutableMap(mutableMap);
    }

    private Map<Player, List<Card>> drawCardsToMutableMap(Deck deck, List<Player> players) {
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

    private Map<Player, List<Card>> convertToImmutableMap(Map<Player, List<Card>> mutableMap) {
        final var mutableMapOfImmutableLists = convertToImmutableLists(mutableMap);
        return Collections.unmodifiableMap(mutableMapOfImmutableLists);
    }

    private Map<Player, List<Card>> convertToImmutableLists(Map<Player, List<Card>> mutableMap) {
        for (var entry : mutableMap.entrySet()) {
            final var player = entry.getKey();
            final var cards = entry.getValue();
            final var immutableLIst = List.copyOf(cards);
            mutableMap.put(player, immutableLIst);
        }
        return mutableMap;
    }
}
