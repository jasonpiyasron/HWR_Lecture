package hwr.oop.poker;

import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.CommunityCards;
import hwr.oop.poker.community.cards.Flop;
import hwr.oop.poker.community.cards.River;
import hwr.oop.poker.community.cards.Turn;

import java.util.*;
import java.util.stream.Stream;

public class Hand {
    private final Deck deck;
    private final List<Player> players;
    private final BlindConfiguration blindConfiguration;
    private final Map<Player, List<Card>> holeCards;
    private final BettingRound preFlop;
    private final BettingRound flop;
    private final BettingRound turn;
    private final BettingRound river;

    public static Builder newBuilder() {
        return new Builder();
    }

    private Hand(Deck deck, List<Player> players, SmallBlind smallBlind) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = new HashMap<>();
        dealHoleCards();
        this.preFlop = null;
        this.flop = null;
        this.turn = null;
        this.river = null;
    }

    private Hand(Deck deck,
                 List<Player> players,
                 SmallBlind smallBlind,
                 Map<Player, List<Card>> holeCards,
                 BettingRound preFlop,
                 BettingRound flop,
                 BettingRound turn,
                 BettingRound river) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = holeCards;
        this.preFlop = preFlop;
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    private void dealHoleCards() {
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                final List<Card> cards = holeCards.computeIfAbsent(player, p -> new ArrayList<>());
                final Card drawnCard = deck.draw();
                cards.add(drawnCard);
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

    public BettingRound preFlop() {
        return new BettingRound(players);
    }

    public Hand accept(BettingRound preFlopPlayed) {
        return copy()
                .preFlop(preFlopPlayed)
                .build();
    }

    private Builder copy() {
        return newBuilder()
                .deck(deck)
                .players(players)
                .smallBlind(blindConfiguration.smallBlind())
                .holeCards(holeCards)
                .preFlop(preFlop)
                .flop(flop)
                .turn(turn)
                .river(river);
    }

    public boolean preFlopPlayed() {
        return preFlop != null && preFlop.isFinished();
    }

    static class Builder {
        private Deck deck;
        private List<Player> players;
        private SmallBlind smallBlind;
        private Map<Player, List<Card>> holeCards;
        private BettingRound preFlop;
        private BettingRound flop;
        private BettingRound turn;
        private BettingRound river;

        Hand build() {
            final boolean hasIncompleteInfo = Stream.of(holeCards, preFlop).anyMatch(Objects::isNull);
            if (hasIncompleteInfo) {
                return new Hand(deck, players, smallBlind);
            } else {
                return new Hand(deck, players, smallBlind, holeCards, preFlop, flop, turn, river);
            }
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

        Builder holeCards(Map<Player, List<Card>> holeCards) {
            this.holeCards = holeCards;
            return this;
        }

        private Builder() {
            this.deck = null;
            this.players = null;
            this.smallBlind = null;
        }

        public Builder preFlop(BettingRound preFlop) {
            this.preFlop = preFlop;
            return this;
        }

        public Builder flop(BettingRound flop) {
            this.flop = flop;
            return this;
        }

        public Builder turn(BettingRound turn) {
            this.turn = turn;
            return this;
        }

        public Builder river(BettingRound river) {
            this.river = river;
            return this;
        }
    }
}
