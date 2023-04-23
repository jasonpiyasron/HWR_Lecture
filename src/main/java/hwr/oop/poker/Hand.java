package hwr.oop.poker;

import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.CommunityCards;
import hwr.oop.poker.community.cards.Flop;
import hwr.oop.poker.community.cards.River;
import hwr.oop.poker.community.cards.Turn;

import java.util.*;

public class Hand {
    private final Deck deck;
    private final List<Player> players;
    private final Map<Player, List<Card>> holeCards;
    private final BlindConfiguration blindConfiguration;

    public static Builder newBuilder() {
        return new Builder();
    }

    private Hand(Deck deck, List<Player> players, SmallBlind smallBlind) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
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

    public BlindConfiguration blinds() {
        return blindConfiguration;
    }

    public ChipValue podSize() {
        return () -> blindConfiguration.bigBlind().value() + blindConfiguration.smallBlind().value();
    }

    public Optional<Flop> flop() {
        return Optional.empty();
    }

    public Optional<Turn> turn() {
        return Optional.empty();
    }

    public Optional<River> river() {
        return Optional.empty();
    }

    public CommunityCards communityCards() {
        return new CommunityCards();
    }

    static class Builder {
        private Deck deck;
        private List<Player> players;
        private SmallBlind smallBlind;

        Hand build() {
            return new Hand(deck, players, smallBlind);
        }

        Builder deck(Deck deck) {
            this.deck = deck;
            return this;
        }

        Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        Builder smallBlind(SmallBlind smallBlind) {
            this.smallBlind = smallBlind;
            return this;
        }

        private Builder() {
            this.deck = null;
            this.players = null;
            this.smallBlind = null;
        }
    }
}
