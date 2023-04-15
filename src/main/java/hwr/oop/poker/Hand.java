package hwr.oop.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hand {
    private final Deck deck;
    private final List<Player> players;
    private final Map<Player, List<Card>> holeCards;

    public Hand(Deck deck, List<Player> players) {
        this.deck = deck;
        this.players = players;
        this.holeCards = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                final List<Card> cards = holeCards.computeIfAbsent(player, p -> new ArrayList<>());
                cards.add(deck.draw());
            }
        }
    }

    public List<Card> holeCards(Player player) {
        return holeCards.get(player);
    }

    public Player smallBlind() {
        return players.get(0);
    }

    public Player bigBlind() {
        return players.get(1);
    }

    public Player button() {
        return players.get(1);
    }

    public Player underTheGun() {
        return players.get(0);
    }
}
