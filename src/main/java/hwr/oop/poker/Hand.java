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
    private final BettingRound preFlopBettingRound;
    private final BettingRound flopBettingRound;
    private final BettingRound turnBettingRound;
    private final BettingRound riverBettingRound;

    private final CommunityCards communityCards;

    public static Builder newBuilder() {
        return new Builder();
    }

    private Hand(Deck deck, List<Player> players, SmallBlind smallBlind) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = new HashMap<>();
        dealHoleCards();
        this.preFlopBettingRound = null;
        this.flopBettingRound = null;
        this.turnBettingRound = null;
        this.riverBettingRound = null;
        this.communityCards = CommunityCards.empty();
    }

    private Hand(Deck deck,
                 List<Player> players,
                 SmallBlind smallBlind,
                 Map<Player, List<Card>> holeCards,
                 BettingRound preFlopBettingRound,
                 BettingRound flopBettingRound,
                 BettingRound turnBettingRound,
                 BettingRound riverBettingRound,
                 CommunityCards communityCards) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = holeCards;
        this.preFlopBettingRound = preFlopBettingRound;
        this.flopBettingRound = flopBettingRound;
        this.turnBettingRound = turnBettingRound;
        this.riverBettingRound = riverBettingRound;
        if (roundPlayed(preFlopBettingRound)) {
            deck.burn();
            if (roundPlayed(flopBettingRound)) {
                this.communityCards = CommunityCards
                        .flop(communityCards.flop().orElseThrow())
                        .turn(deck.draw()).noRiver();
            } else {
                this.communityCards = CommunityCards.flop(
                        deck.draw(), deck.draw(), deck.draw()
                ).noTurnNoRiver();
            }
        } else {
            this.communityCards = communityCards;
        }
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
        return communityCards.flop();
    }

    public Optional<Turn> turn() {
        return communityCards.turn();
    }

    public Optional<River> river() {
        return communityCards.river();
    }

    public CommunityCards communityCards() {
        return communityCards;
    }

    public BettingRound preFlop() {
        return new BettingRound(players);
    }

    public Hand accept(BettingRound bettingRoundPlayed) {
        final Builder copy = copy();
        if (preFlopRoundPlayed()) {
            if (flopRoundPlayed()) {
                copy.turnRound(bettingRoundPlayed);
            } else {
                copy.flopRound(bettingRoundPlayed);
            }
        } else {
            copy.preFlopRound(bettingRoundPlayed);
        }
        return copy.build();
    }

    private Builder copy() {
        return newBuilder()
                .deck(deck)
                .players(players)
                .smallBlind(blindConfiguration.smallBlind())
                .holeCards(holeCards)
                .preFlopRound(preFlopBettingRound)
                .flopRound(flopBettingRound)
                .turnRound(turnBettingRound)
                .river(riverBettingRound)
                .communityCards(communityCards);
    }

    public boolean preFlopRoundPlayed() {
        return roundPlayed(preFlopBettingRound);
    }

    public boolean flopRoundPlayed() {
        return roundPlayed(flopBettingRound);
    }

    public boolean turnRoundPlayed() {
        return roundPlayed(turnBettingRound);
    }

    public boolean riverRoundPlayed() {
        return roundPlayed(riverBettingRound);
    }

    private boolean roundPlayed(BettingRound bettingRound) {
        return bettingRound != null && bettingRound.isFinished();
    }

    static class Builder {
        private Deck deck;
        private List<Player> players;
        private SmallBlind smallBlind;
        private Map<Player, List<Card>> holeCards;
        private BettingRound preFlopRound;
        private BettingRound flopRound;
        private BettingRound turnRound;
        private BettingRound riverRound;
        private CommunityCards communityCards;

        private Builder() {
            this.deck = null;
            this.players = null;
            this.smallBlind = null;
            this.holeCards = null;
            this.preFlopRound = null;
            this.turnRound = null;
            this.riverRound = null;
            this.communityCards = null;
        }

        Hand build() {
            final boolean hasIncompleteInfo = Stream.of(holeCards, preFlopRound, communityCards).anyMatch(Objects::isNull);
            if (hasIncompleteInfo) {
                return new Hand(deck, players, smallBlind);
            } else {
                return new Hand(deck, players, smallBlind, holeCards, preFlopRound, flopRound, turnRound, riverRound, communityCards);
            }
        }

        public Builder deck(Deck deck) {
            this.deck = deck;
            return this;
        }

        public Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder smallBlind(SmallBlind smallBlind) {
            this.smallBlind = smallBlind;
            return this;
        }

        private Builder holeCards(Map<Player, List<Card>> holeCards) {
            this.holeCards = holeCards;
            return this;
        }

        public Builder preFlopRound(BettingRound preFlopRound) {
            this.preFlopRound = preFlopRound;
            return this;
        }

        public Builder flopRound(BettingRound flopRound) {
            this.flopRound = flopRound;
            return this;
        }

        public Builder turnRound(BettingRound turnRound) {
            this.turnRound = turnRound;
            return this;
        }

        public Builder river(BettingRound riverRound) {
            this.riverRound = riverRound;
            return this;
        }

        public Builder communityCards(CommunityCards communityCards) {
            this.communityCards = communityCards;
            return this;
        }
    }
}
